package com.rosary.Task3;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.rosary.Task2.RSA;
import com.rosary.Task3.Task3_KeyLoader.InventoryData;

@Controller
public class Task33 {

    @PostMapping("/Task3-Result")
    @ResponseBody
    public String searchmessage(@RequestParam String item_id) throws Exception {
        // Load inventory metadata (ID and random value for each signer)
        Map<String, InventoryData> inventories = Task3_KeyLoader.loadAllInventories();
        int numSigners = inventories.size();

        // Load item quantities per inventory and extract inventory IDs
        String[] itemQtys = new String[numSigners];
        String[] inventoryIds = inventories.keySet().toArray(new String[0]);

        for (int i = 0; i < numSigners; i++) {
            itemQtys[i] = InvenLoader.Get_ItemQty(inventoryIds[i], item_id);
        }

        // Load cryptographic parameters and initialize the PKG (Private Key Generator)
        PKG pkg = new PKG(
            Task3_KeyLoader.loadPKGValue("p"),
            Task3_KeyLoader.loadPKGValue("q"),
            Task3_KeyLoader.loadPKGValue("e")
        );

        // Arrays to store identity, randomness, secret/public key, and partial signatures
        BigInteger[] ids = new BigInteger[numSigners];
        BigInteger[] rjs = new BigInteger[numSigners];
        BigInteger[] gjs = new BigInteger[numSigners];  // Secret key
        BigInteger[] tjs = new BigInteger[numSigners];  // Public t_j = r_j^e
        BigInteger[] sjs = new BigInteger[numSigners];  // Partial signatures

        // Extract ID and random value from each inventory
        for (int i = 0; i < numSigners; i++) {
            InventoryData inv = inventories.get(inventoryIds[i]);
            ids[i] = inv.id;
            rjs[i] = inv.random;
        }

        // Generate g_j = id^d and t_j = r_j^e for each signer
        for (int i = 0; i < numSigners; i++) {
            gjs[i] = pkg.generateSecretKey(ids[i]);
            tjs[i] = pkg.generateTJ(rjs[i]);
        }


        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String qty : itemQtys) {
            if (qty != null && !qty.isEmpty()) {
                frequencyMap.put(qty, frequencyMap.getOrDefault(qty, 0) + 1);
            }
        }
        
        // Find the most frequent quantity with safe fallback (prefer smaller qty on tie)
        String messageStr = "0";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            String currentQty = entry.getKey();
            int count = entry.getValue();
        
            if (count > maxCount) {
                messageStr = currentQty;
                maxCount = count;
            } else if (count == maxCount) {
                // Tie breaker: choose the smaller numeric quantity
                if (new BigInteger(currentQty).compareTo(new BigInteger(messageStr)) < 0) {
                    messageStr = currentQty;
                }
            }
        }
        // Aggregate all t_j values into one global T
        BigInteger t = pkg.AggregatedT(tjs);

        // ✅ Use global messageStr for all signers
        for (int i = 0; i < numSigners; i++) {
            sjs[i] = pkg.generateSJ(gjs[i], rjs[i], t, messageStr);
        }
        // Aggregate all s_j values to get final global signature S
        BigInteger s = pkg.aggregateSignatures(sjs);

        // Compute global hash H(t || m) and verify S using it
        BigInteger hash = PKG.computeMd5OfTConcatM(t, messageStr);
        boolean valid = pkg.verifySignature(s, ids, t, messageStr, hash);
        

        //User
        Task3_RSA rsa = new Task3_RSA(Task3_KeyLoader.loadOfficerValue("p"),Task3_KeyLoader.loadOfficerValue("q"),Task3_KeyLoader.loadOfficerValue("e"));
        BigInteger encryptedMessage = rsa.encryptmsg(messageStr);
        System.out.println("Encreypted msg " + encryptedMessage);
        String decryptedmessage = rsa.decryptmsg(encryptedMessage).toString();
        boolean isValid = rsa.verifyMultiSignature(s,ids,t,decryptedmessage,pkg);
        System.out.println("Original messageStr: " + messageStr);
        System.out.println("Decrypted message: " + decryptedmessage);
        
        // ========== Begin HTML Output ==========

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head>")
            .append("<meta charset='UTF-8'>")
            .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
            .append("<title>Task 3 - Multi-Signature</title>")
            .append("<link rel='stylesheet' href='/task3.css'>")
            .append("</head><body><div class='layout'>")

            .append("<div class='main-link-wrapper'>")
            .append("<a href='main.html' class='main-link'>← Back to Main Page</a>")
            .append("</div>")

            .append("<h1 class='title'>🖋 Harn Identity-Based Multi-Signature</h1>")
            .append("<div class='container result-container'>")

            // Item summary box
            .append("<div class='box'><h2>📦 Item Info</h2>")
            .append("<p><strong>Item ID:</strong> ").append(item_id).append("</p>")
            .append("<p><strong>Global Message (Qty):</strong> ").append(messageStr).append("</p>")
            .append("</div>");

        // Loop through each inventory to display its computed values and local verification
        for (int i = 0; i < numSigners; i++) {
            String localM = (itemQtys[i] != null && !itemQtys[i].isEmpty()) ? itemQtys[i] : "0";
            // BigInteger m = new BigInteger(localM);
            BigInteger localHash = PKG.computeMd5OfTConcatM(t, localM);

            // Recompute the expected RHS for this inventory’s verification
            BigInteger lhs = s.modPow(pkg.getPublicExponent(), pkg.getModulus());
            BigInteger tPowerH = t.modPow(localHash, pkg.getModulus());

            // ∏ ID_j mod n
            BigInteger idProduct = BigInteger.ONE;
            for (BigInteger id : ids) {
                idProduct = idProduct.multiply(id).mod(pkg.getModulus());
            }

            // Right-hand side of verification equation
            BigInteger rhs = idProduct.multiply(tPowerH).mod(pkg.getModulus());
            boolean consistent = lhs.equals(rhs);

            

            // Output each inventory’s values and verification result
            html.append("<div class='box'>")
                .append("<h2>🏬 Inventory ").append(inventoryIds[i]).append("</h2>")
                .append("<p><strong>Local Qty (mⱼ):</strong> ").append(localM).append("</p>")
                .append("<p><strong>gⱼ:</strong></p><code>").append(gjs[i]).append("</code>")
                .append("<p><strong>tⱼ:</strong></p><code>").append(tjs[i]).append("</code>")
                .append("<p><strong>sⱼ:</strong></p><code>").append(sjs[i]).append("</code>")

                .append("<h3>🔍 Local Aggregated Signature Verification</h3>")
                .append("<p><strong>Formula:</strong> S<sup>e</sup> ≟ ∏IDⱼ × T<sup>H(t‖mⱼ)</sup> mod n</p>")
                .append("<p><strong>S<sup>e</sup>:</strong></p><code>").append(lhs).append("</code>")
                .append("<p><strong>T<sup>H(t‖mⱼ)</sup>:</strong></p><code>").append(tPowerH).append("</code>")
                .append("<p><strong>∏IDⱼ:</strong></p><code>").append(idProduct).append("</code>")
                .append("<p><strong>Expected RHS:</strong></p><code>").append(rhs).append("</code>")
                .append("<p><strong>Verification:</strong> ")
                .append(consistent ? "✅ VALID" : "❌ INVALID")
                .append("</p></div>");
        }

        // Aggregated T and S display
        html.append("<div class='box'>")
            .append("<h2>🧮 Aggregated Values</h2>")
            .append("<p><strong>T (Aggregated):</strong></p><code>").append(t).append("</code>")
            .append("<p><strong>S (Signature):</strong></p><code>").append(s).append("</code>")
            .append("<h2>🔐 Global Signature Verification</h2>")
            .append("<p><strong>Global Hash H(t‖").append(messageStr).append("):</strong></p><code>").append(hash).append("</code>")
            .append("<h2>").append(valid ? "✅ Signature is VALID" : "❌ Signature is INVALID").append("</h2>")
            .append("</div>");

            html.append("<div class='box'>")
            .append("<h2>📤 Encrypted Response Delivery & Verification</h2>")
        
            .append("<h3>🔐 Encrypted Quantity (RSA)</h3>")
            .append("<p><strong>Original Message:</strong> ").append(messageStr).append("</p>")
            .append("<p><strong>Encrypted:</strong></p><code>").append(encryptedMessage).append("</code>")
            .append("<p><strong>Decrypted:</strong></p><code>").append(decryptedmessage).append("</code>")
        
            .append("<h3>📎 RSA Parameters (Officer)</h3>")
            .append("<p><strong>Public Key (e):</strong></p><code>").append(rsa.e).append("</code>")
            .append("<p><strong>Modulus (n):</strong></p><code>").append(rsa.n).append("</code>")
        
            .append("<h3>🧠 Multi-Signature Verification (Using PKG)</h3>")
            .append("<p><strong>Hash H(t‖m):</strong></p><code>").append(hash).append("</code>")
            .append("<p><strong>Aggregated T:</strong></p><code>").append(t).append("</code>")
            .append("<p><strong>Aggregated Signature S:</strong></p><code>").append(s).append("</code>")
        
            // 🧮 Comparison Block
            .append("<h3>🧮 Signature LHS vs RHS Check</h3>")
            .append("<p><strong>LHS (S<sup>e</sup> mod n):</strong></p><code>")
            .append(s.modPow(pkg.getPublicExponent(), pkg.getModulus())).append("</code>")
            .append("<p><strong>RHS (∏IDⱼ × T<sup>H(t‖m)</sup> mod n):</strong></p><code>")
            .append(getRHSString(ids, t, hash, pkg.getModulus())).append("</code>")
        
            .append("<h2>")
            .append(isValid ? "✅ Signature Verification SUCCESSFUL" : "❌ Signature is INVALID")
            .append("</h2>")
            .append("</div>")
            .append("</div></div></body></html>");



        return html.toString(); // Done! HTML returned as response
    }

    private String getRHSString(BigInteger[] ids, BigInteger t, BigInteger hash, BigInteger modulus) {
        BigInteger idProduct = BigInteger.ONE;
        for (BigInteger id : ids) {
            idProduct = idProduct.multiply(id).mod(modulus);
        }
        BigInteger tPowerH = t.modPow(hash, modulus);
        BigInteger rhs = idProduct.multiply(tPowerH).mod(modulus);
        return rhs.toString();
    }

}
