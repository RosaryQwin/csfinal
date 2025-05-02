package com.rosary.Task3;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;

public class InvenLoader {

    public static String Get_ItemQty(String inventoryId, String itemid){
        try {
            String itemqty = "";
            String filename = "src/main/resources/data/" + inventoryId + "_ledger.json";
            File file = new File(filename);

            //Check if the ledger exist
            if(!file.exists()){
                System.out.println("Ledger Not Found");
                return null;
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, String>> records = mapper.readValue(file, new TypeReference<List<Map<String, String>>>(){});

            for (Map<String, String> record : records) {
                if (record.get("item_id").equals(itemid)) {
                    return record.get("item_qty"); // Return only the quantity
                }
            }
            return itemqty;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
