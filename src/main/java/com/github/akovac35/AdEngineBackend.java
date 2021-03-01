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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/api")
@ConditionalOnProperty(value = "com.github.akovac35.isTest", havingValue = "false")
public class AdEngineBackend {

    public static void main(String[] args) {
        SpringApplication.run(AdEngineBackend.class, args);
    }

    private static final Logger logger = LoggerFactory.getLogger(AdEngineBackend.class);

    @Autowired
    public AdEngineBackend(@Value("${com.github.akovac35.scoresCsvFileName}") String scoresCsvFileName,
            @Value("${com.github.akovac35.excludedNetworksCsvFileName}") String excludedNetworksCsvFileName,
            CacheService cacheServiceInstance, FilterService filterServiceInstance) {
        if (logger.isTraceEnabled())
            logger.trace("ctor: {} {}", scoresCsvFileName, excludedNetworksCsvFileName);

        this.scoresCsvFileName = scoresCsvFileName;
        this.excludedNetworksCsvFileName = excludedNetworksCsvFileName;
        this.cacheServiceInstance = cacheServiceInstance;
        this.filterServiceInstance = filterServiceInstance;
    }

    protected final String scoresCsvFileName;
    protected final String excludedNetworksCsvFileName;
    protected final CacheService cacheServiceInstance;
    protected final FilterService filterServiceInstance;

    @PostConstruct
    public void postConstruct() {
        if (logger.isTraceEnabled())
            logger.trace("postConstruct");

        cacheServiceInstance.initializeCache(scoresCsvFileName, excludedNetworksCsvFileName);
        cacheServiceInstance.startCacheUpdateTimer();
    }

    @RequestMapping(value = "/adnetworkscores", method = RequestMethod.GET)
    public List<AdNetworkResponseScoreDto> getScores(@RequestParam Map<String, String> context) {
        if (logger.isTraceEnabled())
            logger.trace("getScores: {}", context);

        AdNetworkContextDto tmp = AdNetworkContextDto.fromMap(context);

        if (logger.isTraceEnabled())
            logger.trace("getScores: context={}", tmp);

        List<AdNetworkResponseScoreDto> results = AdNetworkResponseScoreDto.fromAdNetworkScoreDto(
                filterServiceInstance.getRelevantScores(tmp, cacheServiceInstance.getImmutableScores(),
                        cacheServiceInstance.getImmutableExcludedNetworks()));

        if (logger.isTraceEnabled())
            logger.trace("getScores: results.size={}", results.size());
        return results;
    }

    @RequestMapping(value = "/refreshCache", method = RequestMethod.PUT)
    public String refreshCache() {
        if (logger.isTraceEnabled())
            logger.trace("refreshCache");

        cacheServiceInstance.updateCache();

        return "Cache updated";
    }
}