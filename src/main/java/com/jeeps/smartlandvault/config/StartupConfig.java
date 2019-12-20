package com.jeeps.smartlandvault.config;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerRepository;
import com.jeeps.smartlandvault.util.GenericJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StartupConfig {

    @Autowired
    private DataContainerRepository dataContainerRepository;

    @EventListener(ContextRefreshedEvent.class)
    public void bootConfiguration() {
        // Test object with data array
        Optional<DataContainer> arrayDataContainerOptional = dataContainerRepository.findById("test-container");
        if (arrayDataContainerOptional.isEmpty()) {
            String json = "[{\"screamId\":\"hRmUmDIyzjJ9339t8rVz\",\"body\":\"Hello world again\",\"userHandle\":\"user\",\"createdAt\":\"2019-12-20T17:27:25.854Z\"},{\"screamId\":\"ThVNlb1hjRlspATi1YA2\",\"body\":\"Hello world\",\"userHandle\":\"user\",\"createdAt\":\"2019-12-20T17:27:18.444Z\"}]";
            DataContainer arrayDataContainer = new DataContainer("test-container",
                    "containing objects",
                    "test",
                    "http://example.org",
                    true,
                    GenericJsonMapper.convertFromJsonArray(json));
            dataContainerRepository.save(arrayDataContainer);
        }

        // Test object with single json element
        Optional<DataContainer> singleDataContainerOptional = dataContainerRepository.findById("single-container");
        if (singleDataContainerOptional.isEmpty()) {
            String json = "{\"likeCount\":1,\"body\":\"Hello world again\",\"commentCount\":1,\"createdAt\":\"2019-12-20T17:27:25.854Z\",\"userHandle\":\"user\",\"userImage\":\"https://firebasestorage.googleapis.com/v0/b/socialape-8d0ee.appspot.com/o/7602692939.jpg?alt=media\",\"screamId\":\"hRmUmDIyzjJ9339t8rVz\",\"comments\":[{\"userImage\":\"https://firebasestorage.googleapis.com/v0/b/socialape-8d0ee.appspot.com/o/7602692939.jpg?alt=media\",\"screamId\":\"hRmUmDIyzjJ9339t8rVz\",\"body\":\"Don't overuse literally\",\"request\":\"user\",\"createdAt\":\"2019-12-20T17:27:54.009Z\"}]}";
            DataContainer singleContainer = new DataContainer("single-container",
                    "This one only has one object",
                    "test",
                    "http://example.org",
                    false,
                    GenericJsonMapper.convertFromJsonArray(json));
            dataContainerRepository.save(singleContainer);
        }


        /*
        Gson gson = new Gson();
        dataContainerRepository.findAll().forEach(container -> {
            if (container != null) {
                if (container.getData() != null) {
                    List<Object> data = container.getData();
                    data.forEach(object -> {
                        String raw = gson.toJson(object);
                        JsonElement jsonElement = gson.toJsonTree(object);

                        ((Map<Object, Object>) object).forEach((key, value) -> {
                            System.out.println(key);
                            System.out.println(value);
                        });

                        System.out.println(raw);
                        System.out.println(jsonElement);
                    });

                }
            }
        });*/
    }
}
