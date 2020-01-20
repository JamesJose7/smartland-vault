package com.jeeps.smartlandvault.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlUtils {

    public static boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
        return true;
    }
}
