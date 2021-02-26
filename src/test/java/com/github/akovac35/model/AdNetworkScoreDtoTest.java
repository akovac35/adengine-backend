package com.github.akovac35.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import com.github.akovac35.AdEngineBackend;
import com.github.akovac35.TestConfiguration;
import com.github.akovac35.services.CsvService;
import com.opencsv.exceptions.CsvException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.stream.Collectors;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AdEngineBackend.class)
public class AdNetworkScoreDtoTest
{
    @Autowired
    private CsvService csvService;

    @Test
    public void fromCsv_Works() throws IOException, CsvException
    {
        List<String[]> result = csvService.getCsvContents(TestConfiguration.AdNetworkScoresFileName);

        assertTrue(result.size() != 0);

        List<AdNetworkScoreDto> scores = AdNetworkScoreDto.fromCsv(result);
        
        assertTrue(scores.size() != 0);
        for (AdNetworkScoreDto adNetworkScoreDto : scores) {
            assertNotNull(adNetworkScoreDto);
            assertNotNull(adNetworkScoreDto.getAdName());
            assertNotNull(adNetworkScoreDto.getAdType());
            assertNotNull(adNetworkScoreDto.getCountryCodeIso2());
        }

        assertTrue(scores.stream().filter(item -> item.getAdScore() > 0).collect(Collectors.toList()).size() > 0);
    }
}