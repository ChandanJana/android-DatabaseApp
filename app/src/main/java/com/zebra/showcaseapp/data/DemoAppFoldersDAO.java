package com.zebra.showcaseapp.data;

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
public interface DemoAppFoldersDAO {

    @Query("SELECT * FROM demoappfolders ORDER BY ID")
    List<DemoAppFoldersModel> loadAllDemoAppFoldersModel();

    @Query("SELECT * FROM demoappfolders WHERE masterId = :masterId")
    List<DemoAppFoldersModel> loadAllDemoAppFoldersModel(int masterId);

    @Query("SELECT * FROM demoappfolders ORDER BY id DESC LIMIT 1")
    DemoAppFoldersModel selectLastDemoAppFoldersModel();

    @Query("SELECT * FROM demoappfolders WHERE masterId = :masterId AND isUpdateAvailable = :isUpdateAvailable")
    List<DemoAppFoldersModel> selectSingleDemoAppFoldersModel(int masterId, boolean isUpdateAvailable);

    @Query("SELECT * FROM demoappfolders WHERE foldersName = :foldersName AND masterId = :masterId")
    DemoAppFoldersModel selectDemoAppFolders(String foldersName, int masterId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDemoAppFoldersModel(DemoAppFoldersModel model);

    @Query("UPDATE demoappfolders SET isUpdateAvailable = :isUpdateAvailable  WHERE masterId = :masterId AND id = :folderId")
    int updateDemoAppFoldersModel(int masterId, int folderId, boolean isUpdateAvailable);

    @Update
    void updateDemoAppFoldersModel(DemoAppFoldersModel model);

    @Delete
    void deleteDemoAppFoldersModel(DemoAppFoldersModel model);

    @Query("SELECT * FROM demoappfolders WHERE id = :id")
    DemoAppFoldersModel loadDemoAppFoldersModelById(int id);
}
