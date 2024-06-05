package com.zebra.showcaseapp.data;

import static com.zebra.showcaseapp.data.AppSettingsModel.TABLE_NAME;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Chandan Jana on 22-03-2023.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */

@Entity(tableName = TABLE_NAME)
public class AppSettingsModel {

    public static final String TABLE_NAME = "AppSettings";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "settingsName")
    private String settingsName;

    @ColumnInfo(name = "isEnable")
    private boolean isEnable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSettingsName() {
        return settingsName;
    }

    public void setSettingsName(String settingsName) {
        this.settingsName = settingsName;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    @NonNull
    public static AppSettingsModel fromContentValues(@Nullable ContentValues values) {
        final AppSettingsModel appSettingsModel = new AppSettingsModel();
        if (values != null && values.containsKey("id")) {
            appSettingsModel.id = values.getAsInteger("id");
        }
        if (values != null && values.containsKey("settingsName")) {
            appSettingsModel.settingsName = values.getAsString("settingsName");
        }
        if (values != null && values.containsKey("isEnable")) {
            appSettingsModel.isEnable = Boolean.parseBoolean(values.getAsString("isEnable").trim());
        }

        return appSettingsModel;
    }

    @Override
    public String toString() {
        return "AppSettingsModel{" +
                "id=" + id +
                ", settingsName='" + settingsName + '\'' +
                ", isEnable=" + isEnable +
                '}';
    }
}
