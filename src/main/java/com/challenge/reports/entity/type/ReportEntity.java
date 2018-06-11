package com.challenge.reports.entity.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ReportEntity {

    @JsonProperty("desktop web")
    DESKTOP,
    @JsonProperty("mobile web")
    MOBILE,
    @JsonProperty("android")
    ANDROID,
    @JsonProperty("iOS")
    IOS;

    // temporary solution. JsonCreator or Graphql can be used instead to support normal input for Controllers.
    public static ReportEntity getByName(String name) {
        if (name.contains("_")) {
            name = name.replace("_", " ");
        }
        if (name.equals("desktop web")) return DESKTOP;
        if (name.equals("mobile web")) return MOBILE;
        if (name.equals("android")) return ANDROID;
        if (name.equals("iOS")) return IOS;
        throw new RuntimeException("No such report type: " + name);
    }
}
