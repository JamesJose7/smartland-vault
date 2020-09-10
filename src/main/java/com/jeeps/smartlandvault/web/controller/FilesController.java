package com.jeeps.smartlandvault.web.controller;

import com.jeeps.smartlandvault.nosql.table_file.TableFile;
import com.jeeps.smartlandvault.nosql.table_file.TableFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
public class FilesController {
    @Autowired
    private TableFileService tableFileService;

    @RequestMapping("/files/download/{id}")
    public ResponseEntity<Resource> test(@PathVariable("id") String id) {
        try {
            TableFile tableFile = tableFileService.getTableFile(id);
            InputStreamResource resource = new InputStreamResource(tableFile.getStream());
            HttpHeaders headers = new HttpHeaders();
            String fileName = String.format("%s.%s", tableFile.getTitle(), tableFile.getFileType());
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"");
            return ResponseEntity.ok()
                    .headers(headers)
//                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body(null);
    }
}
