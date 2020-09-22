package com.jeeps.smartlandvault.nosql.data_container;

import org.springframework.web.multipart.MultipartFile;

public class DataContainerForm {
    private MultipartFile file;
    private DataContainer dataContainer;
    private String keywordsRaw;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public DataContainer getDataContainer() {
        return dataContainer;
    }

    public void setDataContainer(DataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    public String getKeywordsRaw() {
        return keywordsRaw;
    }

    public void setKeywordsRaw(String keywordsRaw) {
        this.keywordsRaw = keywordsRaw;
    }
}
