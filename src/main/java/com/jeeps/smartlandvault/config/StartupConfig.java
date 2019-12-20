package com.jeeps.smartlandvault.config;

import com.jeeps.smartlandvault.nosql_model.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql_model.data_container.DataContainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupConfig {

    @Autowired
    private DataContainerRepository dataContainerRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void bootConfiguration() {
        DataContainer dataContainer = new DataContainer("testing database");
        dataContainerRepository.save(dataContainer);

        dataContainerRepository.findAll().forEach(System.out::println);
    }
}
