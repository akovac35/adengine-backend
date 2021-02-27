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
public class CacheService
{
    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    public CacheService(
        @Value("${com.github.akovac35.cacheServiceTimerIntervalSeconds}") int timerIntervalSeconds, 
        @Value("${com.github.akovac35.cacheServiceTimerEnabled}") boolean timerEnabled
    )
    {
        if(logger.isTraceEnabled())
            logger.trace("ctor: {}, {}", timerIntervalSeconds, timerEnabled);

        if(timerIntervalSeconds < 1)
            throw new IllegalArgumentException("Argument should be larger than 0: timerIntervalSeconds");

        cacheServiceTimerIntervalSeconds = timerIntervalSeconds;
        cacheServiceTimerEnabled = timerEnabled;
    }

    @Autowired
    private CsvService csvServiceInstance;

    private int cacheServiceTimerIntervalSeconds;
    private boolean cacheServiceTimerEnabled;
    private String adNetworkScoresCsvFileName;
    private String excludedAdNetworksCsvFileName;
    private List<AdNetworkScoreDto> scores = new ArrayList<AdNetworkScoreDto>();
    private List<ExcludedAdNetworkDto> excludedNetworks = new ArrayList<ExcludedAdNetworkDto>();
    
    private Timer cacheServiceTimer;
    private CacheServiceTimerTask cacheServiceTimerTask;

    public void initializeCache(String scoresCsvFileName, String excludedNetworksCsvFileName)
    {
        if(logger.isTraceEnabled())
            logger.trace("initializeCache: {}, {}", scoresCsvFileName, excludedNetworksCsvFileName);
        
        adNetworkScoresCsvFileName = scoresCsvFileName;
        excludedAdNetworksCsvFileName = excludedNetworksCsvFileName;
        
        cacheServiceTimerTask = new CacheServiceTimerTask();
        cacheServiceTimerTask.run();
    }

    public void startCacheUpdateTimer()
    {
        if(logger.isTraceEnabled())
            logger.trace("startCacheUpdateTimer");
        
        if(cacheServiceTimer != null)
            cacheServiceTimer.cancel();
        cacheServiceTimer = new Timer(true);
        
        if(cacheServiceTimerEnabled)
            cacheServiceTimer.schedule(cacheServiceTimerTask, cacheServiceTimerIntervalSeconds * 1000, cacheServiceTimerIntervalSeconds * 1000);
    }

    public void updateCache()
    {
        if(logger.isTraceEnabled())
            logger.trace("updateCache");

        cacheServiceTimerTask.run();
    }

    public List<AdNetworkScoreDto> getImmutableScores() {
        return Collections.unmodifiableList(scores);
    }

    public List<ExcludedAdNetworkDto> getImmutableExcludedNetworks() {
        return Collections.unmodifiableList(excludedNetworks);
    }

    private class CacheServiceTimerTask extends TimerTask
    {
        @Override
        public void run() {
            if(logger.isTraceEnabled())
                logger.warn("CacheServiceTimerTask.run");
            
            // We are just updating the references, so no need for thread synchronization
            try {
                List<AdNetworkScoreDto> tmpScores = AdNetworkScoreDto.fromCsv(csvServiceInstance.getCsvContents(adNetworkScoresCsvFileName));
                // Do not update the scores in case something is wrong with the score update pipeline
                if(tmpScores.size() > 0) {
                    scores = tmpScores;
                }
                else {
                    logger.warn("CacheServiceTimerTask.run: {} file is invalid, cache was not updated", adNetworkScoresCsvFileName);
                }
                
                excludedNetworks = ExcludedAdNetworkDto.fromCsv(csvServiceInstance.getCsvContents(excludedAdNetworksCsvFileName));
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}