package com.zebra.showcaseapp.data;

import static com.zebra.showcaseapp.data.UpdateDemoAppModel.TABLE_NAME;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

/**
 * Created by Chandan Jana on 11-11-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */

@Entity(tableName = TABLE_NAME)
public class UpdateDemoAppModel {

    public static final String TABLE_NAME = "UpdateDemoApp";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "fileName")
    private String fileName;

    @ColumnInfo(name = "isDownloaded")
    private boolean isDownloaded;

    @ColumnInfo(name = "status")
    private int status = -1;

    @ColumnInfo(name = "refId")
    private String refId;

    @ColumnInfo(name = "lastModifiedDate")
    @TypeConverters(DateConverter.class)
    private Date lastModifiedDate;

    @ColumnInfo(name = "isUpdateAvailable")
    private boolean isUpdateAvailable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        isUpdateAvailable = updateAvailable;
    }

    @Override
    public String toString() {
        return "UpdateDemoAppModel{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", isDownloaded=" + isDownloaded +
                ", status=" + status +
                ", refId='" + refId + '\'' +
                ", lastModifiedDate=" + lastModifiedDate +
                ", isUpdateAvailable=" + isUpdateAvailable +
                '}';
    }
}
