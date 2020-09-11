package com.jeeps.smartlandvault.nosql.merged_container;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface MergedContainerRepository extends MongoRepository<MergedContainer, String> {
}
