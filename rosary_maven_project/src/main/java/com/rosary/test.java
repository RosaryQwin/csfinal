package com.rosary;

import com.rosary.Task3.PKG;
import com.rosary.Task3.Task3_KeyLoader;
import java.math.BigInteger;

public class test {
    public static void main(String[] args) throws Exception {
        String item_units = "12";

        // Load keys
        BigInteger p = Task3_KeyLoader.loadPKGValue("p");
        BigInteger q = Task3_KeyLoader.loadPKGValue("q");
        BigInteger e = Task3_KeyLoader.loadPKGValue("e");
        BigInteger n = p.multiply(q);

        // Create PKG instance
        PKG pkg = new PKG(p, q, e);

        // Message
        BigInteger m = new BigInteger(item_units);

        // Load Inventory IDs
        BigInteger idA = Task3_KeyLoader.loadInventoryID("A");
        BigInteger idB = Task3_KeyLoader.loadInventoryID("B");

        // Get private exponent d
        BigInteger d = pkg.getPrivateExponent();

        // First create RSA signature
        BigInteger rsaSign = m.modPow(d, n);

        // Partial signatures
        BigInteger partialA = rsaSign.modPow(idA, n);
        BigInteger partialB = rsaSign.modPow(idB, n);

        // Combine signatures
        BigInteger combinedSignature = partialA.multiply(partialB).mod(n);

        // idProduct
        BigInteger idProduct = idA.multiply(idB);

        // Verification
        BigInteger left = combinedSignature.modPow(e, n);
        BigInteger right = m.modPow(idProduct, n);

        System.out.println("\nCombined Signature:");
        System.out.println(combinedSignature);

        System.out.println("\nVerification Result:");
        if (left.equals(right)) {
            System.out.println("✅ Signature is VALID!");
        } else {
            System.out.println("❌ Signature is INVALID!");
        }

        // Debug Info
        System.out.println("\n--- Debug Info ---");
        System.out.println("p: " + p);
        System.out.println("q: " + q);
        System.out.println("n: " + n);
        System.out.println("e: " + e);
        System.out.println("d: " + d);
        System.out.println("m (Message): " + m);
        System.out.println("ID A: " + idA);
        System.out.println("ID B: " + idB);
        System.out.println("ID Product: " + idProduct);
        System.out.println("Left (Combined^e mod n): " + left);
        System.out.println("Right (m^idProduct mod n): " + right);
    }
}
