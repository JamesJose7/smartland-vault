package com.jeeps.smartlandvault.sql.container_stock;

import com.jeeps.smartlandvault.sql.sorted_containers.ContainerInventory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ItemRepository extends CrudRepository<Item, Long> {
    @Transactional
    void deleteAllByContainerInventory(ContainerInventory containerInventory);
}
