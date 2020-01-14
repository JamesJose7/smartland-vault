package com.jeeps.smartlandvault.core;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    // Handle max upload size exceeded
    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<Map<String, Object>> handleMaxUploadSize(MaxUploadSizeExceededException e, RedirectAttributes redirectAttributes) {
        JSONObject json = new JSONObject();
        json.put("error", "Max file upload size exceeded");
        json.put("details", "The current file size limit is " + maxFileSize);
        return ResponseEntity.badRequest().body(json.toMap());
    }
}
