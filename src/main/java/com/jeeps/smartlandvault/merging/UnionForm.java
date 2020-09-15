package com.jeeps.smartlandvault.merging;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;

import java.util.List;

public class UnionForm {
    private String name;
    private DataContainer originalContainer;
    private List<DataContainer> newContainers;

    public UnionForm() {}

    public UnionForm(String name, DataContainer originalContainer, List<DataContainer> newContainers) {
        this.name = name;
        this.originalContainer = originalContainer;
        this.newContainers = newContainers;
    }

    public UnionForm(DataContainer originalContainer, List<DataContainer> newContainers) {
        this.originalContainer = originalContainer;
        this.newContainers = newContainers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataContainer getOriginalContainer() {
        return originalContainer;
    }

    public void setOriginalContainer(DataContainer originalContainer) {
        this.originalContainer = originalContainer;
    }

    public List<DataContainer> getNewContainers() {
        return newContainers;
    }

    public void setNewContainers(List<DataContainer> newContainers) {
        this.newContainers = newContainers;
    }
}
