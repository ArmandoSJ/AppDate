package com.example.appdate.database

import android.arch.persistence.room.*

@Dao
interface LocationDao {

    @Query("select * from location_entity")
    fun loadAllLocation(): LocationEntity

    @Query("select * from location_entity ")
    fun getAll(): List<LocationEntity>

    @Insert
    fun insertLocation(locationEntity: LocationEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateLocation(locationEntity: LocationEntity)

    @Delete
    fun deleteLocation(locationEntity: LocationEntity)
}