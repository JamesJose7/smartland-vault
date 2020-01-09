package com.jeeps.smartlandvault.web.controller;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerRepository;
import com.jeeps.smartlandvault.sql.container_stock.Item;
import com.jeeps.smartlandvault.sql.container_stock.ItemRepository;
import com.jeeps.smartlandvault.sql.sorted_containers.ContainerInventory;
import com.jeeps.smartlandvault.sql.sorted_containers.ContainerInventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static com.jeeps.smartlandvault.util.GenericJsonMapper.getPropertiesFromTree;
import static com.jeeps.smartlandvault.util.GenericJsonMapper.getSelectedTree;
import static com.jeeps.smartlandvault.util.InventoryHelper.createItemsFromProperties;

@Controller
public class ContainerController {
    private static Logger logger = LoggerFactory.getLogger(ContainerController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private DataContainerRepository dataContainerRepository;
    @Autowired
    private ContainerInventoryRepository containerInventoryRepository;
    @Autowired
    private ItemRepository itemRepository;

    @RequestMapping("/containers")
    public String containersBrowser(Model model) {
        model.addAttribute("dataContainers", dataContainerRepository.findAll());
        return "containers_browser";
    }

    @RequestMapping("/container/{id}")
    public String viewContainer(@PathVariable("id") String id,
                                @RequestParam(name = "tree", defaultValue = "/", required = false) String currentTree,
                                @RequestParam(name = "navigating", defaultValue = "false", required = false) String isNavigating,
                                Model model) {
        Optional<DataContainer> dataContainerOptional = dataContainerRepository.findById(id);
        if (dataContainerOptional.isEmpty())
            throw new ResourceNotFoundException();
        // Container and it's data
        DataContainer dataContainer = dataContainerOptional.get();
        // Check if it has an inventory and change the default tree accordingly
        if (isNavigating.equals("false")) {
            Optional<ContainerInventory> optionalInventory = containerInventoryRepository.findByContainerId(id);
            if (optionalInventory.isPresent())
                currentTree = optionalInventory.get().getMainDataProperty();
        }
        // Get attribute names and types
        List<Object> data = dataContainer.getData();
        Map<String, String> properties = new HashMap<>();
        if (!data.isEmpty())
           properties = getPropertiesFromTree(getSelectedTree(data, currentTree));
        // Append '/' to the tree if it does not have one at the end
        if (currentTree.charAt(currentTree.length()-1) != '/')
            currentTree += "/";
        // Get previous tree for navigation
        if (currentTree.split("/").length > 0) {
            List<String> treeElements = new ArrayList<>(Arrays.asList(currentTree.split("/")));
            treeElements.remove(treeElements.size()-1);
            String previousTreeUrl = String.join("/", treeElements);
            model.addAttribute("previousTreeUrl", previousTreeUrl.isBlank() ? "/" : previousTreeUrl);
        }
        model.addAttribute("dataContainer", dataContainer);
        model.addAttribute("currentTree", currentTree);
        model.addAttribute("dataProperties", properties);
        model.addAttribute("containerUrl", String.format("/container/%s", dataContainer.getId()));
        model.addAttribute("selectMainDataUrl", String.format("%s/container/%s/main-data", contextPath, dataContainer.getId()));
        model.addAttribute("rawDataLink", String.format("/api/v1/dataContainer/%s/data", dataContainer.getId()));
        return "container_editor";
    }

    @RequestMapping(value = "/container/{id}/main-data", method = RequestMethod.POST)
    public String selectMainDataTree(RedirectAttributes redirectAttributes,
                                     @PathVariable(name = "id") String containerId,
                                     @RequestParam(name = "tree") String selectedTree) {
        Optional<DataContainer> dataContainerOptional = dataContainerRepository.findById(containerId);
        if (dataContainerOptional.isEmpty())
            throw new ResourceNotFoundException();
        List<Object> data = dataContainerOptional.get().getData();
        // Get the inventory or create it if it does not exist
        ContainerInventory containerInventory = containerInventoryRepository.findByContainerId(containerId)
                .orElse(new ContainerInventory(containerId, "unnamed", selectedTree));
        // Create items from selected tree
        Map<String, String> properties = getPropertiesFromTree(getSelectedTree(data, selectedTree));
        List<Item> items = createItemsFromProperties(properties, containerInventory);
        containerInventory.setItems(items);
        containerInventory.setMainDataProperty(selectedTree);
        // Delete previous items before saving
        itemRepository.deleteAllByContainerInventory(containerInventory);
        // Save or update inventory
        containerInventoryRepository.save(containerInventory);
        logger.info(String.format("Request :%s | from id: %s", selectedTree, containerId));
        return String.format("redirect:/container/%s", containerId);
    }
}
