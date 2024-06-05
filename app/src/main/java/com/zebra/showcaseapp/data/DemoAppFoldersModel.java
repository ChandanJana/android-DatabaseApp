package com.zebra.showcaseapp.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Chandan Jana on 11-11-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */

@Entity(tableName = "DemoAppFolders")
public class DemoAppFoldersModel {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "foldersName")
    private String foldersName;
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

    public String getFoldersName() {
        return foldersName;
    }

    public void setFoldersName(String foldersName) {
        this.foldersName = foldersName;
    }


    public boolean isUpdateAvailable() {
        return isUpdateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        isUpdateAvailable = updateAvailable;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    @Override
    public String toString() {
        return "ChildAppModel{" +
                "id=" + id +
                ", childAppName='" + foldersName + '\'' +
                ", masterId=" + masterId +
                ", isUpdateAvailable=" + isUpdateAvailable +
                '}';
    }
}
