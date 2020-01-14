package com.jeeps.smartlandvault.fileupload;

import com.google.gson.Gson;
import com.jeeps.smartlandvault.exceptions.IncorrectExcelFormatException;
import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerRepository;
import com.jeeps.smartlandvault.sql.inventory.ContainerInventoryRepository;
import com.jeeps.smartlandvault.util.ExcelSheetReader;
import com.jeeps.smartlandvault.util.GenericJsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

@Service
public class ExcelTransformerService {
    private static Logger logger = LoggerFactory.getLogger(ExcelTransformerService.class.getName());

    @Autowired
    private DataContainerRepository dataContainerRepository;
    @Autowired
    private ContainerInventoryRepository containerInventoryRepository;

    public void transform(InputStream inputStream, String id, String name) throws IncorrectExcelFormatException {
        // Get Table data
        try {
            ExcelTableData excelTableData = ExcelSheetReader.parseWorkBook(inputStream);
            DataContainer dataContainer = new DataContainer(id == null ? generateRandomId() : id, name);
            Gson gson = new Gson();
            String tableAsJson = gson.toJson(excelTableData.getColumns());
            dataContainer.setData(GenericJsonMapper.convertFromJsonArray(tableAsJson));
            dataContainerRepository.save(dataContainer);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String generateRandomId() {
        Random random = new Random();
        return "CONTAINER-" + random.nextInt(10000000);
    }
}
