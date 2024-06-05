package com.zebra.showcaseapp.data;

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

@Entity(tableName = "DemoAppFiles")
public class DemoAppFilesModel {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "fileName")
    private String fileName;

    @ColumnInfo(name = "isDownloaded")
    private boolean isDownloaded;

    @ColumnInfo(name = "refId")
    private String refId;

    @ColumnInfo(name = "lastModifiedDate")
    @TypeConverters(DateConverter.class)
    private Date lastModifiedDate;

    @ColumnInfo(name = "folderId")
    private int folderId;

    @ColumnInfo(name = "masterId")
    private int masterId;

    @ColumnInfo(name = "mediaFoldersId")
    private int mediaFoldersId;

    @ColumnInfo(name = "fileUri")
    private String fileUri;

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

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public int getMediaFoldersId() {
        return mediaFoldersId;
    }

    public void setMediaFoldersId(int mediaFoldersId) {
        this.mediaFoldersId = mediaFoldersId;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    @Override
    public String toString() {
        return "DemoAppFilesModel{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", isDownloaded=" + isDownloaded +
                ", refId='" + refId + '\'' +
                ", lastModifiedDate=" + lastModifiedDate +
                ", folderId=" + folderId +
                ", masterId=" + masterId +
                ", mediaFoldersId=" + mediaFoldersId +
                ", fileUri='" + fileUri + '\'' +
                ", isUpdateAvailable=" + isUpdateAvailable +
                '}';
    }
}
