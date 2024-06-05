package com.zebra.showcaseapp.data;

import static com.zebra.showcaseapp.data.ShowcaseAppAnalytics.TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.zebra.showcaseapp.util.Utils;

/**
 * Created by Chandan Jana on 14-12-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
@Entity(tableName = TABLE_NAME)
public class ShowcaseAppAnalytics {

    public static final String TABLE_NAME = "ShowcaseAppAnalytics";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "appName")
    private String appName;

    @ColumnInfo(name = "deviceName")
    private String deviceName;

    @ColumnInfo(name = "deviceSerialNo")
    private String deviceSerialNo;

    @ColumnInfo(name = "eventName")
    private String eventName;

    @ColumnInfo(name = "activityDate")
    private String activityDate;

    @NonNull
    public static ShowcaseAppAnalytics fromContentValues(@Nullable ContentValues values, Context context) {
        final ShowcaseAppAnalytics showcaseAppAnalytics = new ShowcaseAppAnalytics();
        if (values != null && values.containsKey("id")) {
            showcaseAppAnalytics.id = values.getAsInteger("id");
        }
        if (values != null && values.containsKey("appName")) {
            showcaseAppAnalytics.appName = values.getAsString("appName");
        }
        if (values != null && values.containsKey("eventName")) {
            showcaseAppAnalytics.eventName = values.getAsString("eventName");
        }
        showcaseAppAnalytics.deviceName = Utils.getDeviceName();
        showcaseAppAnalytics.deviceSerialNo = Utils.getSerial();
        showcaseAppAnalytics.activityDate = Utils.getCurrentDateTime();
        return showcaseAppAnalytics;
    }

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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceSerialNo() {
        return deviceSerialNo;
    }

    public void setDeviceSerialNo(String deviceSerialNo) {
        this.deviceSerialNo = deviceSerialNo;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    @Override
    public String toString() {
        return "ShowcaseAppAnalytics{" +
                "id=" + id +
                ", appName='" + appName + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceSerialNo='" + deviceSerialNo + '\'' +
                ", eventName='" + eventName + '\'' +
                ", activityDate='" + activityDate + '\'' +
                '}';
    }
}
