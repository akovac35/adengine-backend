package com.github.akovac35.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.akovac35.model.AdNetworkContextDto;
import com.github.akovac35.model.AdNetworkScoreDto;
import com.github.akovac35.model.ExcludedAdNetworkDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class FilterService
{
    private static final Logger logger = LoggerFactory.getLogger(FilterService.class);
    private static final String ANY = "*";
    private static final String NONE = "-";

    @Autowired
    public FilterService(@Value("${com.github.akovac35.filterServiceMinAdNetworksPerAdType}") int minAdNetworksPerAdType)
    {
        if(logger.isTraceEnabled())
            logger.trace("ctor: {}", minAdNetworksPerAdType);
        
        filterServiceMinAdNetworksPerAdType = minAdNetworksPerAdType;
    }

    private int filterServiceMinAdNetworksPerAdType;

    /**
     * Gets relevant ad networks based on context and cache contents.
     * @return Returns descending sorted collection of ad networks by score. The collection includes all ad network types with the score being the only sorting property.
     */
    public List<AdNetworkScoreDto> getRelevantScores(AdNetworkContextDto context, List<AdNetworkScoreDto> immutableScores, List<ExcludedAdNetworkDto> immutableExcludedNetworks)
    {
        if(logger.isTraceEnabled())
            logger.trace("getRelevantScores: {}, {}, {}", context, immutableScores.size(), immutableExcludedNetworks.size());
        
        List<ExcludedAdNetworkDto> toExclude = immutableExcludedNetworks.stream()
            .filter(item -> item.getCountryCodeIso2() == ANY || item.getCountryCodeIso2() == context.getCountryCodeIso2().toLowerCase())
            .filter(item -> item.getAppName() == ANY || item.getAppName() == context.getAppName().toLowerCase())
            .filter(item -> item.getPlatform() == ANY || item.getPlatform() == context.getPlatform().toLowerCase())
            .filter(item -> item.getOsVersion() == ANY || item.getOsVersion() == context.getOsVersion().toLowerCase())
            .filter(item -> item.getAppVersion() == ANY || item.getAppVersion() == context.getAppVersion().toLowerCase())
            .collect(Collectors.toList());

        List<ExcludedAdNetworkDto> toExcludeIfPresent = toExclude.stream()
            .filter(item -> item.getExcludeIfThisAdNamePresent() != NONE)
            .collect(Collectors.toList());

        toExclude = toExclude.stream()
            .filter(item -> item.getExcludeIfThisAdNamePresent() == NONE)
            .collect(Collectors.toList());

        List<AdNetworkScoreDto> scores = new ArrayList<AdNetworkScoreDto>(immutableScores);
        scores.removeIf(item -> item.getCountryCodeIso2() != context.getCountryCodeIso2().toLowerCase());
        
        for (ExcludedAdNetworkDto toExcludeItem : toExclude) {
            scores.removeIf(item -> item.getAdName() == toExcludeItem.getAdName());
        }
        for (ExcludedAdNetworkDto toExcludeIfPresentItem : toExcludeIfPresent) {
            boolean isPresent = scores.stream().anyMatch(item -> item.getAdName() == toExcludeIfPresentItem.getExcludeIfThisAdNamePresent());
            if(isPresent)
                scores.removeIf(item -> item.getAdName() == toExcludeIfPresentItem.getAdName());
        }
        
        // Verify that we have minimum number of items of each type, and append unfiltered if not
        Map<String, Long> countByAdType = scores.stream()
            .collect(Collectors.groupingBy(AdNetworkScoreDto::getAdType, Collectors.counting()));

        for (Map.Entry<String, Long> countByAdTypeItem : countByAdType.entrySet()) {
            // Filters may be too strict, erroneous, not enough ad networks ...
            if(countByAdTypeItem.getValue() < filterServiceMinAdNetworksPerAdType)
            {
                logger.warn("getRelevantScores: filtered too many ad networks for ad type {} - additional ad networks will be added", countByAdTypeItem.getKey());

                // Just add the top few networks of this type regardless of exclusions etc.
                List<AdNetworkScoreDto> addition = immutableScores.stream()
                    .filter(item -> item.getAdType() == countByAdTypeItem.getKey())
                    .sorted(Comparator.comparingDouble(AdNetworkScoreDto::getAdScore).reversed()) // Descending
                    .limit(filterServiceMinAdNetworksPerAdType - countByAdTypeItem.getValue()) // Take first n
                    .collect(Collectors.toList());
                scores.addAll(addition);
            }
        }

        scores = scores.stream()
            .sorted(Comparator.comparing(AdNetworkScoreDto::getAdScore).reversed())
            .collect(Collectors.toList());
        
        return scores;
    }
}