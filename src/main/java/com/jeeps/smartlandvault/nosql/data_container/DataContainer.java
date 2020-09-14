package com.jeeps.smartlandvault.nosql.data_container;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeeps.smartlandvault.nosql.metadata.Metadata;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Document
public class DataContainer {
    public static final String ORIGIN_EXCEL = "Excel";
    public static final String ORIGIN_REST_API = "Rest API";

    @Id
    private String id;
    private String name;
    private String observatory;
    private int year;
    private String fileUrl;
    private String publisher;
    private String sourceUrl;
    private boolean isActive;
    private int dataCount;
    private int propertyCount;
    private String originType;
    private boolean isMerge;
    @JsonIgnore
    private List<Object> data;

    private List<Metadata> metadata;

    public DataContainer() {}

    public DataContainer(String id, String name, String originType) {
        setId(id);
        this.name = name;
        this.originType = originType;
    }

    public DataContainer(String id, String name, String publisher, String sourceUrl, boolean isActive, List<Object> data) {
        setId(id);
        this.name = name;
        this.publisher = publisher;
        this.sourceUrl = sourceUrl;
        this.isActive = isActive;
        setData(data);
    }

    public DataContainer(String id, String name, String observatory, int year, String fileUrl, String originType, String publisher, String sourceUrl) {
        setId(id);
        this.name = name;
        this.observatory = observatory;
        this.year = year;
        this.fileUrl = fileUrl;
        this.publisher = publisher;
        this.sourceUrl = sourceUrl;
        this.originType = originType;
    }

    public DataContainer(String id, String name, String originType, String publisher, String sourceUrl) {
        setId(id);
        this.name = name;
        this.publisher = publisher;
        this.sourceUrl = sourceUrl;
        this.originType = originType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null)
            this.id = generateRandomId();
        else
            this.id = id.isEmpty() ? generateRandomId() : id;
    }

    public void setNewUnionId() {
        this.id = generateRandomId() + "-union";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObservatory() {
        return observatory;
    }

    public void setObservatory(String observatory) {
        this.observatory = observatory;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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

    public boolean isMerge() {
        return isMerge;
    }

    public void setMerge(boolean merge) {
        isMerge = merge;
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

    public List<Metadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<Metadata> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "DataContainer{" +
                "name='" + name + '\'' +
                '}';
    }

    private String generateRandomId() {
        Random random = new Random();
        return "CONTAINER-" + random.nextInt(10000000);
    }
}
