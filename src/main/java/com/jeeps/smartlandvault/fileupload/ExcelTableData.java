package com.jeeps.smartlandvault.fileupload;

import java.util.List;
import java.util.Map;

public class ExcelTableData {
    private String name;
    private List<Map<String, Object>> columns;

    public ExcelTableData() { }

    public ExcelTableData(List<Map<String, Object>> columns) {
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Map<String, Object>> getColumns() {
        return columns;
    }

    public void setColumns(List<Map<String, Object>> columns) {
        this.columns = columns;
    }
}
