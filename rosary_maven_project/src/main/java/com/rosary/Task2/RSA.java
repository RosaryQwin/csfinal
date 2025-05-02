package com.rosary.Task2;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class RSA {
    public BigInteger n, e, d;
    // genearate key pair
    public RSA(BigInteger p, BigInteger q, BigInteger e) {
        this.e = e;
        this.n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        this.d = e.modInverse(phi);
    }
    // Inventory sign the record, Task 1 part2
    public BigInteger sign(String message) throws Exception {
        BigInteger hash = hashMessage(message);
        return hash.modPow(d, n);
    }
    // taking public key to decrypt sign to get message and compare, task 1 part3
    public boolean verify(String message, BigInteger signature) throws Exception {
        BigInteger originalHash = hashMessage(message);
        BigInteger decryptedHash = signature.modPow(e, n); // E Is public key
        return originalHash.equals(decryptedHash);
    }

    public BigInteger hashMessage(String message) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(message.getBytes("UTF-8"));
        return new BigInteger(1, hash);
    }

public static void save_record_json(String itemid, String itemqty, String itemprice, String inventoryId, String Location) {
    try {
        String filename = "src/main/resources/data/" + inventoryId + "_ledger.json";
        File file = new File(filename);
        file.getParentFile().mkdirs();

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> records;

        if (file.exists()) {
            records = mapper.readValue(file, new TypeReference<List<Map<String, String>>>(){});
        } else {
            records = new ArrayList<>();
        }

        // Use LinkedHashMap to preserve field order
        Map<String, String> record = new LinkedHashMap<>();
        record.put("item_id", itemid);
        record.put("item_qty", itemqty);
        record.put("item_price", itemprice);
        record.put("item_Location", Location);

        records.add(record);

        mapper.writerWithDefaultPrettyPrinter().writeValue(file, records);

    } catch (IOException e) {
        e.printStackTrace();
    }
}


    public static void save_record_json_to_all(String itemid, String itemqty, String itemprice, String Location) {
        String keyFilePath = "src/main/resources/Keys.json";
        try {
            // Load all keys to get inventory IDs (A, B, C, D, etc.)
            Map<String, BigInteger[]> allKeys = KeyLoader.loadAllKeys(keyFilePath);
    
            // Loop through each inventory and save the same record
            for (String inventoryId : allKeys.keySet()) {
                save_record_json(itemid, itemqty, itemprice, inventoryId, Location);
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
