package com.rosary;

import java.util.Map;

import com.rosary.Task3.InvenLoader;
import com.rosary.Task3.Task3_KeyLoader;
import com.rosary.Task3.Task3_KeyLoader.InventoryData;

public class newtest {
    public static void main(String[] args) throws Exception {
        String itemId = "1";
        Map<String, InventoryData> inventories = Task3_KeyLoader.loadAllInventories();

        int numInventories = inventories.size();
        String[] itemQtys = new String[numInventories];
        String[] inventoryIds = inventories.keySet().toArray(new String[0]);

        for (int i = 0; i < numInventories; i++) {
            String invId = inventoryIds[i];
            itemQtys[i] = InvenLoader.Get_ItemQty(invId, itemId);
        }

        System.out.println("🔍 Item Quantities for Item ID = " + itemId);
        System.out.println("------------------------------------------------");
        for (int i = 0; i < numInventories; i++) {
            String qty = (itemQtys[i] != null && !itemQtys[i].isEmpty()) ? itemQtys[i] : "not found";
            System.out.println("Inventory " + inventoryIds[i] + " ➜ Qty: " + qty);
        }
    }
}
