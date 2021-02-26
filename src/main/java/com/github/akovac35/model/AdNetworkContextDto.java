package com.github.akovac35.model;

public class AdNetworkContextDto
{
    private String platform;
    private String osVersion;
	private String appName;
	private String appVersion;
	private String countryCodeIso2;
    private String adType;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(final String platform) {
        this.platform = platform;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(final String osVersion) {
        this.osVersion = osVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(final String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(final String appVersion) {
        this.appVersion = appVersion;
    }

    public String getCountryCodeIso2() {
        return countryCodeIso2;
    }

    public void setCountryCodeIso2(final String countryCodeIso2) {
        this.countryCodeIso2 = countryCodeIso2;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(final String adType) {
        this.adType = adType;
    }

    @Override
    public String toString() {
        return "AdNetworkContextDto [" + 
        "platform=" + platform + 
        ", osVersion=" + osVersion + 
        ", appName=" + appName + 
        ", appVersion=" + appVersion + 
        ", countryCodeIso2=" + countryCodeIso2 + 
        ", adType=" + adType + 
        "]";
    }
}