package com.github.akovac35.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import com.github.akovac35.TestConfiguration;
import com.opencsv.exceptions.CsvException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest()
public class CsvServiceTest {
    @Autowired
    private CsvService csvServiceInstance;

    @Test
    public void getFileContents_Works() throws IOException, CsvException
    {
        String result = csvServiceInstance.getFileContents(TestConfiguration.ExcludedAdNetworksFileName);
        
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    @Test
    public void getCsvContents_Works() throws IOException, CsvException
    {
        List<String[]> result = csvServiceInstance.getCsvContents(TestConfiguration.ExcludedAdNetworksFileName);
        
        assertTrue(result.size() != 0);
    }
}