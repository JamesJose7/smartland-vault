package com.jeeps.smartlandvault.util;

import com.jeeps.smartlandvault.exceptions.IncorrectExcelFormatException;
import com.jeeps.smartlandvault.fileupload.ExcelTableData;
import com.jeeps.smartlandvault.nosql.table_file.TableFile;
import com.jeeps.smartlandvault.sql.item.Item;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    public static ExcelTableData parseWorkBook(InputStream inputStream, String extension) throws FileNotFoundException, IOException, IncorrectExcelFormatException {
        // Check whether Excel or CSV
        if (extension.equals(TableFile.CSV_FILE))
            return parseCsvFile(inputStream);
        else if (extension.equals(TableFile.EXCEL_FILE) || extension.equals(TableFile.OLD_EXCEL_FILE))
            return parseExcelFile(inputStream, extension);
        else // Return if there is no compatible exception
            throw new IncorrectExcelFormatException("File not supported");
    }

    private static ExcelTableData parseExcelFile(InputStream inputStream, String extension) throws IOException, IncorrectExcelFormatException  {
        Workbook workbook;
        if (extension.equals(TableFile.EXCEL_FILE))
            workbook = new XSSFWorkbook(inputStream);
        else
            workbook = new HSSFWorkbook(inputStream);

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
        if (!iterator.hasNext()) throw new IncorrectExcelFormatException("Excel sheet does not have any rows");
        Map<Integer, String> columnNames = new HashMap<>();
        for (Cell currentCell : iterator.next())
            columnNames.put(currentCell.getColumnIndex(), currentCell.getStringCellValue().trim().replaceAll(" ", "_"));
        if (columnNames.isEmpty()) throw new IncorrectExcelFormatException("Excel sheet does not have any columns");

        while (iterator.hasNext()) {
            Map<String, Object> rowData = new HashMap<>();
            Row currentRow = iterator.next();
            for (Cell currentCell : currentRow) {
                String columnName = columnNames.get(currentCell.getColumnIndex());
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
                        item.setPropertyName(map.getOrDefault(METADATA_ELEMENT_ID, "").trim().replaceAll(" ", "_"));
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

    private static ExcelTableData parseCsvFile(InputStream inputStream) throws IOException, IncorrectExcelFormatException {
        CSVParser parser = CSVParser.parse(inputStream, StandardCharsets.UTF_8, CSVFormat.DEFAULT);
        List<CSVRecord> records = parser.getRecords();
        if (records.size() < 2) throw new IncorrectExcelFormatException("CSV file does not have enough rows");
        // Separate records from columns
        List<CSVRecord> columns = records.subList(0, 1);
        records = records.subList(1, records.size());

        // List for rows
        List<Map<String, Object>> tableData = new ArrayList<>();
        // Get column names
        Map<Integer, String> columnNames = new HashMap<>();
        int counter = 0;
        for (String column : columns.get(0)) {
            columnNames.put(counter, column.trim().replaceAll(" ", "_"));
            counter++;
        }

        for (CSVRecord row : records) {
            Map<String, Object> rowData = new HashMap<>();
            int columnCounter = 0;
            for (String currentCell : row) {
                String columnName = columnNames.get(columnCounter);
                rowData.put(columnName, TypeUtils.parseType(currentCell));
                columnCounter++;
            }
            tableData.add(rowData);
        }

        return new ExcelTableData(tableData);
    }
}
