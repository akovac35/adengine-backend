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

    public static List<ExcludedAdNetworkDto> fromCsv(List<String[]> csv) {
        if(csv == null) throw new IllegalArgumentException("Argument is null: csv");
        
        // Skip the header line
        List<ExcludedAdNetworkDto> result = csv.stream().skip(1).map(item -> {
            ExcludedAdNetworkDto tmp = new ExcludedAdNetworkDto();

            tmp.adName = item[0];
            tmp.platform = item[1];
            tmp.osVersion = item[2];
            tmp.appName = item[3];
            tmp.appVersion = item[4];
            tmp.countryCodeIso2 = item[5];

            return tmp;
        }).collect(Collectors.toList());

        return result;
    }
}