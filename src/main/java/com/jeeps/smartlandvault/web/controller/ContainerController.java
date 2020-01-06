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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
                                Model model) {
        Optional<DataContainer> dataContainerOptional = dataContainerRepository.findById(id);
        if (dataContainerOptional.isEmpty())
            throw new ResourceNotFoundException();
        model.addAttribute("dataContainer", dataContainerOptional.get());
        // Get attribute names
        Map<String, String> properties = new HashMap<>();
//        List<String> propertyTypes = new ArrayList<>();
        List<Object> data = dataContainerOptional.get().getData();
        if (!data.isEmpty()) {
            ((Map<Object, Object>) data.get(0)).forEach((key, value) -> {
                properties.put(key.toString(),
                        GenericJsonMapper.detectType(value));
            });
        }
        model.addAttribute("dataProperties", properties);
        model.addAttribute("rawDataLink", String.format("/api/v1/dataContainer/%s/data", dataContainerOptional.get().getId()));
        return "container_editor";
    }
}
