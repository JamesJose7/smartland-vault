package com.jeeps.smartlandvault.fileupload;

import com.jeeps.smartlandvault.exceptions.IncorrectExcelFormatException;
import com.jeeps.smartlandvault.util.ExcelSheetReader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadApi {
    private static Logger logger = LoggerFactory.getLogger(FileUploadApi.class.getName());

    @Autowired
    private ExcelTransformerService excelTransformerService;

    @PostMapping(value = "api/v1/fileUpload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadExcelTable(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "name", required = false) String name
            ) throws Exception {
        JSONObject json = new JSONObject();
        // Handle file not included and notify user
        if (file == null) {
            json.put("error", "Please include a file with the response");
            logger.warn("Using file upload API without including a file");
            return ResponseEntity.badRequest().body(json.toString());
        }
        // Check MIME type to match the accepted ones
        if (file.getContentType() == null) {
            json.put("error", "Could not determine file's MIME type, please try again");
            return ResponseEntity.badRequest().body(json.toString());
        }
        if (!ExcelSheetReader.isExcelMimeType(file.getContentType())) {
            json.put("error", "Only Excel spreadsheets are supported, please try again");
            return ResponseEntity.badRequest().body(json.toString());
        }
        // Hand input stream to excel service
        try {
            excelTransformerService.transform(file.getInputStream(), id, name);
            json.put("message", "File uploaded successfully");
            return ResponseEntity.ok(json.toString());
        } catch (IncorrectExcelFormatException e) {
            json.put("error", e.getMessage());
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(json.toString());
        }
    }
}
