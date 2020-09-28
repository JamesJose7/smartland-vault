package com.jeeps.smartlandvault.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KeywordsHelper {
    public static List<String> processKeywords(String keywordsRaw) {
        if (keywordsRaw.trim().isEmpty()) return Collections.emptyList();
        return Arrays.stream(keywordsRaw.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public static String unprocessKeywords(List<String> keywords) {
        if (keywords == null) return "";
        return String.join(", ", keywords);
    }
}
