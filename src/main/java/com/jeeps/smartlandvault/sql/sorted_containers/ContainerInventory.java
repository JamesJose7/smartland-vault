package com.jeeps.smartlandvault.sql.sorted_containers;

import com.jeeps.smartlandvault.core.BaseEntity;
import com.jeeps.smartlandvault.sql.container_stock.Item;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class ContainerInventory extends BaseEntity {
    private String containerId;
    private String name;
    private String mainDataProperty; // Used to point to the main data object, in case there's an array inside the json object that contains the actual data
    @OneToMany(mappedBy = "containerInventory", cascade = CascadeType.ALL)
    private List<Item> items;

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMainDataProperty() {
        return mainDataProperty;
    }

    public void setMainDataProperty(String mainDataProperty) {
        this.mainDataProperty = mainDataProperty;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}