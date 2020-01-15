package com.jeeps.smartlandvault.util;

import com.jeeps.smartlandvault.exceptions.IncorrectExcelFormatException;
import com.jeeps.smartlandvault.fileupload.ExcelTableData;
import com.jeeps.smartlandvault.sql.item.Item;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelSheetReader {
    public static final String METADATA_ELEMENT_ID = "element_id";
    public static final String METADATA_NAME_ID = "name";
    public static final String METADATA_ELEMENT_TYPE = "element_type";
    public static final String METADATA_COLUMN_ELEMENT = "column";
    public static final String METADATA_METADATA_ELEMENT = "metadata";
    public static final String METADATA_NAME_COLUMN = "name";
    public static final String METADATA_DESCRIPTION_COLUMN = "description";
    public static final String METADATA_DATATYPE_COLUMN = "data_type";

    private static Logger logger = LoggerFactory.getLogger(ExcelSheetReader.class.getName());

    public static boolean isExcelMimeType(String type) {
        switch (type) {
            case "application/vnd.ms-excel":
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                return true;
            default:
                return false;
        }
    }

    public static ExcelTableData parseWorkBook(InputStream inputStream) throws FileNotFoundException, IOException, IncorrectExcelFormatException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        // Parse data sheet
        Sheet dataSheet;
        try {
            dataSheet = workbook.getSheetAt(0);
        } catch (IllegalArgumentException e) {
            throw new IncorrectExcelFormatException("Excel workbook does not have any sheets in it");
        }
        Iterator<Row> iterator = dataSheet.iterator();

        // List for rows
        List<Map<String, Object>> tableData = new ArrayList<>();
        // Get first row for column names
        List<String> columnNames = new ArrayList<>();
        if (!iterator.hasNext()) throw new IncorrectExcelFormatException("Excel sheet does not have any rows");
        iterator.next().cellIterator().forEachRemaining(cell -> columnNames.add(cell.getStringCellValue()));
        if (columnNames.isEmpty()) throw new IncorrectExcelFormatException("Excel sheet does not have any columns");

        while (iterator.hasNext()) {
            int columnPosition = 0;
            Map<String, Object> rowData = new HashMap<>();
            Row currentRow = iterator.next();
            for (Cell currentCell : currentRow) {
                String columnName = columnNames.get(columnPosition);
                // Get objects based on data type
                switch (currentCell.getCellType()) {
                    case STRING:
                        rowData.put(columnName, currentCell.getStringCellValue());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(currentCell))
                            rowData.put(columnName, currentCell.getDateCellValue()
                                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
                        else
                            rowData.put(columnName, currentCell.getNumericCellValue());
                        break;
                    case BOOLEAN:
                        rowData.put(columnName, currentCell.getBooleanCellValue());
                        break;
                    case BLANK:
                        rowData.put(columnName, null);
                    default:
                        rowData.put(columnName, null);
                }
                columnPosition++;
            }
            // Add row to table data
            tableData.add(rowData);
        }
        ExcelTableData excelTableData = new ExcelTableData(tableData);

        // Parse metadata sheet (optional)
        try {
            Sheet metadataSheet = workbook.getSheetAt(1);
            Iterator<Row> metadataIterator = metadataSheet.iterator();
            // Get key columns positions
            Map<Integer, String> columnsPositions = new HashMap<>();
            for (Cell currentCell : metadataIterator.next())
                columnsPositions.put(currentCell.getColumnIndex(), currentCell.getStringCellValue());
            // Get metadata from each row
            List<Map<String, String>> tableMetadata = new ArrayList<>();
            while (metadataIterator.hasNext()) {
                Map<String, String> rowMetadata = new HashMap<>();
                Row currentRow = metadataIterator.next();
                for (Cell currentCell : currentRow) {
                    rowMetadata.put(columnsPositions.get(currentCell.getColumnIndex()), currentCell.getStringCellValue());
                }
                tableMetadata.add(rowMetadata);
            }
            // Create items metadata
            List<Item> items = tableMetadata.stream()
                    .filter(map -> map.getOrDefault(METADATA_ELEMENT_TYPE, "").equals(METADATA_COLUMN_ELEMENT))
                    .map(map -> {
                        Item item = new Item();
                        item.setPropertyName(map.getOrDefault(METADATA_ELEMENT_ID, ""));
                        item.setDataType(map.getOrDefault(METADATA_DATATYPE_COLUMN, ""));
                        item.setName(map.getOrDefault(METADATA_NAME_COLUMN, ""));
                        item.setDescription(map.getOrDefault(METADATA_DESCRIPTION_COLUMN, ""));
                        return item;
                    })
                    .collect(Collectors.toList());
            // Get container metadata
            String containerName = tableMetadata.stream()
                    .filter(map -> map.getOrDefault(METADATA_ELEMENT_TYPE, "").equals(METADATA_METADATA_ELEMENT)
                            && map.getOrDefault(METADATA_ELEMENT_ID, "").equals(METADATA_NAME_ID))
                    .findFirst()
                    .map(map -> map.get(METADATA_NAME_COLUMN))
                    .orElse("No name");
            // Add metadata to returning object
            excelTableData.setItemsMetadata(items);
            excelTableData.setName(containerName);
        } catch (IllegalArgumentException e) {
            logger.info("Metadata sheet not included, providing default information for container");
        }

        return excelTableData;
    }
}
