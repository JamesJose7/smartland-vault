package com.jeeps.smartlandvault.nosql.data_container;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface DataContainerRepository extends MongoRepository<DataContainer, String> {
    List<DataContainer> findAllByDeletedIsFalseAndSharedObservatoriesContains(Integer observatoryId);
    List<DataContainer> findAllByDeletedIsFalseAndMergeIsFalseAndSharedObservatoriesContains(Integer observatoryId);
    List<DataContainer> findAllByDeletedIsFalseAndMergeIsTrueAndSharedObservatoriesContains(Integer observatoryId);
    List<DataContainer> findAllByDeletedIsFalseAndPublicContainerIsTrue();
    List<DataContainer> findAllByDeletedIsFalseAndObservatoryEquals(int observatory);
    List<DataContainer> findAllByDeletedIsFalseAndMergeIsFalseAndObservatoryEquals(int observatory);
    List<DataContainer> findAllByDeletedIsFalseAndMergeIsTrueAndObservatoryEquals(int observatory);
}
