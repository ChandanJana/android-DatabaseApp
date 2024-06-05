package com.zebra.showcaseapp.data;

import static com.zebra.showcaseapp.data.DemoAvailabilityModel.TABLE_NAME;

import android.content.ContentValues;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = TABLE_NAME)
public class DemoAvailabilityModel {

    public static final String DEMO_AVAILABLE_OR_NOT = "demoAvailableOrNot";

    public static final String TABLE_NAME = "DemoAvailability";
    @ColumnInfo(name = DEMO_AVAILABLE_OR_NOT)
    public boolean demoAvailableOrNot;
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @Nullable
    public static DemoAvailabilityModel fromContentValues(@Nullable ContentValues values) {

        Log.v(DEMO_AVAILABLE_OR_NOT, "availability : " + values.containsKey(DEMO_AVAILABLE_OR_NOT));
        Log.v(DEMO_AVAILABLE_OR_NOT, "value : " + values.getAsBoolean(DEMO_AVAILABLE_OR_NOT));

        final DemoAvailabilityModel demoAvailabilityModel = new DemoAvailabilityModel();

        if (values != null && values.containsKey(DEMO_AVAILABLE_OR_NOT)) {
            demoAvailabilityModel.demoAvailableOrNot = values.getAsBoolean(DEMO_AVAILABLE_OR_NOT);
        }

        Log.v(DEMO_AVAILABLE_OR_NOT, "model : " + demoAvailabilityModel);

        return demoAvailabilityModel;

    }

    public boolean isDemoAvailableOrNot() {
        return demoAvailableOrNot;
    }

    public void setDemoAvailableOrNot(boolean demoAvailableOrNot) {
        this.demoAvailableOrNot = demoAvailableOrNot;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DemoAvailabilityModel{" +
                "id=" + id +
                ", demoAvailableOrNot=" + demoAvailableOrNot +
                '}';
    }
}
