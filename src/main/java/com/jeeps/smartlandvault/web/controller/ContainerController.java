package com.jeeps.smartlandvault.web.controller;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerRepository;
import com.jeeps.smartlandvault.util.GenericJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

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
        DataContainer dataContainer = dataContainerOptional.get();
        model.addAttribute("dataContainer", dataContainer);
        // Get attribute names
        Map<String, String> properties = new HashMap<>();
        List<Object> data = dataContainer.getData();
        if (!data.isEmpty()) {
            getSelectedTree(data, currentTree).forEach((key, value) -> properties.put(
                    key.toString(),
                    GenericJsonMapper.detectType(value)));
        }
        // Append '/' if it does not have one at the end
        if (currentTree.charAt(currentTree.length()-1) != '/')
            currentTree += "/";
        if (currentTree.split("/").length > 0) {
            List<String> treeElements = new ArrayList<>(Arrays.asList(currentTree.split("/")));
            treeElements.remove(treeElements.size()-1);
            String previousTreeUrl = String.join("/", treeElements);
            model.addAttribute("previousTreeUrl", previousTreeUrl.isBlank() ? "/" : previousTreeUrl);
        }
        model.addAttribute("treeView", currentTree);
        model.addAttribute("containerUrl", String.format("/container/%s", dataContainer.getId()));
        model.addAttribute("dataProperties", properties);
        model.addAttribute("rawDataLink", String.format("/api/v1/dataContainer/%s/data", dataContainer.getId()));
        return "container_editor";
    }

    private Map<Object, Object> getSelectedTree(List<Object> data, String tree) {
        if (tree.equals("/"))
            return (HashMap<Object, Object>) data.get(0);
        else {
            List<String> treeElements = new ArrayList<>(Arrays.asList(tree.split("/")));
            treeElements.remove(""); // Remove blank from first '/'
            Map<Object, Object> dataMap = (HashMap<Object, Object>) data.get(0);
            for (String treeElement : treeElements) {
                if (dataMap.get(treeElement) instanceof ArrayList) {
                    List<Object> list = (ArrayList<Object>) dataMap.get(treeElement);
                    dataMap = (HashMap<Object, Object>) list.get(0);
                } else
                    dataMap = (HashMap<Object, Object>) dataMap.get(treeElement);
            }
            return dataMap;
        }
    }
}
