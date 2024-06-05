package com.zebra.showcaseapp.data;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by Chandan Jana on 11-11-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
@Dao
public interface DemoAppFilesDAO {

    @Query("SELECT * FROM demoappfiles ORDER BY ID")
    List<DemoAppFilesModel> loadAllDemoAppFilesModel();

    @Query("SELECT * FROM demoappfiles WHERE masterId = :masterId AND folderId = :foldersId AND fileName = :fileName")
    DemoAppFilesModel loadAllDemoAppFilesModel(int masterId, int foldersId, String fileName);

    @Query("SELECT * FROM demoappfiles WHERE masterId = :masterId AND folderId = :foldersId  AND mediaFoldersId = :mediaFoldersId AND fileName = :fileName")
    DemoAppFilesModel loadAllDemoAppFilesModelForMedia(int masterId, int foldersId, int mediaFoldersId, String fileName);

    @Query("SELECT * FROM demoappfiles WHERE masterId = :masterId AND folderId = :foldersId  AND isUpdateAvailable = :isUpdateAvailable")
    Cursor loadAllDemoAppFilesModelForMedia(int masterId, int foldersId, boolean isUpdateAvailable);

    @Query("SELECT * FROM demoappfiles WHERE masterId = :masterId AND folderId IN (:foldersIds)  AND isUpdateAvailable = :isUpdateAvailable")
    Cursor loadAllDemoAppFilesModelForMedia(int masterId, List<Integer> foldersIds, boolean isUpdateAvailable);

    @Query("SELECT * FROM demoappfiles ORDER BY id DESC LIMIT 1")
    DemoAppFilesModel selectLastDemoAppFilesModel();

    @Query("UPDATE demoappfiles SET lastModifiedDate = :date, isDownloaded = :isDownload, isUpdateAvailable = :isUpdateAvailable, fileUri = :uri  WHERE masterId = :masterId AND folderId = :folderId AND fileName = :fileName")
    int updateDemoAppFilesModelByName(int masterId, int folderId, String fileName, boolean isDownload, boolean isUpdateAvailable, String uri, Date date);

    @Query("UPDATE demoappfiles SET lastModifiedDate = :date, isDownloaded = :isDownload, isUpdateAvailable = :isUpdateAvailable, fileUri = :uri  WHERE masterId = :masterId AND folderId = :folderId AND mediaFoldersId = :mediaFolderId AND fileName = :fileName")
    int updateDemoAppFilesModelByNameForMedia(int masterId, int folderId, int mediaFolderId, String fileName, boolean isDownload, boolean isUpdateAvailable, String uri, Date date);

    @Query("UPDATE demoappfiles SET isUpdateAvailable = :isUpdateAvailable, isDownloaded = :isDownloaded WHERE id = :fileId AND masterId = :masterId AND folderId = :folderId AND mediaFoldersId = :mediaFolderId ")
    int updateDemoAppFilesModelForMedia(int fileId, int masterId, int folderId, int mediaFolderId, boolean isUpdateAvailable, boolean isDownloaded);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDemoAppFilesModelModel(DemoAppFilesModel model);

    @Update
    void updateDemoAppFilesModel(DemoAppFilesModel model);

    @Delete
    void deleteDemoAppFilesModel(DemoAppFilesModel model);

    @Query("SELECT * FROM demoappfiles WHERE id = :id")
    DemoAppFilesModel loadDemoAppFilesModelById(int id);
}
