package com.jeeps.smartlandvault.sql.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContainerInventoryApi {
    @Autowired
    private ContainerInventoryRepository containerInventoryRepository;

    @GetMapping(value = "api/v1/containerInventories/details")
    public Iterable<ContainerInventory> getAllInventoriesDetails() {
        return containerInventoryRepository.findAll();
    }

    @GetMapping(value = "api/v1/containerInventories/{id}/details")
    public ContainerInventory getInventoryDetails(@PathVariable("id") String id) {
        return containerInventoryRepository.findByContainerId(id)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
