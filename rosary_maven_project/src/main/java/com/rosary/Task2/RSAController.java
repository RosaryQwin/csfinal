package com.rosary.Task2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class RSAController {

    // Handle POST request for signing and verifying new records
    @PostMapping("/b")  // This matches your form: <form action="/b" method="post">
    @ResponseBody
    public String signMessage(@RequestParam String item_units,
                               @RequestParam String item_id,
                               @RequestParam String item_price,
                               @RequestParam String send_inventory_id) throws Exception {

        // Load the sender's (inventory's) RSA key pair
        BigInteger[] senderKeys = KeyLoader.loadKeys(send_inventory_id, "src/main/resources/keys.json");
        RSA senderRSA = new RSA(senderKeys[0], senderKeys[1], senderKeys[2]);

        // Create the new inventory record message
        String message = item_id + " " + item_units + " " + item_price + " " + send_inventory_id;

        // Signing process: hash the message and encrypt it using sender's private key
        BigInteger senderHash = senderRSA.hashMessage(message);
        BigInteger signature = senderHash.modPow(senderRSA.d, senderRSA.n);

        // Load all inventories' keys for verification
        Map<String, BigInteger[]> allKeys = KeyLoader.loadAllKeys("src/main/resources/keys.json");
        List<String> voters = new ArrayList<>(allKeys.keySet());
        voters.remove(send_inventory_id); // Remove sender from voters list (no self-verification)

        // Verification: other inventories verify the signature
        int approvals = 0; // Count approvals
        StringBuilder verificationSteps = new StringBuilder(); // Store detailed steps for HTML

        for (String voterId : voters) {
            // Always use sender's public key for verifying signature
            BigInteger[] voterKeys = allKeys.get(send_inventory_id);
            RSA voterRSA = new RSA(voterKeys[0], voterKeys[1], voterKeys[2]);

            // Each voter hashes the message and decrypts the signature
            BigInteger voterHash = voterRSA.hashMessage(message);
            BigInteger decryptedSignature = signature.modPow(voterRSA.e, voterRSA.n); // This will return original Hassh

            // Compare the two hashes to verify the signature
            boolean isValid = voterHash.equals(decryptedSignature);

            if (isValid) approvals++; // Increment approvals if verified successfully

            // Append step-by-step verification process for each warehouse
            verificationSteps.append("<div class='verification-box'>")
                .append("<h3>Warehouse ").append(voterId).append(" Verification</h3>")
                .append("<p><strong>Step 1:</strong> Hash the message → <code>").append(voterHash.toString(16)).append("</code></p>")
                .append("<p><strong>Step 2:</strong> Decrypt signature using public key → <code>").append(decryptedSignature.toString(16)).append("</code></p>")
                .append("<p><strong>Step 3:</strong> Compare hashes → ").append(isValid ? "✅ Match" : "❌ Mismatch").append("</p>")
                .append("<p><strong>Vote:</strong> ").append(isValid ? "✅ Approve" : "❌ Reject").append("</p>")
                .append("</div><br>");
        }

        // Consensus: require majority (at least 2/3 approvals) for block acceptance
        boolean consensus = approvals >= 2;
        if (consensus) {
            // Save the record to inventory ledger if consensus is achieved
            // RSA.save_record_json(item_id, item_units, item_price, send_inventory_id);
            RSA.save_record_json_to_all(item_id, item_units, item_price,send_inventory_id);

        }

        // Build and return detailed HTML response
        return "<!DOCTYPE html>" +
            "<html lang='en'>" +
            "<head>" +
            "<meta charset='UTF-8'>" +
            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "<title>RSA Digital Signature Result</title>" +
            "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;600&display=swap' rel='stylesheet'>" +
            "<link rel='stylesheet' href='/signer1.css'>" +
            "</head>" +
            "<body>" +
            "<div class='layout'>" +
                getFormHtml() +
                "<div class='container result-container'>" +
                    "<h2>📦 Inventory Blockchain Signing Result</h2>" +
                    "<div class='basic'>" +
                        "<h3>🔹 Message Details</h3>" +
                        "<p><strong>Item ID:</strong> " + item_id + "</p>" +
                        "<p><strong>Item Units:</strong> " + item_units + "</p>" +
                        "<p><strong>Item Price:</strong> " + item_price + "</p>" +
                        "<p><strong>Inventory:</strong> " + send_inventory_id + "</p>" +
                        "<hr>" +
                        "<h3>🔐 RSA Keys Used by Warehouse " + send_inventory_id + "</h3>" +
                        "<p><strong>Public Key (e, n):</strong><code>(" + senderRSA.e + ", " + senderRSA.n + ")</code></p>" +
                        "<p><strong>Private Key (d, n):</strong><code>(" + senderRSA.d + ", " + senderRSA.n + ")</code></p>" +
                        "<hr>" +
                        "<h3>✍️ Signing by Warehouse " + send_inventory_id + "</h3>" +
                        "<p><strong>Step 1:</strong> Hash the message → <code>" + senderHash.toString(16) + "</code></p>" +
                        "<p><strong>Step 2:</strong> Encrypt the hash with private key → <code>" + signature.toString(16) + "</code></p>" +
                        "<hr>" +
                        "<h2>🛡 Verification by Other Warehouses</h2>" +
                        verificationSteps.toString() +
                        "<hr>" +
                        "<h2>🏁 Final Consensus</h2>" +
                        "<p><strong>Result:</strong> " + (consensus ? "✅ Block Accepted" : "❌ Block Rejected") + "</p>" +
                        "<p><strong>Approvals:</strong> " + approvals + " / 3</p>" +
                    "</div>" +
                "</div>" +
            "</div>" +
            "</body>" +
            "</html>";
    }

    // Helper method to generate the form HTML for adding a new record
    private String getFormHtml() {
        return "<div class='main-link-wrapper'>" +
            "<a href='main.html' class='main-link'>← Back to Main Page</a>" +
            "<h1><u>TASK1 AND 2</u></h1>" +
            "</div>" +
            "<div class='container'>" +
                "<h2 class='box'>➕ Add New Record</h2>" +
                "<form action='/b' method='post'>" + // Form posts back to /b
                    "<label class='basic'>Item ID:</label>" +
                    "<input type='text' name='item_id' required />" +
                    "<label class='basic'>Item QTY:</label>" +
                    "<input type='text' name='item_units' required />" +
                    "<label class='basic'>Item Price:</label>" +
                    "<input type='text' name='item_price' required />" +
                    "<label class='basic'>Location:</label>" +
                    "<select name='send_inventory_id' required>" +
                        "<option value=''>-- Select Inventory --</option>" +
                        "<option value='A'>Inventory A</option>" +
                        "<option value='B'>Inventory B</option>" +
                        "<option value='C'>Inventory C</option>" +
                        "<option value='D'>Inventory D</option>" +
                    "</select>" +
                    "<button type='submit'>Sign</button>" +
                "</form>" +
            "</div>";
    }
}
