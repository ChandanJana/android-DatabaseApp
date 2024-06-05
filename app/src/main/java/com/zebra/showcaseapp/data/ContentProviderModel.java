package com.zebra.showcaseapp.data;

import android.content.ContentValues;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Chandan Jana on 28-10-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
@Entity(tableName = ContentProviderModel.TABLE_NAME)
public class ContentProviderModel {
    /**
     * The name of the Cheese table.
     */
    public static final String TABLE_NAME = "launcher";

    /**
     * The name of the ID column.
     */
    public static final String LAUNCHER_ID = BaseColumns._ID;

    /**
     * The name of the name column.
     */
    public static final String PACKAGE_NAME = "package_name";

    /**
     * The name of the name column.
     */
    public static final String UUID = "uuid";

    /**
     * The name of the name column.
     */
    public static final String UNIQUEID = "uniqueid";

    /**
     * The name of the name column.
     */
    public static final String SHOWCASEDEMO_RUNNING_OR_NOT = "showcasedemoRunningOrNot";

    /**
     * The unique ID of the cheese.
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true, name = LAUNCHER_ID)
    public long id;

    /**
     * The name of the cheese.
     */
    @ColumnInfo(name = PACKAGE_NAME)
    public String package_name;

    /**
     * The name of the cheese.
     */
    @ColumnInfo(name = UUID)
    public String uuid;

    /**
     * The name of the cheese.
     */
    @ColumnInfo(name = UNIQUEID)
    public String uniqueid;

    @ColumnInfo(name = SHOWCASEDEMO_RUNNING_OR_NOT)
    public boolean showcasedemoRunningOrNot;

    @NonNull
    public static ContentProviderModel fromContentValues(@Nullable ContentValues values) {
        final ContentProviderModel contentProviderModel = new ContentProviderModel();
        if (values != null && values.containsKey(LAUNCHER_ID)) {
            contentProviderModel.id = values.getAsLong(LAUNCHER_ID);
        }
        if (values != null && values.containsKey(PACKAGE_NAME)) {
            contentProviderModel.package_name = values.getAsString(PACKAGE_NAME);
        }
        if (values != null && values.containsKey(UUID)) {
            contentProviderModel.uuid = values.getAsString(UUID);
        }
        if (values != null && values.containsKey(UNIQUEID)) {
            contentProviderModel.uniqueid = values.getAsString(UNIQUEID);
        }
        if (values != null && values.containsKey(SHOWCASEDEMO_RUNNING_OR_NOT)) {
            contentProviderModel.showcasedemoRunningOrNot = values.getAsBoolean(SHOWCASEDEMO_RUNNING_OR_NOT);
        }
        return contentProviderModel;
    }
}
