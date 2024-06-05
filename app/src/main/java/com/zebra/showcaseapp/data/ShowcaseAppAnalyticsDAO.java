package com.zebra.showcaseapp.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ShowcaseAppAnalyticsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertShowcaseAppAnalytics(ShowcaseAppAnalytics model);

    @Query("SELECT * FROM ShowcaseAppAnalytics ORDER BY id")
    List<ShowcaseAppAnalytics> getAllShowcaseAppAnalytics();

    @Query("DELETE FROM ShowcaseAppAnalytics WHERE id = :id")
    int deleteShowcaseAppAnalyticsById(int id);
}
