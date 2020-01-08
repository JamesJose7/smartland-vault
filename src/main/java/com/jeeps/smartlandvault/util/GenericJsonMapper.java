package com.jeeps.smartlandvault.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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

    public static String detectType(Object object) {
        if (object instanceof Integer) return "Integer";
        if (object instanceof Double) return "Decimal";
        if (object instanceof String) return "String";
        if (object instanceof ArrayList) {
            List<Object> list = (ArrayList<Object>) object;
            if (list.isEmpty()) return "Empty Array";
            if (list.get(0) instanceof Map) return "Object Array";
            return "Array";
        }
        if (object instanceof Map) return "Object";
        return "Unknown";
    }

    public static Map<Object, Object> getSelectedTree(List<Object> data, String tree) {
        if (tree.equals("/"))
            return (HashMap<Object, Object>) data.get(0);
        else {
            List<String> treeElements = new ArrayList<>(Arrays.asList(tree.split("/")));
            treeElements.remove(""); // Remove blank from first '/'
            Map<Object, Object> dataMap = (HashMap<Object, Object>) data.get(0);
            for (String treeElement : treeElements) {
                if (dataMap.get(treeElement) instanceof ArrayList) {
                    List<Object> list = (ArrayList<Object>) dataMap.get(treeElement);
                    dataMap = (HashMap<Object, Object>) list.get(0);
                } else
                    dataMap = (HashMap<Object, Object>) dataMap.get(treeElement);
            }
            return dataMap;
        }
    }
}
