package com.jeeps.smartlandvault.nosql.data_container;

import java.util.List;

public class DataContainer {
    private String identifier;
    private String name;
    private List<Object> data;

    public DataContainer() {}

    public DataContainer(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataContainer{" +
                "name='" + name + '\'' +
                '}';
    }
}
