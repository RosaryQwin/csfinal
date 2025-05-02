// package com.rosary.Task3;

// import java.math.BigInteger;
// import java.util.Map;
// import com.rosary.Task3.Task3_KeyLoader.InventoryData;

// public class realtest {
//     public static void main(String[] args) throws Exception {
//         // Step 1: Load PKG parameters
//         PKG pkg = new PKG(
//             Task3_KeyLoader.loadPKGValue("p"),
//             Task3_KeyLoader.loadPKGValue("q"),
//             Task3_KeyLoader.loadPKGValue("e")
//         );

//         // Step 2: Load inventory nodes
//         Map<String, InventoryData> inventories = Task3_KeyLoader.loadAllInventories();
//         int numSigners = inventories.size();

//         // Step 3: Define the message
//         String message = "2";

//         // Step 4: Initialize arrays
//         BigInteger[] ids = new BigInteger[numSigners];
//         BigInteger[] rjs = new BigInteger[numSigners];
//         BigInteger[] tjs = new BigInteger[numSigners];
//         BigInteger[] gjs = new BigInteger[numSigners];
//         BigInteger[] sjs = new BigInteger[numSigners];

//         int i = 0;
//         for (Map.Entry<String, InventoryData> entry : inventories.entrySet()) {
//             InventoryData inv = entry.getValue();
//             ids[i] = inv.id;
//             rjs[i] = inv.random;
//             i++;
//         }

//         // Step 5: Generate secret keys and tjs
//         for (i = 0; i < numSigners; i++) {
//             gjs[i] = pkg.generateSecretKey(ids[i]);
//             tjs[i] = pkg.generateTJ(rjs[i]);
//             System.out.println("g" + (i+1) + " = " + gjs[i]);
//             System.out.println("t" + (i+1) + " = " + tjs[i]);
//         }

//         // Step 6: Aggregate T
//         BigInteger t = pkg.AggregatedT(tjs);
//         System.out.println("✅ Aggregated T = " + t);

//         // Step 7: Hash H(t || m) using MD5
//         BigInteger hashValue = pkg.computeMd5OfTConcatM(t, message);
//         System.out.println("✅ H(t || m) = " + hashValue);
//         System.out.println("✅ H(t || m) HEX = " + pkg.md5ToHex(t.toString() + message));

//         // Step 8: Generate sj = gj * rj^H mod n
//         for (i = 0; i < numSigners; i++) {
//             sjs[i] = pkg.generateSJ(gjs[i], rjs[i], t, message);
//             System.out.println("s" + (i+1) + " = " + sjs[i]);
//         }

//         // Step 9: Aggregate signature s
//         BigInteger s = pkg.aggregateSignatures(sjs);
//         System.out.println("✅ Final Signature: (T, S) = (" + t + ", " + s + ")");

//         // Step 10: Use PKG's verifySignature method
//         boolean valid = pkg.verifySignature(s, ids, t, new BigInteger(message.getBytes()), hashValue);
//         System.out.println(valid ? "✅ Signature is VALID" : "❌ Signature is INVALID");

//     }
// }
