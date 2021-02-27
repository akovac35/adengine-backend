package com.github.akovac35.api;

import java.util.List;

import com.github.akovac35.model.AdNetworkContextDto;
import com.github.akovac35.model.AdNetworkScoreDto;
import com.github.akovac35.services.CacheService;
import com.github.akovac35.services.FilterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/adnetworkscore")
public class AdNetworkScoreApi
{
    private static final Logger logger = LoggerFactory.getLogger(AdNetworkScoreApi.class);

    @Autowired
    private CacheService cacheServiceInstance;

    @Autowired
    private FilterService filterServiceInstance;

    @GetMapping
    public List<AdNetworkScoreDto> getScores(@RequestBody AdNetworkContextDto context)
    {
        if(logger.isTraceEnabled())
            logger.trace("getScores: {}", context);
        
        List<AdNetworkScoreDto> results =
            filterServiceInstance.getRelevantScores(context, cacheServiceInstance.getImmutableScores(), cacheServiceInstance.getImmutableExcludedNetworks());
        
        if(logger.isTraceEnabled())
            logger.trace("getScores: results.size={}", results.size());
        return results;
    }
}