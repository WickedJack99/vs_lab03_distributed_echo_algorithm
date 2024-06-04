package com.hso.AppInfo;

/**
 * @author Aaron Moser
 */

public class AppInfo {

    private final EAppType appType;

    public AppInfo(EAppType appType) {
        this.appType = appType;
    }

    public EAppType getAppType() {
        return appType;
    }
}
