package com.example.appdate.database

import java.util.*
import android.arch.persistence.room.TypeConverter

/**
 * Class to convert the date type.
 */
class DateConverter {
    @TypeConverter
    fun toDate(timestamp:Long?): Date?{
        return  if (timestamp != null) Date(timestamp) else null
    }

    @TypeConverter
    fun toTimeStamp(date: Date?): Long? = date?.time
}