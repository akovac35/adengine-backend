package com.github.akovac35;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.github.akovac35.model.AdNetworkContextDto;
import com.github.akovac35.model.AdNetworkResponseScoreDto;
import com.github.akovac35.services.CacheService;
import com.github.akovac35.services.FilterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RequestMapping(value = "/api/adnetworkscores")
@RestController
@ConditionalOnProperty(
    value="com.github.akovac35.isTest", 
    havingValue = "false")
public class AdEngineBackend {

	public static void main(String[] args) {
		SpringApplication.run(AdEngineBackend.class, args);
    }
    
    private static final Logger logger = LoggerFactory.getLogger(AdEngineBackend.class);

    @Value("${com.github.akovac35.scoresCsvFileName}")
    private String scoresCsvFileName;

    @Value("${com.github.akovac35.excludedNetworksCsvFileName}")
    private String excludedNetworksCsvFileName;
    
    @Autowired
    private CacheService cacheServiceInstance;

    @Autowired
    private FilterService filterServiceInstance;

    @PostConstruct
    private void postConstruct(){
        if(logger.isTraceEnabled())
            logger.trace("postConstruct");

        cacheServiceInstance.initializeCache(scoresCsvFileName, excludedNetworksCsvFileName);
        cacheServiceInstance.startCacheUpdateTimer();
    }

    @GetMapping()
    public List<AdNetworkResponseScoreDto> getScores(@RequestParam Map<String, String> context)
    {
        if(logger.isTraceEnabled())
            logger.trace("getScores: {}", context);
        
        AdNetworkContextDto tmp = AdNetworkContextDto.fromMap(context);
        
        if(logger.isTraceEnabled())
            logger.trace("getScores: context={}", tmp);

        List<AdNetworkResponseScoreDto> results = AdNetworkResponseScoreDto.fromAdNetworkScoreDto(
            filterServiceInstance.getRelevantScores(tmp, cacheServiceInstance.getImmutableScores(), cacheServiceInstance.getImmutableExcludedNetworks())
        );
        
        if(logger.isTraceEnabled())
            logger.trace("getScores: results.size={}", results.size());
        return results;
    }
}