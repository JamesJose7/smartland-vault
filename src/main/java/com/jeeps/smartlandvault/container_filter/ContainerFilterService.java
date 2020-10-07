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

    private static final int ALL = 0;
    private static final int MERGE_TRUE = 1;
    private static final int MERGE_FALSE = 2;

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
            // Add shared containers
            includeSharedContainers(dataContainers, userObservatories, MERGE_FALSE);
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
            // Add shared containers
            includeSharedContainers(dataContainers, userObservatories, ALL);
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
            // Add shared containers
            includeSharedContainers(dataContainers, userObservatories, MERGE_TRUE);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        // Add public containers
        includePublicContainers(dataContainers);
        return dataContainers;
    }

    private void includeSharedContainers(List<DataContainer> dataContainers,
                                         List<UserObservatory.ObservatoryDetails> observatories,
                                         int type) {
        // Iterate through observatories and get shared containers
        List<DataContainer> sharedContainers = new ArrayList<>();
        for (UserObservatory.ObservatoryDetails observatory : observatories) {
            List<DataContainer> filteredContainers = new ArrayList<>();
            switch (type) {
                case ALL:
                    filteredContainers =
                            dataContainerRepository.findAllByDeletedIsFalseAndSharedObservatoriesContains(observatory.getId());
                    break;
                case MERGE_FALSE:
                    filteredContainers =
                            dataContainerRepository.findAllByDeletedIsFalseAndMergeIsFalseAndSharedObservatoriesContains(observatory.getId());
                    break;
                case MERGE_TRUE:
                    filteredContainers =
                            dataContainerRepository.findAllByDeletedIsFalseAndMergeIsTrueAndSharedObservatoriesContains(observatory.getId());
                    break;
            }
            sharedContainers.addAll(filteredContainers);
        }
        // Add containers
        sharedContainers.forEach(container -> {
            if (!containsContainer(dataContainers, container))
                dataContainers.add(container);
        });
    }

    private void includePublicContainers(List<DataContainer> dataContainers) {
        List<DataContainer> publicContainers = dataContainerRepository.findAllByDeletedIsFalseAndPublicContainerIsTrue();
        publicContainers.forEach(container -> {
            if (!containsContainer(dataContainers, container))
                dataContainers.add(container);
        });
    }

    private boolean containsContainer(List<DataContainer> dataContainers, DataContainer container) {
        return dataContainers.stream().anyMatch(o -> o.getId().equals(container.getId()));
    }
}
