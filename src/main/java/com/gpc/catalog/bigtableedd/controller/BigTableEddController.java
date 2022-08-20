package com.gpc.catalog.bigtableedd.controller;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.io.ByteSource;
import com.gpc.catalog.bigtableedd.model.Table;
import com.gpc.catalog.bigtableedd.processor.BigTableAdmin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static com.gpc.catalog.bigtableedd.util.YamlFileReader.readTableDetailsFromFile;

@RestController("/catalog/bigtable")
@Slf4j
public class BigTableEddController {

    private String storageLocation;

    private BigTableAdmin bigTableAdmin;

    public BigTableEddController(@Value("${gcp.storage.bucket-name}") String storageLocation, BigTableAdmin bigTableAdmin) {
        this.storageLocation = storageLocation;
        this.bigTableAdmin = bigTableAdmin;
    }

    @GetMapping("/schema-update/{file-name}")
    public ResponseEntity<Map<String,String>> bigtableSchemaUpdate(@PathVariable(name = "file-name") String fileName)  {
        //var tableSet = readTableDetailsFromFile(new FileInputStream("/Users/malarapu/Documents/poc-code-repos/bigtable-edd/src/main/resources/bigtable-edd-18-Aug-2022.yml"));
        Set<Table> tableSet = null;
        try {
            tableSet = readTableDetailsFromFile(readFileFromStorage(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
       // return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(bigTableAdmin.processBigtableSchemaChangeRequest(tableSet),HttpStatus.ACCEPTED);
    }




    private InputStream readFileFromStorage(String fileName) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        byte[] content = storage.readAllBytes(BlobId.of(storageLocation,fileName));
        return ByteSource.wrap(content).openStream();
    }





}
