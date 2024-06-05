package com.zebra.showcaseapp.data;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DemoAvailabilityDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDemoAvailabalityModel(DemoAvailabilityModel model);

    @Query("SELECT * FROM DemoAvailability")
    Cursor getDemoAvailableOrNot();

    @Query("SELECT * FROM DemoAvailability")
    DemoAvailabilityModel isDemoAvailableOrNot();

    @Delete()
    int deleteData(DemoAvailabilityModel model);


}
