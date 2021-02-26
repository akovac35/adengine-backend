package com.github.akovac35.model;

import java.util.List;
import java.util.stream.Collectors;

public class AdNetworkScoreDto {
    private String adName;
    private float adScore;
    private String adType;
    private String countryCodeIso2;

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public float getAdScore() {
        return adScore;
    }

    public void setAdScore(float adScore) {
        this.adScore = adScore;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getCountryCodeIso2() {
        return countryCodeIso2;
    }

    public void setCountryCodeIso2(String countryCodeIso2) {
        this.countryCodeIso2 = countryCodeIso2;
    }

    public static List<AdNetworkScoreDto> fromCsv(List<String[]> csv) {
        if(csv == null) throw new IllegalArgumentException("Argument is null: csv");
        
        // Skip the header line
        List<AdNetworkScoreDto> result = csv.stream().skip(1).map(item -> {
            AdNetworkScoreDto tmp = new AdNetworkScoreDto();

            tmp.adName = item[0];
            tmp.adScore = Float.parseFloat(item[1]);
            tmp.adType = item[2];
            tmp.countryCodeIso2 = item[3];

            return tmp;
        }).collect(Collectors.toList());

        return result;
    }
}