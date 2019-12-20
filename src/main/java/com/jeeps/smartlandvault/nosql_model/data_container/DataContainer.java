package com.jeeps.smartlandvault.nosql_model.data_container;

public class DataContainer {
    private String name;

    public DataContainer() {}

    public DataContainer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DataContainer{" +
                "name='" + name + '\'' +
                '}';
    }
}
