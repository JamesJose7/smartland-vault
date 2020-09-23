package com.jeeps.smartlandvault.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TemplateUtils {

    public static String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - mm:hh:ss");
        return dateFormat.format(date);
    }
}
