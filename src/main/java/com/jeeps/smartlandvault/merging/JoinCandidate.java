package com.jeeps.smartlandvault.merging;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;

import java.util.List;

public class JoinCandidate {
    private DataContainer dataContainer;
    private List<String> validProperties;

    public JoinCandidate(DataContainer dataContainer, List<String> validProperties) {
        this.dataContainer = dataContainer;
        this.validProperties = validProperties;
    }

    public DataContainer getDataContainer() {
        return dataContainer;
    }

    public void setDataContainer(DataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    public List<String> getValidProperties() {
        return validProperties;
    }

    public void setValidProperties(List<String> validProperties) {
        this.validProperties = validProperties;
    }
}
