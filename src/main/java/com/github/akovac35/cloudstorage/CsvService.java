package com.github.akovac35.cloudstorage;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/*
References:
    https://cloud.google.com/storage/docs/downloading-objects#code-samples
    https://www.baeldung.com/java-google-cloud-storage
*/

@Service
public class CsvService {

    private static Storage storage = StorageOptions.getDefaultInstance().getService();

    @Value("${google.storage.bucket}")
    private String bucketName;

    public String getFileContents(String fileName)
    {
        Blob blob = storage.get(bucketName, fileName);
        return new String(blob.getContent());
    }

    public List<String[]> getCsvContents(String fileName) throws IOException, CsvException
    {
        String tmp = getFileContents(fileName);

        try (CSVReader reader = new CSVReader(new StringReader(tmp))) {
            List<String[]> r = reader.readAll();
            return r;
        }
    }
}