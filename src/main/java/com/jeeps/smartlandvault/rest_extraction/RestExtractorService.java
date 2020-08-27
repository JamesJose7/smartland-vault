package com.jeeps.smartlandvault.rest_extraction;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerRepository;
import com.jeeps.smartlandvault.util.GenericJsonMapper;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RestExtractorService {
    private Logger logger = LoggerFactory.getLogger(RestExtractorService.class);

    public static volatile boolean isProcessRunning = false;

    @Autowired
    private DataContainerRepository dataContainerRepository;

    @Async("asyncExecutor")
    public void extractRestData(String restUrl, String id, String name, String publisher, String sourceUrl) {
        try {
            // Extract REST contents
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(restUrl)
                    .build();

            DataContainer dataContainer = new DataContainer(id, name, DataContainer.ORIGIN_REST_API, publisher, sourceUrl);
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                String body = response.body().string();
                if (body != null) {
                    dataContainer.setData(GenericJsonMapper.convertFromJsonArray(body));
                    dataContainerRepository.save(dataContainer);
                }
            }
        } catch (Exception e) {

        }
    }
}
