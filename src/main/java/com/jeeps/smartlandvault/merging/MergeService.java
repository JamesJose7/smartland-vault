package com.jeeps.smartlandvault.merging;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerRepository;
import com.jeeps.smartlandvault.nosql.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MergeService {

    @Autowired
    private DataContainerRepository dataContainerRepository;

    public List<DataContainer> findUnionCandidates(String containerId) {
        Optional<DataContainer> containerOptional = dataContainerRepository.findById(containerId);
        if (containerOptional.isEmpty()) return null;
        DataContainer selectedContainer = containerOptional.get();
        List<DataContainer> allContainers = dataContainerRepository.findAll();

        // Find containers with the same data structure
        if (selectedContainer.getMetadata() == null) return Collections.emptyList();
        return allContainers.stream()
                .filter(container -> {
                    if (container.getMetadata() == null) return false;
                    if (container.getMetadata().size() > selectedContainer.getMetadata().size()) return false;
                    List<Metadata> equalMetadata = container.getMetadata().stream().filter(metadata ->
                            containsMetadata(selectedContainer.getMetadata(), metadata))
                            .collect(Collectors.toList());
                    return equalMetadata.size() == selectedContainer.getMetadata().size();
                })
                .collect(Collectors.toList());
    }

    private boolean containsMetadata(List<Metadata> metadataList, Metadata targetMetadata) {
        return metadataList.stream().anyMatch(metadata ->
                metadata.getDataType().equals(targetMetadata.getDataType())
                    &&
                metadata.getPropertyName().equals(targetMetadata.getPropertyName()));
    }

    public DataContainer performUnion(DataContainer original, List<DataContainer> newContainers) {
        for (DataContainer container : newContainers)
            original.getData().addAll(container.getData());
        return original;
    }
}
