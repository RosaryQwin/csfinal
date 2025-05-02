package com.rosary.Task2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;


import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class KeyLoader {
    public static BigInteger[] loadKeys(String inventoryId, String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filePath));
        JsonNode node = root.get(inventoryId);

        if (node == null) throw new Exception("Inventory ID not found");

        BigInteger p = new BigInteger(node.get("p").asText());
        BigInteger q = new BigInteger(node.get("q").asText());
        BigInteger e = new BigInteger(node.get("e").asText());

        return new BigInteger[]{p, q, e};
    }

 // Load all keys as a map: { A -> [p, q, e], B -> [...] }
public static Map<String, BigInteger[]> loadAllKeys(String filePath) throws Exception {
     Map<String, BigInteger[]> keyMap = new HashMap<>();

    ObjectMapper mapper = new ObjectMapper();
    JsonNode root = mapper.readTree(new File(filePath));

    Iterator<String> fieldNames = root.fieldNames();
    while (fieldNames.hasNext()) {
        String id = fieldNames.next();
        JsonNode node = root.get(id);

        BigInteger p = new BigInteger(node.get("p").asText());
        BigInteger q = new BigInteger(node.get("q").asText());
        BigInteger e = new BigInteger(node.get("e").asText());

        keyMap.put(id, new BigInteger[]{p, q, e});
    }

        return keyMap;
    }
}
