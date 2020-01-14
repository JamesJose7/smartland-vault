package com.jeeps.smartlandvault.util;

import com.jeeps.smartlandvault.exceptions.IncorrectExcelFormatException;
import com.jeeps.smartlandvault.fileupload.ExcelTableData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.*;

public class ExcelSheetReader {

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
        Sheet dataSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = dataSheet.iterator();

        // List for rows
        List<Map<String, Object>> tableData = new ArrayList<>();
        // Get first row for column names
        List<String> columnNames = new ArrayList<>();
        if (!iterator.hasNext()) throw new IncorrectExcelFormatException("Excel sheet does not have any rows");
        iterator.next().cellIterator().forEachRemaining(cell -> {
            columnNames.add(cell.getStringCellValue());
        });

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
        return new ExcelTableData(tableData);
    }
}
