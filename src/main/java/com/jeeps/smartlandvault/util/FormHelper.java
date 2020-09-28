package com.jeeps.smartlandvault.util;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;

public class FormHelper {

    public static DataContainer transferContainerFormData(DataContainer formData, DataContainer originalContainer) {
        // Copy new values from form into original container
        originalContainer.setName(formData.getName());
        originalContainer.setDescription(formData.getDescription());
        originalContainer.setObservatory(formData.getObservatory());
        originalContainer.setYear(formData.getYear());
        originalContainer.setPublisher(formData.getPublisher());
        originalContainer.setSourceUrl(formData.getSourceUrl());
        originalContainer.setLicenseType(formData.getLicenseType());
        return originalContainer;
    }
}
