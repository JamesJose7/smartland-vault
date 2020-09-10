package com.jeeps.smartlandvault.nosql.table_file;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class TableFileService {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations operations;

    public String addTableFile(String title, String type, MultipartFile file) throws IOException {
        DBObject metadata = new BasicDBObject();
        metadata.put("type", type);
        metadata.put("title", title);
        ObjectId id = gridFsTemplate.store(
                file.getInputStream(), file.getName(), file.getContentType(), metadata);
        return id.toString();
    }

    public TableFile getTableFile(String id) throws IllegalStateException, IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        TableFile tableFile = new TableFile();
        tableFile.setTitle(file.getMetadata().get("title").toString());
        tableFile.setFileType(file.getMetadata().get("type").toString());
        tableFile.setStream(operations.getResource(file).getInputStream());
        return tableFile;
    }
}
