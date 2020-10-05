package com.jeeps.smartlandvault.observatories;

import java.util.List;

public class SharedObservatoriesForm {
    private String containerId;
    private List<Observatory> observatories;

    public SharedObservatoriesForm() { }

    public SharedObservatoriesForm(List<Observatory> observatories, List<Integer> sharedObservatories, String containerId) {
        observatories.forEach(obs -> {
            if (sharedObservatories.contains(obs.getId()))
                obs.setShared(true);
        });
        this.observatories = observatories;
        this.containerId = containerId;
    }

    public List<Observatory> getObservatories() {
        return observatories;
    }

    public void setObservatories(List<Observatory> observatories) {
        this.observatories = observatories;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }
}
