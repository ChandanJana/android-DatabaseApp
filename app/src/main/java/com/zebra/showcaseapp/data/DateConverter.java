package com.zebra.showcaseapp.data;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Created by Chandan Jana on 14-11-2022.
 * Company name: Mindteck
 * Email: chandan.jana@mindteck.com
 */
public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        if (date == null) {
            return null;
        }

        return date.getTime();
    }
}
