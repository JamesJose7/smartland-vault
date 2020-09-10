package com.jeeps.smartlandvault.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

public class HttpClient {
    private static final OkHttpClient client = new OkHttpClient();

    public static Request buildRequest(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }

    public static String httpRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            ResponseBody body = response.body();
            return body != null ? body.string() : "{\"error\": \"Error encountered\"}";
        }
    }
}
