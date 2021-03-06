package com.jeeps.smartlandvault.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TemplateUtils {

    private static final String ASSETS_PATH = "/assets/icons/file_types/";

    public static String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss");
        return dateFormat.format(date);
    }

    public static String formatShared(List<Integer> sharedObs) {
        switch (sharedObs.size()) {
            case 0:
                return "No compartido";
            case 1:
                return "1 Observatorio";
            default:
                return String.format("%d Observatorios", sharedObs.size());
        }
    }

    public static String getFileIcon(String extension) {
        StringBuilder nameBuilder = new StringBuilder(ASSETS_PATH);
        // Get icon based on extension
        switch (extension) {
            case "xls":
            case "xlsx":
                nameBuilder.append("xls");
                break;
            case "csv":
                nameBuilder.append("csv");
                break;
            default:
                nameBuilder.append("file");
        }
        // Add asset extension
        nameBuilder.append(".png");

        return nameBuilder.toString();
    }
}
