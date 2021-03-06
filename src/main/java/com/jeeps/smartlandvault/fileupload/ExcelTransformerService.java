package com.jeeps.smartlandvault.fileupload;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.jeeps.smartlandvault.exceptions.IncorrectExcelFormatException;
import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerRepository;
import com.jeeps.smartlandvault.sql.inventory.ContainerInventory;
import com.jeeps.smartlandvault.sql.inventory.ContainerInventoryRepository;
import com.jeeps.smartlandvault.sql.item.Item;
import com.jeeps.smartlandvault.sql.item.ItemRepository;
import com.jeeps.smartlandvault.util.ExcelSheetReader;
import com.jeeps.smartlandvault.util.GenericJsonMapper;
import com.jeeps.smartlandvault.util.InventoryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.jeeps.smartlandvault.util.GenericJsonMapper.getPropertiesFromTree;
import static com.jeeps.smartlandvault.util.InventoryHelper.createItemsFromProperties;

@Service
public class ExcelTransformerService {
    private static Logger logger = LoggerFactory.getLogger(ExcelTransformerService.class.getName());

    @Autowired
    private DataContainerRepository dataContainerRepository;
    @Autowired
    private ContainerInventoryRepository containerInventoryRepository;
    @Autowired
    private ItemRepository itemRepository;

    public void transform(InputStream inputStream, DataContainer dataContainer, String extension) throws IncorrectExcelFormatException {
        // Get Table data
        try {
            ExcelTableData excelTableData = ExcelSheetReader.parseWorkBook(inputStream, extension);
            // Save data container
            dataContainer.setOriginType(DataContainer.ORIGIN_EXCEL);

            Gson gson = new Gson();
            String tableAsJson = gson.toJson(excelTableData.getColumns());
            dataContainer.setData(GenericJsonMapper.convertFromJsonArray(tableAsJson));

            // Save container inventory
            // Get existing inventory if it has one
            ContainerInventory containerInventory =
                    containerInventoryRepository.findByContainerId(dataContainer.getId())
                        .orElse(new ContainerInventory(
                                dataContainer.getId(),
                                excelTableData.getName(),
                                "/"));
            // Update parameters
            containerInventory.setName(excelTableData.getName());
            // Delete items if they exist
            if (containerInventory.getItems() != null)
                itemRepository.deleteAllByContainerInventory(containerInventory);
            // Save new items
            List<Item> items = excelTableData.getItemsMetadata();
            if (items != null) {
                items.forEach(item -> item.setContainerInventory(containerInventory));
            } else {
                // Create metadata properties from data
                Map<String, String> properties =
                        getPropertiesFromTree((LinkedTreeMap<Object, Object>) dataContainer.getData().get(0));
                items = createItemsFromProperties(properties, containerInventory);
            }
            containerInventory.setItems(items);
            containerInventoryRepository.save(containerInventory);

            dataContainer.setMetadata(InventoryHelper.getMetadataFromInventory(containerInventory));
            dataContainerRepository.save(dataContainer);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
