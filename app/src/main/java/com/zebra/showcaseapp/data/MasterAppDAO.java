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
public interface MasterAppDAO {

    @Query("SELECT * FROM demoappmaster ORDER BY ID")
    List<MasterAppModel> loadAllMasterAppModel();

    @Query("SELECT masterAppName FROM demoappmaster ORDER BY ID")
    List<String> loadAllMasterAppName();

    @Query("SELECT * FROM demoappmaster WHERE isUpdateAvailable = :isUpdateAvailable ORDER BY ID")
    Cursor loadAllMasterAppModel(boolean isUpdateAvailable);

    @Query("SELECT * FROM demoappmaster WHERE isUpdateAvailable = :isUpdateAvailable AND masterAppName = :appName")
    MasterAppModel loadAllMasterAppModel(String appName, boolean isUpdateAvailable);

    @Query("SELECT * FROM demoappmaster WHERE isUpdateAvailable = :isUpdateAvailable AND masterAppName = :appName")
    Cursor loadAllMasterAppModel1(String appName, boolean isUpdateAvailable);

    @Query("SELECT * FROM demoappmaster ORDER BY id DESC LIMIT 1")
    MasterAppModel selectLastMasterAppModel();

    @Query("SELECT * FROM demoappmaster WHERE masterAppName = :masterAppName")
    MasterAppModel selectMasterAppModel(String masterAppName);

    @Query("SELECT * FROM demoappmaster WHERE isUpdateAvailable = :isUpdateAvailable")
    List<MasterAppModel> selectMasterAppModel(boolean isUpdateAvailable);

    @Query("SELECT * FROM demoappmaster WHERE masterAppName = :masterAppName")
    Cursor selectSingleMasterAppModel(String masterAppName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMasterAppModel(MasterAppModel model);

    @Query("UPDATE demoappmaster SET isUpdateAvailable = :isUpdateAvailable  WHERE id = :masterId")
    int updateMasterAppModel(int masterId, boolean isUpdateAvailable);

    @Update
    void updateMasterAppModel(MasterAppModel model);

    @Delete
    void deleteMasterAppModel(MasterAppModel model);

    @Query("SELECT * FROM demoappmaster WHERE id = :id")
    MasterAppModel loadMasterAppModelById(int id);
}
