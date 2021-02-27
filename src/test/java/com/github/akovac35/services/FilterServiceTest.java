package com.github.akovac35.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.github.akovac35.AdEngineBackend;
import com.github.akovac35.TestConfiguration;
import com.github.akovac35.model.AdNetworkContextDto;
import com.github.akovac35.model.AdNetworkScoreDto;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AdEngineBackend.class)
public class FilterServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(FilterServiceTest.class);

    @Autowired
    private CacheService cacheServiceInstance;

    @Autowired
    private FilterService filterServiceInstance;

    @Before
    public void initializeTest(){
        cacheServiceInstance.initializeCache(TestConfiguration.AdNetworkScoresFileName, TestConfiguration.ExcludedAdNetworksFileName);
    }

    @Test
    public void getRelevantScores_Works_ForKnownCountry()
    {        
        AdNetworkContextDto context = new AdNetworkContextDto();
        context.setPlatform("platform");
        context.setOsVersion("osVersion");
        context.setAppName("appName");
        context.setAppVersion("appVersion");
        context.setCountryCodeIso2("fr");

        List<AdNetworkScoreDto> result = filterServiceInstance.getRelevantScores(context, cacheServiceInstance.getImmutableScores(), cacheServiceInstance.getImmutableExcludedNetworks());
        logger.info("getRelevantScores_Works_ForKnownCountry: number of results={}", result.size());

        assertNotNull(result);
        assertTrue(result.size() > 0);
    }
}