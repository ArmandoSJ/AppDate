package com.example.appdate.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "location_entity")

data class LocationEntity (
    @PrimaryKey
        //(autoGenerate = true)
    var idlocacion: Int = 0,
    var nomlocacion:String="",
    var fecha: Date,
    var latitud:String="",
    var longitud:String=""

)