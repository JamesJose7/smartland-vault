package com.jeeps.smartlandvault.sql.container_stock;

import com.jeeps.smartlandvault.core.BaseEntity;
import com.jeeps.smartlandvault.sql.sorted_containers.ContainerInventory;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Item extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "containerInventory_id")
    private ContainerInventory containerInventory;
    private String propertyName;
    private String dataType;

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
}
