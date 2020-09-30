package com.jeeps.smartlandvault.nosql.license_type;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document
public class LicenseType {
    @Id
    private String id;
    private String name;

    public LicenseType() { }

    public LicenseType(String id, String name) {
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
}
