package com.jeeps.smartlandvault.nosql.metadata;

import com.jeeps.smartlandvault.sql.item.Item;

public class Metadata {
    private String propertyName;
    private String dataType;
    private String name;
    private String description;

    public Metadata() {
    }

    public Metadata(Item item) {
        propertyName = item.getPropertyName();
        dataType = item.getDataType();
        name = item.getName();
        description = item.getDescription();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
