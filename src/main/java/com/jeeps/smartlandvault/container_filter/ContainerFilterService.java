package com.jeeps.smartlandvault.container_filter;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerRepository;
import com.jeeps.smartlandvault.observatories.ObservatoriesService;
import com.jeeps.smartlandvault.observatories.UserObservatory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContainerFilterService {
    private static final Logger logger = LoggerFactory.getLogger(ContainerFilterService.class);

    @Autowired
    private ObservatoriesService observatoriesService;
    @Autowired
    private DataContainerRepository dataContainerRepository;

    public List<DataContainer> filterDataContainersByUser(String userToken) {
        List<DataContainer> dataContainers = new ArrayList<>();
        try {
            List<UserObservatory.ObservatoryDetails> userObservatories = observatoriesService.getObservatoriesByUserToken(userToken);
            userObservatories.forEach(observatory -> {
                int obsId = observatory.getId();
                dataContainers.addAll(dataContainerRepository.findAllByDeletedIsFalseAndMergeIsFalseAndObservatoryEquals(obsId));
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        // Add public containers
        includePublicContainers(dataContainers);
        return dataContainers;
    }

    public List<DataContainer> filterAllDataContainersByUser(String userToken) {
        List<DataContainer> dataContainers = new ArrayList<>();
        try {
            List<UserObservatory.ObservatoryDetails> userObservatories = observatoriesService.getObservatoriesByUserToken(userToken);
            userObservatories.forEach(observatory -> {
                int obsId = observatory.getId();
                dataContainers.addAll(dataContainerRepository.findAllByDeletedIsFalseAndObservatoryEquals(obsId));
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        // Add public containers
        includePublicContainers(dataContainers);
        return dataContainers;
    }

    public List<DataContainer> filterMergedContainersByUser(String userToken) {
        List<DataContainer> dataContainers = new ArrayList<>();
        try {
            List<UserObservatory.ObservatoryDetails> userObservatories = observatoriesService.getObservatoriesByUserToken(userToken);
            userObservatories.forEach(observatory -> {
                int obsId = observatory.getId();
                dataContainers.addAll(dataContainerRepository.findAllByDeletedIsFalseAndMergeIsTrueAndObservatoryEquals(obsId));
            });
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        // Add public containers
        includePublicContainers(dataContainers);
        return dataContainers;
    }

    private void includePublicContainers(List<DataContainer> dataContainers) {
        List<DataContainer> publicContainers = dataContainerRepository.findAllByDeletedIsFalseAndPublicContainerIsTrue();
        publicContainers.forEach(container -> {
            if (!dataContainers.contains(container))
                dataContainers.add(container);
        });
    }
}
