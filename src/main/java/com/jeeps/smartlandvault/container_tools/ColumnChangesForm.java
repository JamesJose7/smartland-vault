package com.jeeps.smartlandvault.container_tools;

import java.util.List;

public class ColumnChangesForm {
    private String containerId;
    private List<String> selectedColumns;

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public List<String> getSelectedColumns() {
        return selectedColumns;
    }

    public void setSelectedColumns(List<String> selectedColumns) {
        this.selectedColumns = selectedColumns;
    }
}
