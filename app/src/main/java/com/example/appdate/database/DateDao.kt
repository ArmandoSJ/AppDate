package com.example.appdate.database

import android.arch.persistence.room.*

@Dao
interface DateDao {

   // @Query("select * from cita_entity where status =1")
   // fun getAll(): List<DateEntity>

   // @Query("select * from cita_entity where id = :idArg")
    //fun findById(idArg: Int): DateEntity

   @Query("select * from cita_entity")
   fun loadAllRegistro(): DateEntity

    @Query("select * from cita_entity where status =0")
    fun getAll(): List<DateEntity>

    @Insert
    fun insertDate(dateEntity: DateEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateDate(dateEntity: DateEntity)

    @Delete
    fun deleteDate(dateEntity: DateEntity)
}