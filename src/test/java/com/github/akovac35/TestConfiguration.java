package com.github.akovac35;

import static org.mockito.Mockito.when;

import java.io.IOException;

import com.github.akovac35.services.CsvService;
import com.opencsv.exceptions.CsvException;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TestConfiguration {
    public static final String ExcludedAdNetworksFileName = "ExcludedAdNetworks.csv";
    public static final String AdNetworkScoresFileName = "AdNetworkScores.csv";

    @Bean
    @Primary
    public CsvService csvService() throws IOException, CsvException {
        CsvService service = Mockito.mock(CsvService.class);
        
        when(service.getFileContents(ExcludedAdNetworksFileName)).thenReturn(TestFileHelper.readFile(ExcludedAdNetworksFileName));
        when(service.getFileContents(AdNetworkScoresFileName)).thenReturn(TestFileHelper.readFile(AdNetworkScoresFileName));

        when(service.getCsvContents(ExcludedAdNetworksFileName)).thenCallRealMethod();
        when(service.getCsvContents(AdNetworkScoresFileName)).thenCallRealMethod();
        
        return service;
    }
}