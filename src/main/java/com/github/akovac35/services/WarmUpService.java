package com.github.akovac35.services;

import java.util.HashMap;

import com.github.akovac35.model.AdNetworkContextDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/_ah/warmup")
@RestController
@ConditionalOnProperty(value = "com.github.akovac35.isTest", havingValue = "false")
public class WarmUpService {
    private static final Logger logger = LoggerFactory.getLogger(WarmUpService.class);

    public WarmUpService(CacheService cacheServiceInstance, FilterService filterServiceInstance) {
        if (logger.isTraceEnabled())
            logger.trace("ctor");

        this.cacheServiceInstance = cacheServiceInstance;
        this.filterServiceInstance = filterServiceInstance;
    }

    protected final CacheService cacheServiceInstance;
    protected final FilterService filterServiceInstance;

    @GetMapping()
    public String warmup() {
        if (logger.isTraceEnabled())
            logger.trace("warmup");

        filterServiceInstance.getRelevantScores(AdNetworkContextDto.fromMap(new HashMap<String, String>()),
                cacheServiceInstance.getImmutableScores(), cacheServiceInstance.getImmutableExcludedNetworks());

        return "Warmup successful";
    }
}