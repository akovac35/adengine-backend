package com.github.akovac35.model;

import java.util.List;
import java.util.stream.Collectors;

public class ExcludedAdNetworkDto
{
    private String adName;
	private String platform;
	private String osVersion;
	private String appName;
	private String appVersion;
    private String countryCodeIso2;
    private String excludeIfThisAdNamePresent;

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getCountryCodeIso2() {
        return countryCodeIso2;
    }

    public void setCountryCodeIso2(String countryCodeIso2) {
        this.countryCodeIso2 = countryCodeIso2;
    }
    
    public String getExcludeIfThisAdNamePresent() {
        return excludeIfThisAdNamePresent;
    }

    public void setExcludeIfThisAdNamePresent(String excludeIfThisAdNamePresent) {
        this.excludeIfThisAdNamePresent = excludeIfThisAdNamePresent;
    }

    public static List<ExcludedAdNetworkDto> fromCsv(List<String[]> csv) {
        if(csv == null) throw new IllegalArgumentException("Argument is null: csv");
        
        // Skip the header line
        List<ExcludedAdNetworkDto> result = csv.stream().skip(1).map(item -> {
            ExcludedAdNetworkDto tmp = new ExcludedAdNetworkDto();

            tmp.adName = item[0].toLowerCase();
            tmp.countryCodeIso2 = item[1].toLowerCase();
            tmp.appName = item[2].toLowerCase();
            tmp.platform = item[3].toLowerCase();
            tmp.osVersion = item[4].toLowerCase();
            tmp.appVersion = item[5].toLowerCase();
            tmp.excludeIfThisAdNamePresent = item[6].toLowerCase();

            return tmp;
        }).collect(Collectors.toList());

        return result;
    }
}