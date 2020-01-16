package com.jeeps.smartlandvault.nosql.data_container;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class DataContainer {
    public static final String ORIGIN_EXCEL = "Excel";
    public static final String ORIGIN_REST_API = "Rest API";

    @Id
    private String id;
    private String name;
    private String publisher;
    private String sourceUrl;
    private boolean isActive;
    private int dataCount;
    private int propertyCount;
    private String originType;
    @JsonIgnore
    private List<Object> data;

    public DataContainer() {}

    public DataContainer(String id, String name, String originType) {
        this.id = id;
        this.name = name;
        this.originType = originType;
    }

    public DataContainer(String id, String name, String publisher, String sourceUrl, boolean isActive, List<Object> data) {
        this.id = id;
        this.name = name;
        this.publisher = publisher;
        this.sourceUrl = sourceUrl;
        this.isActive = isActive;
        setData(data);
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getDataCount() {
        return dataCount;
    }

    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    public int getPropertyCount() {
        return propertyCount;
    }

    public void setPropertyCount(int propertyCount) {
        this.propertyCount = propertyCount;
    }

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }

    public List<Object> getData() {
        if (data == null) return new ArrayList<>();
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
        // update data count
        setDataCount(data.size());
    }

    @Override
    public String toString() {
        return "DataContainer{" +
                "name='" + name + '\'' +
                '}';
    }
}
