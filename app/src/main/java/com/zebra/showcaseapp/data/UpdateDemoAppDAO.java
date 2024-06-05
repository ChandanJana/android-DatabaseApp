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
 * Created by Chandan Jana on 11-11-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
@Dao
public interface UpdateDemoAppDAO {

    @Query("SELECT * FROM updatedemoapp ORDER BY ID")
    UpdateDemoAppModel loadAllDemoAppFilesModel();

    @Query("SELECT * FROM updatedemoapp ORDER BY ID")
    Cursor getDemoAppFilesModel();

    @Query("SELECT * FROM updatedemoapp WHERE isUpdateAvailable = :isUpdateAvailable")
    UpdateDemoAppModel selectLastDemoAppFilesModel(boolean isUpdateAvailable);

    @Query("UPDATE updatedemoapp SET lastModifiedDate = :date, isDownloaded = :isDownload, isUpdateAvailable = :isUpdateAvailable WHERE fileName = :fileName")
    int updateDemoAppFilesModelByName(String fileName, boolean isDownload, boolean isUpdateAvailable, Date date);

    @Query("UPDATE updatedemoapp SET lastModifiedDate = :date, isDownloaded = :isDownload, isUpdateAvailable = :isUpdateAvailable, status = :status WHERE fileName = :fileName")
    int updateDemoAppFilesModelByName(String fileName, boolean isDownload, boolean isUpdateAvailable, int status, Date date);

    @Query("UPDATE updatedemoapp SET status = :status WHERE fileName = :fileName")
    int updateDemoAppFilesModelByName(String fileName, int status);

    @Query("UPDATE updatedemoapp SET isDownloaded = :isDownload, isUpdateAvailable = :isUpdateAvailable, status = :status WHERE fileName = :fileName")
    int updateDemoAppFilesModelByName(String fileName, boolean isDownload, boolean isUpdateAvailable, int status);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDemoAppFilesModelModel(UpdateDemoAppModel model);

    @Update
    void updateDemoAppFilesModel(UpdateDemoAppModel model);

    @Delete
    void deleteDemoAppFilesModel(UpdateDemoAppModel model);

    @Query("DELETE FROM updatedemoapp")
    int deleteUpdateDemo();

    @Query("SELECT * FROM updatedemoapp WHERE id = :id")
    UpdateDemoAppModel loadDemoAppFilesModelById(int id);
}
