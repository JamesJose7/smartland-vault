package com.jeeps.smartlandvault.util;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.metadata.Metadata;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class ContainerExporter {

//    private static final String TEMP_PATH = "./temp/";
    private static final String TEMP_PATH = String.format("%s/webapps/assets/smartlandvault/temp/",
                                        System.getProperty("catalina.base"));
    public static final String XLSX = ".xlsx";

    public static FileInputStream exportToExcel(DataContainer dataContainer) throws IOException {
        createTempPath();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("data");
        // Create header
        int colCounter = 0;
        Map<String, Integer> headerIndexes = new HashMap<>();
        Row header = sheet.createRow(0);
        for (Metadata metadata : dataContainer.getMetadata()) {
            Cell cell = header.createCell(colCounter);
            cell.setCellValue(metadata.getPropertyName());
            // Save header index
            headerIndexes.put(metadata.getPropertyName(), colCounter);
            colCounter++;
        }
        // Create data rows
        int rowCounter = 1;
        for (Object dataRowRaw : dataContainer.getData()) {
            Row row = sheet.createRow(rowCounter);
            LinkedHashMap<String, Object> dataRow = (LinkedHashMap) dataRowRaw;
            dataRow.forEach((column, value) -> {
                // Get the column index for this property
                Integer colIndex = headerIndexes.get(column);
                if (colIndex != null) {
                    Cell cell = row.createCell(colIndex);
                    // Set the appropriate value
                    String valueAsString = String.valueOf(value);
                    if (TypeUtils.isDecimal(valueAsString))
                        cell.setCellValue(Double.parseDouble(valueAsString));
                    else if (TypeUtils.isNumeric(valueAsString))
                        cell.setCellValue(Integer.parseInt(valueAsString));
                    else
                        cell.setCellValue(valueAsString);
                }
            });
            rowCounter++;
        }

        // Create temp file and return input stream
        File file = new File(String.format(TEMP_PATH + "%s.xsls", dataContainer.getId()));
        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
        return new FileInputStream(file);
    }

    public static FileInputStream exportToCsv(DataContainer dataContainer) throws IOException {
        createTempPath();

        // Get headers
        List<String> headersList = new ArrayList<>();
        for (Metadata metadata : dataContainer.getMetadata())
            headersList.add(metadata.getPropertyName());
        String[] headers = headersList.toArray(new String[0]);

        File file = new File(String.format(TEMP_PATH + "%s.csv", dataContainer.getId()));
        FileWriter outputStream = new FileWriter(file);

        // Write to CSV file
        try (CSVPrinter printer = new CSVPrinter(outputStream, CSVFormat.DEFAULT.withHeader(headers))) {
            for (Object dataRowRaw : dataContainer.getData()) {
                LinkedHashMap<String, Object> dataRow = (LinkedHashMap) dataRowRaw;
                printer.printRecord(dataRow.values());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new FileInputStream(file);
    }

    public static boolean deleteTempFile(String containerId, String format) {
        File file = new File(String.format("%s%s%s", TEMP_PATH, containerId, format));
        file.deleteOnExit();
        return false;
    }

    private static void createTempPath() {
        File tempDir = new File(TEMP_PATH);
        if (!tempDir.exists())
            tempDir.mkdirs();
    }
}
