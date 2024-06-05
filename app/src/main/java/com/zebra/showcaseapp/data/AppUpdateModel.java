package com.zebra.showcaseapp.data;

import static com.zebra.showcaseapp.data.AppUpdateModel.TABLE_NAME;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Chandan Jana on 27-02-2023.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */

@Entity(tableName = TABLE_NAME)
public class AppUpdateModel {

    public static final String TABLE_NAME = "AppUpdate";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "appName")
    private String appName;

    @ColumnInfo(name = "appLink")
    private String appLink;

    @ColumnInfo(name = "version")
    private String version;

    @ColumnInfo(name = "isUpdateAvailable")
    private boolean isUpdateAvailable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppLink() {
        return appLink;
    }

    public void setAppLink(String appLink) {
        this.appLink = appLink;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        isUpdateAvailable = updateAvailable;
    }

    @Override
    public String toString() {
        return "AppUpdateModel{" +
                "id=" + id +
                ", appName='" + appName + '\'' +
                ", appLink='" + appLink + '\'' +
                ", version='" + version + '\'' +
                ", isUpdateAvailable=" + isUpdateAvailable +
                '}';
    }
}
