package com.example.appdate.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(entities = arrayOf(UsuarioEntity::class, DateEntity::class,LocationEntity::class), version = 1,exportSchema = false)
@TypeConverters(DateConverter::class)

abstract class AppDatabase : RoomDatabase() {
    companion object {
        private  var Instance: AppDatabase?= null

        fun getInstance(context: Context): AppDatabase?{
            if (Instance == null){

                synchronized(AppDatabase::class){
                    Instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "citasList.db"
                    ).build()
                }
            }
            return Instance
        }
    }
    // Agrega las Opciones CRUD definidas en el paso anterior
    abstract fun usuarioDao() : UsuarioDao
    abstract fun dateDao() : DateDao

}
