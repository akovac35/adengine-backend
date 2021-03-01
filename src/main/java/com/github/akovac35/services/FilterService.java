package com.github.akovac35.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
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
public class FilterService {
    private static final Logger logger = LoggerFactory.getLogger(FilterService.class);
    protected static final String ANY = "*";
    protected static final String NONE = "-";

    @Autowired
    public FilterService(
            @Value("${com.github.akovac35.filterServiceMinAdNetworksPerAdType}") int minAdNetworksPerAdType) {
        if (logger.isTraceEnabled())
            logger.trace("ctor: {}", minAdNetworksPerAdType);

        if (minAdNetworksPerAdType < 1)
            throw new IllegalArgumentException("Argument should be larger than 0: minAdNetworksPerAdType");

        filterServiceMinAdNetworksPerAdType = minAdNetworksPerAdType;
    }

    protected final int filterServiceMinAdNetworksPerAdType;

    /**
     * Gets relevant ad networks based on context and cache contents.
     * 
     * @return Returns descending sorted collection of ad networks by score. The
     *         collection includes all ad network types with the score being the
     *         only sorting property.
     */
    public List<AdNetworkScoreDto> getRelevantScores(AdNetworkContextDto context,
            List<AdNetworkScoreDto> immutableScores, List<ExcludedAdNetworkDto> immutableExcludedNetworks) {
        if (logger.isTraceEnabled())
            logger.trace("getRelevantScores: {}, {}, {}", context, immutableScores.size(),
                    immutableExcludedNetworks.size());

        List<ExcludedAdNetworkDto> toExclude = immutableExcludedNetworks.stream()
                .filter(item -> ANY.equals(item.getCountryCodeIso2())
                        || item.getCountryCodeIso2().equals(context.getCountryCodeIso2().toLowerCase()))
                .filter(item -> ANY.equals(item.getAppName())
                        || item.getAppName().equals(context.getAppName().toLowerCase()))
                .filter(item -> ANY.equals(item.getPlatform())
                        || item.getPlatform().equals(context.getPlatform().toLowerCase()))
                .filter(item -> ANY.equals(item.getOsVersion())
                        || item.getOsVersion().equals(context.getOsVersion().toLowerCase()))
                .filter(item -> ANY.equals(item.getAppVersion())
                        || item.getAppVersion().equals(context.getAppVersion().toLowerCase()))
                .collect(Collectors.toList());

        List<ExcludedAdNetworkDto> toExcludeIfPresent = toExclude.stream()
                .filter(item -> !NONE.equals(item.getExcludeIfThisAdNamePresent())).collect(Collectors.toList());

        toExclude = toExclude.stream().filter(item -> NONE.equals(item.getExcludeIfThisAdNamePresent()))
                .collect(Collectors.toList());

        List<AdNetworkScoreDto> scores = new ArrayList<AdNetworkScoreDto>(immutableScores);
        if (!ANY.equals(context.getCountryCodeIso2().toLowerCase()))
            scores.removeIf(item -> !ANY.equals(item.getCountryCodeIso2())
                    && !item.getCountryCodeIso2().equals(context.getCountryCodeIso2().toLowerCase()));

        if (logger.isTraceEnabled())
            logger.trace("getRelevantScores: scores.size={} after filtering by country", scores.size());

        for (ExcludedAdNetworkDto toExcludeItem : toExclude) {
            scores.removeIf(item -> item.getAdName().equals(toExcludeItem.getAdName()));
        }
        for (ExcludedAdNetworkDto toExcludeIfPresentItem : toExcludeIfPresent) {
            boolean isPresent = scores.stream()
                    .anyMatch(item -> item.getAdName().equals(toExcludeIfPresentItem.getExcludeIfThisAdNamePresent()));
            if (isPresent)
                scores.removeIf(item -> item.getAdName().equals(toExcludeIfPresentItem.getAdName()));
        }
        // Remove duplicates, for example, context is set for any country
        scores = scores.stream().filter(distinctByKey(item -> item.getAdName() + item.getAdType()))
                .collect(Collectors.toList());

        if (logger.isTraceEnabled())
            logger.trace("getRelevantScores: scores.size={} after filtering by excluded", scores.size());

        // Verify that we have a minimum number of distinct items of each ad type, and
        // append unfiltered if not
        Map<String, Long> countByAdType = new HashMap<String, Long>();
        List<String> distinctAdTypes = immutableScores.stream().filter(distinctByKey(item -> item.getAdType()))
                .map(AdNetworkScoreDto::getAdType).collect(Collectors.toList());
        // We have already removed duplicates a few lines above
        for (String distinctAdTypesItem : distinctAdTypes) {
            Long count = scores.stream().filter(item -> item.getAdType().equals(distinctAdTypesItem)).count();
            countByAdType.put(distinctAdTypesItem, count);
        }

        if (logger.isTraceEnabled())
            logger.trace("getRelevantScores: countByAdType={}", countByAdType);

        for (Map.Entry<String, Long> countByAdTypeItem : countByAdType.entrySet()) {
            // Filters may be too strict, erroneous, not enough ad networks ...
            if (countByAdTypeItem.getValue() < filterServiceMinAdNetworksPerAdType) {
                logger.warn(
                        "getRelevantScores: filtered too many ad networks for ad type {} - additional ad networks will be added",
                        countByAdTypeItem.getKey());

                // Just add the top few networks of this type regardless of exclusions etc.
                List<AdNetworkScoreDto> addition = immutableScores.stream()
                        .filter(item -> item.getAdType().equals(countByAdTypeItem.getKey()))
                        .filter(distinctByKey(item -> item.getAdName()))
                        .sorted(Comparator.comparingDouble(AdNetworkScoreDto::getAdScore).reversed()) // Descending
                        .limit(filterServiceMinAdNetworksPerAdType - countByAdTypeItem.getValue()) // Take first n
                        .collect(Collectors.toList());
                scores.addAll(addition);
            }
        }

        scores = scores.stream().sorted(Comparator.comparing(AdNetworkScoreDto::getAdScore).reversed())
                .collect(Collectors.toList());

        return scores;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public int getFilterServiceMinAdNetworksPerAdType() {
        return filterServiceMinAdNetworksPerAdType;
    }
}