package com.jeeps.smartlandvault.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KeywordsHelper {
    public static List<String> processKeywords(String keywordsRaw) {
        return Arrays.stream(keywordsRaw.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
