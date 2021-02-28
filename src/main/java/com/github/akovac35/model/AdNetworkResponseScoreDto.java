package com.github.akovac35.model;

import java.util.List;
import java.util.stream.Collectors;

public class AdNetworkResponseScoreDto {
    private String adName;
    private double adScore;
    private String adType;

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public double getAdScore() {
        return adScore;
    }

    public void setAdScore(double adScore) {
        this.adScore = adScore;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public static List<AdNetworkResponseScoreDto> fromAdNetworkScoreDto(List<AdNetworkScoreDto> scores) {
        if(scores == null) throw new IllegalArgumentException("Argument is null: scores");
        
        List<AdNetworkResponseScoreDto> result = scores.stream().map(item -> {
            AdNetworkResponseScoreDto tmp = new AdNetworkResponseScoreDto();

            tmp.adName = item.getAdName();
            tmp.adScore = item.getAdScore();
            tmp.adType = item.getAdType();

            return tmp;
        }).collect(Collectors.toList());

        return result;
    }
}