package com.jeeps.smartlandvault.nosql.data_container;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeeps.smartlandvault.nosql.metadata.Metadata;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
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

//    private Catalog catalog
    private Date dateCreated;
    private Date dateUpdated;
    private List<String> keywords;
    private String description;
    private String fileType;
    private String licenseType;

    private boolean merge;
    private boolean deleted;

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

    public void setNewJoinId() {
        this.id = generateRandomId() + "-join";
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public boolean isMerge() {
        return merge;
    }

    public boolean getMerge() {
        return merge;
    }

    public void setMerge(boolean merge) {
        this.merge = merge;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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
        // update property count
        setPropertyCount(metadata.size());
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
