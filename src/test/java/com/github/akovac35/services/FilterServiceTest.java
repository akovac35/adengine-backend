package com.github.akovac35.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest()
public class FilterServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(FilterServiceTest.class);
    private boolean initialized = false;
    
    @Autowired
    private CacheService cacheServiceInstance;

    @Autowired
    private FilterService filterServiceInstance;

    @Before
    public void initializeTest(){
        if(!initialized)
        {
        cacheServiceInstance.initializeCache(TestConfiguration.AdNetworkScoresFileName, TestConfiguration.ExcludedAdNetworksFileName);
            initialized = true;
        }
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
        assertTrue(result.size() == 29);
        assertTrue(result.get(0).getAdScore() > result.get(result.size()-1).getAdScore());
    }

    @Test
    public void getRelevantScores_Appends_WhenTooFewAdNetworksPerType()
    {       
        // The following ad network supports any country
        String adName = "aleksander";
        
        AdNetworkContextDto context = new AdNetworkContextDto();
        context.setPlatform("platform");
        context.setOsVersion("osVersion");
        context.setAppName("appName");
        context.setAppVersion("appVersion");
        context.setCountryCodeIso2("00"); // Does not exist

        List<AdNetworkScoreDto> result = filterServiceInstance.getRelevantScores(context, cacheServiceInstance.getImmutableScores(), cacheServiceInstance.getImmutableExcludedNetworks());
        logger.info("getRelevantScores_Appends_WhenTooFewAdNetworksPerType: number of results={}", result.size());

        assertNotNull(result);
        // Three ad network types ...
        assertTrue(result.size() == 3 * filterServiceInstance.getFilterServiceMinAdNetworksPerAdType());
        assertTrue(result.stream().anyMatch(item -> adName.equals(item.getAdName())));
        assertTrue(result.get(0).getAdScore() > result.get(result.size()-1).getAdScore());
    }

    @Test
    public void getRelevantScores_Works_ContextWithAnyCountry()
    {        
        AdNetworkContextDto context = new AdNetworkContextDto();
        context.setPlatform("platform");
        context.setOsVersion("osVersion");
        context.setAppName("appName");
        context.setAppVersion("appVersion");
        context.setCountryCodeIso2("*"); // Any country

        List<AdNetworkScoreDto> result = filterServiceInstance.getRelevantScores(context, cacheServiceInstance.getImmutableScores(), cacheServiceInstance.getImmutableExcludedNetworks());
        logger.info("getRelevantScores_Works_ContextWithAnyCountry: number of results={}", result.size());

        assertNotNull(result);
        assertTrue(result.size() == 234);
        assertTrue(result.get(0).getAdScore() > result.get(result.size()-1).getAdScore());
    }

    @Test
    public void getRelevantScores_FacebookInChina_IsExcluded()
    {        
        String adName = "facebook ads";
        String country = "cn";
        assertTrue(cacheServiceInstance.getImmutableScores().stream().anyMatch(item -> country.equals(item.getCountryCodeIso2()) && adName.equals(item.getAdName())));
        
        AdNetworkContextDto context = new AdNetworkContextDto();
        context.setPlatform("platform");
        context.setOsVersion("osVersion");
        context.setAppName("appName");
        context.setAppVersion("appVersion");
        context.setCountryCodeIso2(country);

        List<AdNetworkScoreDto> result = filterServiceInstance.getRelevantScores(context, cacheServiceInstance.getImmutableScores(), cacheServiceInstance.getImmutableExcludedNetworks());
        logger.info("getRelevantScores_FacebookInChina_IsExcluded: number of results={}", result.size());

        assertNotNull(result);
        assertTrue(result.size() == 118);
        assertFalse(result.stream().anyMatch(item -> adName.equals(item.getAdName())));
        assertTrue(result.get(0).getAdScore() > result.get(result.size()-1).getAdScore());
    }

    @Test
    public void getRelevantScores_AdMobAndroid9_IsExcluded()
    {        
        String adName = "admob";
        String country = "*";
        assertTrue(cacheServiceInstance.getImmutableScores().stream().anyMatch(item -> adName.equals(item.getAdName())));
        
        AdNetworkContextDto context = new AdNetworkContextDto();
        context.setPlatform("android");
        context.setOsVersion("9");
        context.setAppName("appName");
        context.setAppVersion("appVersion");
        context.setCountryCodeIso2(country);

        List<AdNetworkScoreDto> result = filterServiceInstance.getRelevantScores(context, cacheServiceInstance.getImmutableScores(), cacheServiceInstance.getImmutableExcludedNetworks());
        logger.info("getRelevantScores_AdMobAndroid9_IsExcluded: number of results={}", result.size());

        assertNotNull(result);
        assertTrue(result.size() == 234);
        assertFalse(result.stream().anyMatch(item -> adName.equals(item.getAdName())));
        assertTrue(result.get(0).getAdScore() > result.get(result.size()-1).getAdScore());
    }
    
    @Test
    public void getRelevantScores_AdMobOptOut_IsExcludedBecauseAdMobIsPresent()
    {        
        String adName = "admob-optout";
        String country = "*";
        assertTrue(cacheServiceInstance.getImmutableScores().stream().anyMatch(item -> adName.equals(item.getAdName())));
        assertTrue(cacheServiceInstance.getImmutableScores().stream().anyMatch(item -> "admob".equals(item.getAdName())));

        AdNetworkContextDto context = new AdNetworkContextDto();
        context.setPlatform("platform");
        context.setOsVersion("osVersion");
        context.setAppName("appName");
        context.setAppVersion("appVersion");
        context.setCountryCodeIso2(country);

        List<AdNetworkScoreDto> result = filterServiceInstance.getRelevantScores(context, cacheServiceInstance.getImmutableScores(), cacheServiceInstance.getImmutableExcludedNetworks());
        logger.info("getRelevantScores_AdMobOptOut_IsExcludedBecauseAdMobIsPresent: number of results={}", result.size());

        assertNotNull(result);
        assertTrue(result.size() == 234);
        assertFalse(result.stream().anyMatch(item -> adName.equals(item.getAdName())));
        assertTrue(result.get(0).getAdScore() > result.get(result.size()-1).getAdScore());
    }
}