package com.example.appdate.database

import android.arch.persistence.room.*

@Dao
interface LocationDao {

    // @Query("select * from cita_entity where status =1")
    // fun getAll(): List<DateEntity>

    // @Query("select * from cita_entity where id = :idArg")
    //fun findById(idArg: Int): DateEntity
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