package com.jeeps.smartlandvault.web.controller;

import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerRepository;
import com.jeeps.smartlandvault.nosql.table_file.TableFile;
import com.jeeps.smartlandvault.nosql.table_file.TableFileService;
import com.jeeps.smartlandvault.util.ContainerExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class FilesController {
    @Autowired
    private TableFileService tableFileService;
    @Autowired
    private DataContainerRepository dataContainerRepository;

    @RequestMapping("/files/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") String id) {
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

    @RequestMapping("/files/export/excel/{containerId}")
    public ResponseEntity<Resource> exportExcel(@PathVariable("containerId") String containerId) {
        try {
            DataContainer dataContainer = dataContainerRepository.findById(containerId).orElse(new DataContainer());
            FileInputStream inputStream = ContainerExporter.exportToExcel(dataContainer);

            InputStreamResource resource = new InputStreamResource(inputStream);
            HttpHeaders headers = new HttpHeaders();
            String fileName = String.format("%s.%s", dataContainer.getName(), "xlsx");
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

    @RequestMapping("/files/export/csv/{containerId}")
    public ResponseEntity<Resource> exportCsv(@PathVariable("containerId") String containerId) {
        try {
            DataContainer dataContainer = dataContainerRepository.findById(containerId).orElse(new DataContainer());
            FileInputStream inputStream = ContainerExporter.exportToCsv(dataContainer);

            InputStreamResource resource = new InputStreamResource(inputStream);
            HttpHeaders headers = new HttpHeaders();
            String fileName = String.format("%s.%s", dataContainer.getName(), "csv");
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
