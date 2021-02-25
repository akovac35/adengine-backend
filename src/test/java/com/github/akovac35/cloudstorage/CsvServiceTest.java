package com.github.akovac35.cloudstorage;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import com.github.akovac35.AdEngineBackend;
import com.github.akovac35.TestConfiguration;
import com.opencsv.exceptions.CsvException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AdEngineBackend.class)
public class CsvServiceTest {
    @Autowired
    private CsvService csvService;

    @Test
    public void getFileContents_Works() throws IOException, CsvException
    {
        String result = csvService.getFileContents(TestConfiguration.ExcludedAdNetworksFileName);
        
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    @Test
    public void getCsvContents_Works() throws IOException, CsvException
    {
        List<String[]> resut = csvService.getCsvContents(TestConfiguration.ExcludedAdNetworksFileName);
        
        assertTrue(resut.size() != 0);
    }
}