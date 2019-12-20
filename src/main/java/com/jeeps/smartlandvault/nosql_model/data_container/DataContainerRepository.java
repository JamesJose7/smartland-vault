package com.jeeps.smartlandvault.nosql_model.data_container;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DataContainerRepository extends MongoRepository<DataContainer, String> {
}
