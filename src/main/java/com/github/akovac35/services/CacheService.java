package com.github.akovac35.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.github.akovac35.model.AdNetworkScoreDto;
import com.github.akovac35.model.ExcludedAdNetworkDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    public CacheService(
            @Value("${com.github.akovac35.cacheServiceTimerIntervalSeconds}") int cacheServiceTimerIntervalSeconds,
            @Value("${com.github.akovac35.cacheServiceTimerEnabled}") boolean cacheServiceTimerEnabled,
            CsvService csvServiceInstance) {
        if (logger.isTraceEnabled())
            logger.trace("ctor: {}, {}", cacheServiceTimerIntervalSeconds, cacheServiceTimerEnabled);

        if (cacheServiceTimerIntervalSeconds < 1)
            throw new IllegalArgumentException("Argument should be larger than 0: cacheServiceTimerIntervalSeconds");

        this.cacheServiceTimerIntervalSeconds = cacheServiceTimerIntervalSeconds;
        this.cacheServiceTimerEnabled = cacheServiceTimerEnabled;
        this.csvServiceInstance = csvServiceInstance;
    }

    protected final CsvService csvServiceInstance;
    protected final int cacheServiceTimerIntervalSeconds;
    protected final boolean cacheServiceTimerEnabled;

    protected String adNetworkScoresCsvFileName;
    protected String excludedAdNetworksCsvFileName;
    protected Timer cacheServiceTimer;
    protected CacheServiceTimerTask cacheServiceTimerTask;

    // Actual cache
    protected List<AdNetworkScoreDto> scores = new ArrayList<AdNetworkScoreDto>();
    protected List<ExcludedAdNetworkDto> excludedNetworks = new ArrayList<ExcludedAdNetworkDto>();

    public void initializeCache(String adNetworkScoresCsvFileName, String excludedAdNetworksCsvFileName) {
        if (logger.isTraceEnabled())
            logger.trace("initializeCache: {}, {}", adNetworkScoresCsvFileName, excludedAdNetworksCsvFileName);

        this.adNetworkScoresCsvFileName = adNetworkScoresCsvFileName;
        this.excludedAdNetworksCsvFileName = excludedAdNetworksCsvFileName;

        cacheServiceTimerTask = new CacheServiceTimerTask();
        cacheServiceTimerTask.run();
    }

    public void startCacheUpdateTimer() {
        if (logger.isTraceEnabled())
            logger.trace("startCacheUpdateTimer");

        if (cacheServiceTimer != null)
            cacheServiceTimer.cancel();
        cacheServiceTimer = new Timer(true);

        if (cacheServiceTimerEnabled)
            cacheServiceTimer.schedule(cacheServiceTimerTask, cacheServiceTimerIntervalSeconds * 1000,
                    cacheServiceTimerIntervalSeconds * 1000);
    }

    public void updateCache() {
        if (logger.isTraceEnabled())
            logger.trace("updateCache");

        cacheServiceTimerTask.run();
    }

    public List<AdNetworkScoreDto> getImmutableScores() {
        return Collections.unmodifiableList(scores);
    }

    public List<ExcludedAdNetworkDto> getImmutableExcludedNetworks() {
        return Collections.unmodifiableList(excludedNetworks);
    }

    private class CacheServiceTimerTask extends TimerTask {
        @Override
        public void run() {
            if (logger.isTraceEnabled())
                logger.warn("CacheServiceTimerTask.run");

            // We are just updating the references, so no need for thread synchronization
            try {
                List<AdNetworkScoreDto> tmpScores = AdNetworkScoreDto
                        .fromCsv(csvServiceInstance.getCsvContents(adNetworkScoresCsvFileName));
                // Do not update the scores in case something is wrong with the score update
                // pipeline
                if (tmpScores.size() > 0) {
                    scores = tmpScores;
                } else {
                    logger.warn("CacheServiceTimerTask.run: {} file is invalid, cache was not updated",
                            adNetworkScoresCsvFileName);
                }

                excludedNetworks = ExcludedAdNetworkDto
                        .fromCsv(csvServiceInstance.getCsvContents(excludedAdNetworksCsvFileName));
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}