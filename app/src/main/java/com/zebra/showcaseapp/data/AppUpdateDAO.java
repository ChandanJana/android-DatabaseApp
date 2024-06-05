package com.zebra.showcaseapp.data;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Created by Chandan Jana on 11-11-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
@Dao
public interface AppUpdateDAO {

    @Query("SELECT * FROM appupdate ORDER BY ID")
    List<AppUpdateModel> loadAllAppUpdateModel();


    @Query("SELECT * FROM appupdate WHERE appName = :appName")
    AppUpdateModel lastAppUpdateModel(String appName);

    @Query("SELECT * FROM appupdate WHERE isUpdateAvailable = :isUpdateAvailable")
    Cursor selectLastAppUpdateModel(boolean isUpdateAvailable);

    @Query("SELECT * FROM appupdate WHERE isUpdateAvailable = :isUpdateAvailable AND appName = :appName")
    AppUpdateModel selectLastAppUpdateModel(boolean isUpdateAvailable, String appName);

    @Query("UPDATE appupdate SET isUpdateAvailable = :isUpdateAvailable, appLink = :appLink, version = :version WHERE appName = :appName")
    int updateAppUpdateModelByName(String appName, String version, boolean isUpdateAvailable, String appLink);

    @Query("UPDATE appupdate SET isUpdateAvailable = :isUpdateAvailable WHERE appName = :appName")
    int updateAppUpdateModelByName(String appName, boolean isUpdateAvailable);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAppUpdateModel(AppUpdateModel model);

    @Update
    void updateAppUpdateModel(AppUpdateModel model);

    @Delete
    void deleteAppUpdateModel(AppUpdateModel model);

    @Query("DELETE FROM appupdate WHERE appName  = :appName")
    int deleteAppUpdateModel(String appName);

    @Query("SELECT * FROM appupdate WHERE id = :id")
    AppUpdateModel loadAppUpdateModelById(int id);
}
