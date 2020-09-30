package com.jeeps.smartlandvault.nosql.license_type;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface LicenseTypeRepository extends MongoRepository<LicenseType, String> {
}
