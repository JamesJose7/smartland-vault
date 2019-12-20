package com.jeeps.smartlandvault.nosql.data_container;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class DataContainerApi {
    @Autowired
    private DataContainerRepository dataContainerRepository;

    @GetMapping(value = "/dataContainer/{id}/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getData(@PathVariable("id") String id) {
        Optional<DataContainer> containerOptional = dataContainerRepository.findById(id);
        String result = "[]";
        Gson gson = new Gson();
        if (containerOptional.isPresent())
            if (containerOptional.get().getData() != null) {
                List<Object> data = containerOptional.get().getData();
                return gson.toJson(data);
            }
        return result;
    }
}
