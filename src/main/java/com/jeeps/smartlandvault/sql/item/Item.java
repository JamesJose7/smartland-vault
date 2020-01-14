package com.jeeps.smartlandvault.sql.item;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jeeps.smartlandvault.core.BaseEntity;
import com.jeeps.smartlandvault.sql.inventory.ContainerInventory;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Item extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "containerInventory_id")
    @JsonBackReference
    private ContainerInventory containerInventory;
    private String propertyName;
    private String dataType;
    private String name;
    private String description;

    public Item() {}

    public Item(ContainerInventory containerInventory, String propertyName, String dataType) {
        this.containerInventory = containerInventory;
        this.propertyName = propertyName;
        this.dataType = dataType;
    }

    public ContainerInventory getContainerInventory() {
        return containerInventory;
    }

    public void setContainerInventory(ContainerInventory containerInventory) {
        this.containerInventory = containerInventory;
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
