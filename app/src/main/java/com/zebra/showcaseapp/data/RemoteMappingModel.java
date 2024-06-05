package com.zebra.showcaseapp.data;

import java.util.Date;

/**
 * Created by Chandan Jana on 16-11-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
public class RemoteMappingModel {

    private String appNames;
    private Date updateDate;
    private String description;
    private String appVersion;

    public String getAppNames() {
        return appNames;
    }

    public void setAppNames(String appNames) {
        this.appNames = appNames;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Override
    public String toString() {
        return "RemoteMappingModel{" +
                "appNames='" + appNames + '\'' +
                ", updateDate=" + updateDate +
                ", description='" + description + '\'' +
                ", appVersion='" + appVersion + '\'' +
                '}';
    }
}
