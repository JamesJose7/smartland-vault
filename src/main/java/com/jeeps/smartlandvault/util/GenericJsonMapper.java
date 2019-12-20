package com.jeeps.smartlandvault.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GenericJsonMapper {
    private static Logger logger = LoggerFactory.getLogger(GenericJsonMapper.class);

    public static List<Object> convertFromJsonArray(String json) {
        Gson gson = new Gson();
        List<Object> result = new ArrayList<>();
        boolean succeeded = false;
        // Try to convert an array
        try {
            Object[] objects = gson.fromJson(json, Object[].class);
            result = Arrays.asList(objects);
            succeeded = true;
        } catch (JsonSyntaxException e) {
            // This was not a json array
            logger.warn("Failed converting json from array");
        }
        // If fails, convert from an object
        if (!succeeded)
            try {
                Object object = gson.fromJson(json, Object.class);
                result = Collections.singletonList(object);
                succeeded = true;
            } catch (JsonSyntaxException e) {
                // This was not a json object
                logger.warn("Failed converting json from single object");
            }

        if (!succeeded) logger.error("Invalid json syntax, could not map to objects");
        return result;
    }
}
