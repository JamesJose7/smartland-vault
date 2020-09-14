package com.jeeps.smartlandvault.nosql.merged_container;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Document
public class MergedContainer {
    @Id
    private String id;
    private String name;
    private List<String> mergedContainers;
    private DataContainer container;

    public MergedContainer() {
    }

    public MergedContainer(String id, String name, List<String> mergedContainers, DataContainer container) {
        this.id = id;
        this.name = name;
        this.mergedContainers = mergedContainers;
        this.container = container;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMergedContainers() {
        return mergedContainers;
    }

    public void setMergedContainers(List<String> mergedContainers) {
        this.mergedContainers = mergedContainers;
    }

    public DataContainer getContainer() {
        return container;
    }

    public void setContainer(DataContainer container) {
        this.container = container;
    }
}
