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
public interface MediaFoldersAppDAO {

    @Query("SELECT * FROM mediaFoldersApp ORDER BY ID")
    List<MediaFoldersAppModel> loadAllMediaFoldersAppModel();

    @Query("SELECT * FROM mediaFoldersApp ORDER BY id DESC LIMIT 1")
    MediaFoldersAppModel selectLastMediaFoldersAppModel();

    @Query("SELECT * FROM mediaFoldersApp WHERE masterId = :masterId AND foldersId = :foldersId AND mediaFoldersName = :mediaFoldersName ORDER BY id ")
    MediaFoldersAppModel selectMediaFoldersAppModel(int masterId, int foldersId, String mediaFoldersName);

    @Query("SELECT * FROM mediaFoldersApp WHERE masterId = :masterId AND foldersId = :mediaFoldersId AND isUpdateAvailable = :isUpdateAvailable ORDER BY id ")
    List<MediaFoldersAppModel> selectMediaFoldersAppModel(int masterId, int mediaFoldersId, boolean isUpdateAvailable);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMediaFoldersAppModel(MediaFoldersAppModel model);

    @Update
    void updateMediaFoldersAppModel(MediaFoldersAppModel model);

    @Query("UPDATE mediaFoldersApp SET isUpdateAvailable = :isUpdateAvailable  WHERE foldersId = :mediaFoldersId AND masterId = :masterId AND id = :id")
    int updateMediaFoldersAppModel(int masterId, int mediaFoldersId, int id, boolean isUpdateAvailable);

    @Query("UPDATE mediaFoldersApp SET isUpdateAvailable = :isUpdateAvailable  WHERE id = :mediaFoldersId")
    int updateMediaFoldersAppModel(int mediaFoldersId, boolean isUpdateAvailable);

    @Delete
    void deleteMediaFoldersAppModel(MediaFoldersAppModel model);

    @Query("SELECT * FROM mediaFoldersApp WHERE id = :id")
    MediaFoldersAppModel loadMediaFoldersAppModelById(int id);
}
