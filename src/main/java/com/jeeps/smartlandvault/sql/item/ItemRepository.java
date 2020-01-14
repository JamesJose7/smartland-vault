package com.jeeps.smartlandvault.sql.item;

import com.jeeps.smartlandvault.sql.inventory.ContainerInventory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ItemRepository extends CrudRepository<Item, Long> {
    @Transactional
    void deleteAllByContainerInventory(ContainerInventory containerInventory);
}
