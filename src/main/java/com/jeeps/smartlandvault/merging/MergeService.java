package com.jeeps.smartlandvault.merging;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerRepository;
import com.jeeps.smartlandvault.nosql.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MergeService {

    @Autowired
    private DataContainerRepository dataContainerRepository;

    public List<DataContainer> findUnionCandidates(String containerId) {
        Optional<DataContainer> containerOptional = dataContainerRepository.findById(containerId);
        if (!containerOptional.isPresent()) return null;
        DataContainer selectedContainer = containerOptional.get();
        List<DataContainer> allContainers = dataContainerRepository.findAllByDeletedIsFalse();

        // Find containers with the same data structure
        if (selectedContainer.getMetadata() == null) return Collections.emptyList();
        return allContainers.stream()
                .filter(container -> {
                    if (container.getMetadata() == null) return false;
                    // Filter out containers with more columns than the selected one
                    if (container.getMetadata().size() > selectedContainer.getMetadata().size()) return false;
                    // Filter out the selected container
                    if (container.getId().equals(selectedContainer.getId())) return false;
                    // Find containers with the same metadata
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

    public List<JoinCandidate> findJoinCandidates(String containerId) {
        Optional<DataContainer> containerOptional = dataContainerRepository.findById(containerId);
        if (!containerOptional.isPresent()) return null;
        DataContainer selectedContainer = containerOptional.get();
        List<DataContainer> allContainers = dataContainerRepository.findAll();

        // Find containers with the same data structure
        if (selectedContainer.getMetadata() == null) return Collections.emptyList();
        return allContainers.stream()
                .filter(container -> {
                    if (container.getMetadata() == null) return false;
                    // Filter out the selected container
                    if (container.getId().equals(selectedContainer.getId())) return false;
                    // Find containers with the same metadata
                    List<Metadata> equalMetadata = container.getMetadata().stream().filter(metadata ->
                            containsMetadata(selectedContainer.getMetadata(), metadata))
                            .collect(Collectors.toList());
                    return !equalMetadata.isEmpty();
                })
                .map(container -> {
                    List<String> similarProperties = filterSimilarProperties(selectedContainer, container);
                    return new JoinCandidate(container, similarProperties);
                })
                .collect(Collectors.toList());
    }

    private List<String> filterSimilarProperties(DataContainer main, DataContainer candidate) {
        // Get main property names as a list
        List<String> mainPropertyNames = main.getMetadata().stream()
                .map(metadata -> metadata.getPropertyName().toLowerCase())
                .collect(Collectors.toList());
        // Filter similar properties
        return candidate.getMetadata().stream()
                .filter(metadata -> mainPropertyNames.contains(metadata.getPropertyName().toLowerCase()))
                .map(Metadata::getPropertyName)
                .collect(Collectors.toList());
    }

    public DataContainer performJoin(DataContainer originalContainer, DataContainer joinContainer,
                                     String leftJoinProperty, String rightJoinProperty) {
        // Merge metadata
        joinContainer.getMetadata().removeIf(metadata -> metadata.getPropertyName().equals(leftJoinProperty));
        originalContainer.getMetadata().addAll(joinContainer.getMetadata());
        // join columns
        List<Object> newValues = originalContainer.getData().stream()
                .map(rowDataRaw -> {
                    LinkedHashMap<String, Object> originalContainerData = (LinkedHashMap) rowDataRaw;
                    Object joinPropertyValue = originalContainerData.get(leftJoinProperty);
                    LinkedHashMap<String, Object> joinContainerValues =
                            findJoinColumn(joinContainer, rightJoinProperty, joinPropertyValue);
                    if (joinContainerValues != null)
                        originalContainerData.putAll(joinContainerValues);
                    else
                        originalContainerData.clear();
                    return originalContainerData;
                })
                .collect(Collectors.toList());
        newValues.removeIf(Objects::isNull);
        originalContainer.setData(newValues);
        return originalContainer;
    }

    private LinkedHashMap<String, Object> findJoinColumn(DataContainer dataContainer, String joinProperty, Object joinPropertyValue) {
        return dataContainer.getData().stream()
                .filter(rowDataRaw -> {
                    LinkedHashMap<String, Object> rowData = (LinkedHashMap) rowDataRaw;
                    if (rowData.get(joinProperty) == null || joinPropertyValue == null) return false;
                    return rowData.get(joinProperty).equals(joinPropertyValue);
                })
                .findFirst()
                .map(rowDataRaw -> {
                    LinkedHashMap<String, Object> rowData = (LinkedHashMap) rowDataRaw;
                    LinkedHashMap<String, Object> newRowData = new LinkedHashMap<>(rowData);
                    newRowData.remove(joinProperty);
                    return newRowData;
                })
                .orElse(null);
    }
}
