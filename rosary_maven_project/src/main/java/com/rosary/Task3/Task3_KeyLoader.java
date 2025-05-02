package com.rosary.Task3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Task3_KeyLoader {

    public static class InventoryData {
        public final BigInteger id;
        public final BigInteger random;

        public InventoryData(BigInteger id, BigInteger random) {
            this.id = id;
            this.random = random;
        }
    }

    static String filePath = "src/main/resources/Task3_Keys.json";

    public static BigInteger loadPKGValue(String key) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filePath));
        JsonNode pkgNode = root.get("PKG");

        if (pkgNode == null) throw new Exception("PKG section not found");

        return new BigInteger(pkgNode.get(key).asText());
    }

    public static BigInteger loadOfficerValue(String key) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filePath));
        JsonNode officerNode = root.get("ProcurementOfficer");

        if (officerNode == null) throw new Exception("ProcurementOfficer section not found");

        return new BigInteger(officerNode.get(key).asText());
    }

    public static BigInteger loadInventoryID(String inventoryName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filePath));
        JsonNode inventoryNode = root.get("Inventories").get(inventoryName);

        if (inventoryNode == null) throw new Exception("Inventory " + inventoryName + " not found");

        return new BigInteger(inventoryNode.get("ID").asText());
    }

    public static BigInteger loadInventoryRandomValue(String inventoryName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filePath));
        JsonNode inventoryNode = root.get("Inventories").get(inventoryName);

        if (inventoryNode == null) throw new Exception("Inventory " + inventoryName + " not found");

        return new BigInteger(inventoryNode.get("RandomValue").asText());
    }

    // ✅ Load all inventories dynamically
    public static Map<String, InventoryData> loadAllInventories() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(filePath));
        JsonNode inventoriesNode = root.get("Inventories");

        if (inventoriesNode == null) throw new Exception("Inventories section not found");

        Map<String, InventoryData> inventoryMap = new HashMap<>();
        Iterator<String> fieldNames = inventoriesNode.fieldNames();

        while (fieldNames.hasNext()) {
            String inventoryName = fieldNames.next();
            JsonNode node = inventoriesNode.get(inventoryName);

            BigInteger id = new BigInteger(node.get("ID").asText());
            BigInteger random = new BigInteger(node.get("RandomValue").asText());

            inventoryMap.put(inventoryName, new InventoryData(id, random));
        }

        return inventoryMap;
    }
}
