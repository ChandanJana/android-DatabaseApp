package com.zebra.showcaseapp.data;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;

/**
 * Created by Chandan Jana on 30-03-2023.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
@Dao
public interface BetaAppDAO {

    @Query("SELECT * FROM betaapp ORDER BY ID")
    BetaAppModel loadAllBetaAppModel();

    @Query("SELECT * FROM betaapp WHERE betaAppName = :betaAppName")
    Cursor getDemoBetaAppModel(String betaAppName);

    @Query("SELECT * FROM betaapp WHERE isDownloaded = :isDownloaded")
    BetaAppModel selectLastBetaAppModel(boolean isDownloaded);

    @Query("UPDATE betaapp SET isDownloaded = :isDownload WHERE betaAppName = :betaAppName")
    int updateBetaAppModelByName(String betaAppName, boolean isDownload);

    @Query("UPDATE betaapp SET status = :status WHERE betaAppName = :betaAppName")
    int updateBetaAppModelByName(String betaAppName, int status);

    @Query("UPDATE betaapp SET isDownloaded = :isDownload, status = :status WHERE betaAppName = :betaAppName")
    int updateBetaAppModelByName(String betaAppName, boolean isDownload, int status);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertBetaAppModel(BetaAppModel model);

    @Update
    void updateBetaAppModel(BetaAppModel model);

    @Delete
    void deleteBetaAppModel(BetaAppModel model);

    @Query("DELETE FROM betaapp")
    int deleteBetaAppModel();

    @Query("SELECT * FROM betaapp WHERE betaAppName = :betaAppName")
    BetaAppModel loadBetaAppModelByName(String betaAppName);
}
