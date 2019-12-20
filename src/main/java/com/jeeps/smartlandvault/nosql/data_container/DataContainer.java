package com.jeeps.smartlandvault.nosql.data_container;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class DataContainer {
    @Id
    private String id;
    private String name;
    @JsonIgnore
    private List<Object> data;

    public DataContainer() {}

    public DataContainer(String id, String name) {
        this.id = id;
        this.name = name;
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
