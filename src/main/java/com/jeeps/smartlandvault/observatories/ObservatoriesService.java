package com.jeeps.smartlandvault.observatories;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.jeeps.smartlandvault.util.HttpClient.buildRequest;
import static com.jeeps.smartlandvault.util.HttpClient.httpRequest;

@Service
public class ObservatoriesService {
    public static final String OBSERVATORIES_API_URL = "https://apps.utpl.edu.ec/observatorio/api/v1/visor";

    public List<Observatory> getObservatories() throws IOException {
        String body = httpRequest(buildRequest(OBSERVATORIES_API_URL));
        ObservatoryApiData apiData = new Gson().fromJson(body, ObservatoryApiData.class);
        return apiData.getObservatorios();
    }

    public Observatory getObservatory(String apiIdUrl) throws IOException {
        String body = httpRequest(buildRequest(apiIdUrl));
        return new Gson().fromJson(body, Observatory.class);
    }

}
