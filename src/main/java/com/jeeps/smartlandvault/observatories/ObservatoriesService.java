package com.jeeps.smartlandvault.observatories;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.jeeps.smartlandvault.util.HttpClient.buildRequest;
import static com.jeeps.smartlandvault.util.HttpClient.httpRequest;

@Service
public class ObservatoriesService {
    public static final String OBSERVATORIES_API_URL = "https://apps.utpl.edu.ec/observatorio/api/v1/visor";
    public static final String OBSERVATORY_DETAILS_API_URL = "https://apps.utpl.edu.ec/observatorio/api/v1/observatorio/%d";
    public static final String OBSERVATORIES_BY_USER_API_URL = "https://apps.utpl.edu.ec/observatorio/api/v1/obtener_observatorios/%s";

    public List<Observatory> getObservatories() throws IOException {
        String body = httpRequest(buildRequest(OBSERVATORIES_API_URL));
        ObservatoryApiData apiData = new Gson().fromJson(body, ObservatoryApiData.class);
        return apiData.getObservatorios();
    }

    public Observatory getObservatory(int observatoryId) throws IOException {
        String body = httpRequest(buildRequest(String.format(OBSERVATORY_DETAILS_API_URL, observatoryId)));
        return new Gson().fromJson(body, Observatory.class);
    }

    public List<UserObservatory> getObservatoriesByUserToken(String token) throws IOException {
        String body = httpRequest(buildRequest(String.format(OBSERVATORIES_BY_USER_API_URL, token)));
        UserObservatory[] userObservatories = new Gson().fromJson(body, UserObservatory[].class);
        return Arrays.asList(userObservatories);
    }

}
