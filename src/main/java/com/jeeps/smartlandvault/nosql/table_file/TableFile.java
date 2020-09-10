package com.jeeps.smartlandvault.nosql.table_file;

import java.io.InputStream;

public class TableFile {
    public static final String EXCEL_FILE = "xlsx";
    public static final String OLD_EXCEL_FILE = "xls";
    public static final String CSV_FILE = "csv";

    private String title;
    private String fileType;
    private InputStream stream;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }
}
