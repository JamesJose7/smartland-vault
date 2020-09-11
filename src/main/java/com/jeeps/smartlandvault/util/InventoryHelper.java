package com.jeeps.smartlandvault.util;

import com.jeeps.smartlandvault.nosql.metadata.Metadata;
import com.jeeps.smartlandvault.sql.item.Item;
import com.jeeps.smartlandvault.sql.inventory.ContainerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryHelper {

    public static List<Item> createItemsFromProperties(Map<String, String> properties, ContainerInventory containerInventory) {
        return properties.entrySet().stream()
                .map(entry -> new Item(containerInventory, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public static List<Metadata> getMetadataFromInventory(ContainerInventory containerInventory) {
        if (containerInventory.getItems() == null)
            return new ArrayList<>();
        return containerInventory.getItems().stream()
                .map(Metadata::new).collect(Collectors.toList());
    }
}
