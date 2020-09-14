package com.jeeps.smartlandvault.merging;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;

import java.util.List;

public class UnionForm {
    private DataContainer originalContainer;
    private List<DataContainer> newContainers;

    public UnionForm() {}

    public UnionForm(DataContainer originalContainer, List<DataContainer> newContainers) {
        this.originalContainer = originalContainer;
        this.newContainers = newContainers;
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
