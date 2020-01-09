package com.jeeps.smartlandvault.util;

import com.jeeps.smartlandvault.sql.container_stock.Item;
import com.jeeps.smartlandvault.sql.sorted_containers.ContainerInventory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryHelper {

    public static List<Item> createItemsFromProperties(Map<String, String> properties, ContainerInventory containerInventory) {
        return properties.entrySet().stream()
                .map(entry -> new Item(containerInventory, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
