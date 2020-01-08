package com.jeeps.smartlandvault.web.controller;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

import static com.jeeps.smartlandvault.util.GenericJsonMapper.detectType;
import static com.jeeps.smartlandvault.util.GenericJsonMapper.getSelectedTree;

@Controller
public class ContainerController {

    @Autowired
    private DataContainerRepository dataContainerRepository;

    @RequestMapping("/containers")
    public String containersBrowser(Model model) {
        model.addAttribute("dataContainers", dataContainerRepository.findAll());
        return "containers_browser";
    }

    @RequestMapping("/container/{id}")
    public String viewContainer(@PathVariable("id") String id,
                                @RequestParam(name = "tree", defaultValue = "/", required = false) String currentTree,
                                Model model) {
        Optional<DataContainer> dataContainerOptional = dataContainerRepository.findById(id);
        if (dataContainerOptional.isEmpty())
            throw new ResourceNotFoundException();
        // Container and it's data
        DataContainer dataContainer = dataContainerOptional.get();
        List<Object> data = dataContainer.getData();
        // Get attribute names and types
        Map<String, String> properties = new HashMap<>();
        if (!data.isEmpty()) {
           properties = getSelectedTree(data, currentTree).entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().toString(),
                            e -> detectType(e.getValue())));
        }
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
        model.addAttribute("treeView", currentTree);
        model.addAttribute("containerUrl", String.format("/container/%s", dataContainer.getId()));
        model.addAttribute("dataProperties", properties);
        model.addAttribute("rawDataLink", String.format("/api/v1/dataContainer/%s/data", dataContainer.getId()));
        return "container_editor";
    }
}
