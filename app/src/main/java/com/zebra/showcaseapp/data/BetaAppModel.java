package com.zebra.showcaseapp.data;

import static com.zebra.showcaseapp.data.BetaAppModel.TABLE_NAME;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Chandan Jana on 30-03-2023.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */

@Entity(tableName = TABLE_NAME)
public class BetaAppModel {

    public static final String TABLE_NAME = "BetaApp";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "betaAppName")
    private String betaAppName;

    @ColumnInfo(name = "betaType")
    private String betaType;

    @ColumnInfo(name = "isDownloaded")
    private boolean isDownloaded;

    @ColumnInfo(name = "status")
    private int status = -1;

    @ColumnInfo(name = "refId")
    private String refId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBetaAppName() {
        return betaAppName;
    }

    public void setBetaAppName(String betaAppName) {
        this.betaAppName = betaAppName;
    }

    public String getBetaType() {
        return betaType;
    }

    public void setBetaType(String betaType) {
        this.betaType = betaType;
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

    @Override
    public String toString() {
        return "BetaAppModel{" +
                "id=" + id +
                ", betaAppName='" + betaAppName + '\'' +
                ", betaType='" + betaType + '\'' +
                ", isDownloaded=" + isDownloaded +
                ", status=" + status +
                ", refId='" + refId + '\'' +
                '}';
    }
}
