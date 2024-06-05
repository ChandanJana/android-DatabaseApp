package com.zebra.showcaseapp.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Created by Chandan Jana on 22-03-2023.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
@Dao
public interface AppSettingsDAO {

    @Query("SELECT * FROM appsettings ORDER BY id")
    List<AppSettingsModel> loadAllAppSettingsModel();

    @Query("SELECT * FROM appsettings WHERE settingsName = :appName")
    AppSettingsModel lastAppSettingsModel(String appName);

    @Query("SELECT * FROM appsettings WHERE isEnable = :isEnable AND settingsName = :settingsName")
    AppSettingsModel selectLastAppSettingsModel(boolean isEnable, String settingsName);

    @Query("UPDATE appsettings SET isEnable = :isEnable WHERE settingsName = :settingsName")
    int updateAppSettingsModelByName(String settingsName, boolean isEnable);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAppSettingsModel(AppSettingsModel model);

    @Update
    void updateAppSettingsModel(AppSettingsModel model);

    @Delete
    void deleteAppSettingsModel(AppSettingsModel model);

    @Query("DELETE FROM appsettings WHERE settingsName  = :settingsName")
    int deleteAppSettingsModel(String settingsName);

    @Query("SELECT * FROM appsettings WHERE id = :id")
    AppSettingsModel loadAppSettingsModelById(int id);
}
