package com.zebra.showcaseapp.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Chandan Jana on 11-11-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */

@Entity(tableName = "mediaFoldersApp")
public class MediaFoldersAppModel {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "mediaFoldersName")
    private String mediaFoldersName;

    @ColumnInfo(name = "foldersId")
    private int foldersId;

    @ColumnInfo(name = "masterId")
    private int masterId;

    @ColumnInfo(name = "isUpdateAvailable")
    private boolean isUpdateAvailable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMediaFoldersName() {
        return mediaFoldersName;
    }

    public void setMediaFoldersName(String mediaFoldersName) {
        this.mediaFoldersName = mediaFoldersName;
    }

    public int getFoldersId() {
        return foldersId;
    }

    public void setFoldersId(int foldersId) {
        this.foldersId = foldersId;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        isUpdateAvailable = updateAvailable;
    }

    @Override
    public String toString() {
        return "MediaFilesAppModel{" +
                "id=" + id +
                ", mediaFoldersName='" + mediaFoldersName + '\'' +
                ", foldersId=" + foldersId +
                ", masterId=" + masterId +
                ", isUpdateAvailable=" + isUpdateAvailable +
                '}';
    }
}
