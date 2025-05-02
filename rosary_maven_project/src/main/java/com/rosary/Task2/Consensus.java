

package com.rosary.Task2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Consensus {

    // Simulate voting by other inventories
    public static boolean verifyWithMajority(String senderId, String message, BigInteger signature) throws Exception {
        Map<String, BigInteger[]> allKeys = KeyLoader.loadAllKeys("src/main/resources/keys.json");
        List<String> voters = new ArrayList<>(allKeys.keySet());

        // Remove sender from voters (they don't vote on their own block)
        voters.remove(senderId);

        // int approvals = 0;
        int approvals = 0;

        for (String voterId : voters) {
            try {
                BigInteger[] keys = allKeys.get(senderId); // Always use the sender's public key
                RSA rsa = new RSA(keys[0], keys[1], keys[2]); // Their p, q, e, to get key pair
                if (rsa.verify(message, signature)) {
                    approvals++;
                    
                }
            } catch (Exception e) {
                // Do nothing for this voter
            }
        }

        // Require at least 2 out of 3 (majority)
        return approvals >= 2;
    }


}
