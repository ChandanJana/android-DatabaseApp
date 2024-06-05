package com.zebra.showcaseapp.data;

import static com.zebra.showcaseapp.data.MasterAppModel.TABLE_NAME;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Chandan Jana on 11-11-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */

@Entity(tableName = TABLE_NAME)
public class MasterAppModel {

    public static final String TABLE_NAME = "DemoAppMaster";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "masterAppName")
    private String masterAppName;

    @ColumnInfo(name = "isUpdateAvailable")
    private boolean isUpdateAvailable;

    @ColumnInfo(name = "appVersion")
    private String appVersion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMasterAppName() {
        return masterAppName;
    }

    public void setMasterAppName(String masterAppName) {
        this.masterAppName = masterAppName;
    }

    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        isUpdateAvailable = updateAvailable;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Override
    public String toString() {
        return "MasterAppModel{" +
                "id=" + id +
                ", masterAppName='" + masterAppName + '\'' +
                ", isUpdateAvailable=" + isUpdateAvailable +
                ", appVersion='" + appVersion + '\'' +
                '}';
    }
}
