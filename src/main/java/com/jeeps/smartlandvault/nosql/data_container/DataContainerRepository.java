package com.jeeps.smartlandvault.nosql.data_container;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DataContainerRepository extends MongoRepository<DataContainer, String> {
    DataContainer findByIdentifier(String identifier);
}
