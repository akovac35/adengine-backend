package com.github.akovac35.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.github.akovac35.AdEngineBackend;
import com.github.akovac35.TestConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AdEngineBackend.class)
public class CacheServiceTest
{
    @Autowired
    private CacheService cacheServiceInstance;

    @Test
    public void initializeCache_Works()
    {
        cacheServiceInstance.initializeCache(TestConfiguration.AdNetworkScoresFileName, TestConfiguration.ExcludedAdNetworksFileName);

        assertNotNull(cacheServiceInstance.getImmutableScores());
        assertTrue(cacheServiceInstance.getImmutableScores().size() > 0);

        assertNotNull(cacheServiceInstance.getImmutableExcludedNetworks());
        assertTrue(cacheServiceInstance.getImmutableExcludedNetworks().size() > 0);
    }
}