package com.zebra.showcaseapp.data;

/**
 * Created by Chandan Jana on 28-10-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.AutoMigrationSpec;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * The Room database.
 */
@Database(entities = {ContentProviderModel.class, MasterAppModel.class, DemoAvailabilityModel.class,
        UpdateDemoAppModel.class, ShowcaseAppAnalytics.class, AppUpdateModel.class, AppSettingsModel.class,
        BetaAppModel.class}, autoMigrations = {
        @AutoMigration(from = 1, to = 2, spec = ShowcaseDatabase.MyExampleAutoMigration.class),
        @AutoMigration(from = 2, to = 3, spec = ShowcaseDatabase.MyExampleAutoMigration.class),
        @AutoMigration(from = 3, to = 4, spec = ShowcaseDatabase.MyExampleAutoMigration.class),
        @AutoMigration(from = 4, to = 5, spec = ShowcaseDatabase.MyExampleAutoMigration.class),
        @AutoMigration(from = 5, to = 6, spec = ShowcaseDatabase.MyExampleAutoMigration.class),
        @AutoMigration(from = 6, to = 7, spec = ShowcaseDatabase.MyExampleAutoMigration.class)
}, version = 7, exportSchema = true)
@TypeConverters(DateConverter.class)
public abstract class ShowcaseDatabase extends RoomDatabase {

    /**
     * The only instance
     */
    private static ShowcaseDatabase sInstance;

    /**
     * Gets the singleton instance of SampleDatabase.
     *
     * @param context The context.
     * @return The singleton instance of SampleDatabase.
     */
    public static synchronized ShowcaseDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room
                    .databaseBuilder(context.getApplicationContext(), ShowcaseDatabase.class, "ex")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
            //sInstance.populateInitialData();
        }
        return sInstance;
    }

    /**
     * Switches the internal implementation with an empty in-memory database.
     *
     * @param context The context.
     */
    @VisibleForTesting
    public static void switchToInMemory(Context context) {
        sInstance = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                ShowcaseDatabase.class).build();
    }

    static class MyExampleAutoMigration implements AutoMigrationSpec {
        @Override
        public void onPostMigrate(SupportSQLiteDatabase database) {
            // Invoked once auto migration is done

        }
    }

    /**
     * @return The DAO for the Cheese table.
     */
    @SuppressWarnings("WeakerAccess")
    public abstract ContentProviderDao launcherDao();

    public abstract MasterAppDAO masterAppDAO();

    public abstract DemoAvailabilityDAO demoAvailabalityDAO();

    public abstract UpdateDemoAppDAO updateDemoAppDAO();

    public abstract ShowcaseAppAnalyticsDAO appAnalyticsDAO();

    public abstract AppUpdateDAO appUpdateDAO();

    public abstract AppSettingsDAO appSettingsDAO();

    public abstract BetaAppDAO betaAppDAO();

}
