package com.github.akovac35.services;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/*
References:
    https://cloud.google.com/storage/docs/downloading-objects#code-samples
    https://www.baeldung.com/java-google-cloud-storage
*/

@Service
public class CsvService {
    private static final Storage storage = StorageOptions.getDefaultInstance().getService();
    private static final Logger logger = LoggerFactory.getLogger(CsvService.class);

    @Autowired
    public CsvService(@Value("${google.storage.bucket}") String bucketName) {
        this.bucketName = bucketName;
    }

    protected final String bucketName;

    public String getFileContents(String fileName) {
        if (logger.isTraceEnabled())
            logger.trace("getFileContents: {}", fileName);

        if (fileName == null)
            throw new IllegalArgumentException("Argument is null: fileName");

        Blob blob = storage.get(bucketName, fileName);

        return new String(blob.getContent());
    }

    public List<String[]> getCsvContents(String fileName) throws IOException, CsvException {
        if (logger.isTraceEnabled())
            logger.trace("getCsvContents: {}", fileName);

        if (fileName == null)
            throw new IllegalArgumentException("Argument is null: fileName");

        String tmp = getFileContents(fileName);
        if (tmp == null)
            throw new IllegalArgumentException("File is invalid: fileName");

        try (CSVReader reader = new CSVReader(new StringReader(tmp))) {
            List<String[]> r = reader.readAll();
            return r;
        }
    }
}