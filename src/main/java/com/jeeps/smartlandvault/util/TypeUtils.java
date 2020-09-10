package com.jeeps.smartlandvault.util;

public class TypeUtils {

    public static Object parseType(String element) {
        if (isNumeric(element))
            return Integer.parseInt(element);
        if (isDecimal(element))
            return Double.parseDouble(element);
        return element;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isDecimal(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
