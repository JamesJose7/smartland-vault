package com.jeeps.smartlandvault.sql.sorted_containers;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ContainerInventoryRepository extends CrudRepository<ContainerInventory, Long> {
    Optional<ContainerInventory> findByContainerId(String containerId);
}
