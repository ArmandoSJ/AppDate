package com.example.appdate.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*
import android.arch.persistence.room.ForeignKey
import java.sql.Time

@Entity(tableName = "cita_entity")
data class DateEntity (
    @PrimaryKey(autoGenerate = true)
    var idcita:Int = 0,
    var nomper:String="",
    var fechacita:String="",
    var horacita: String="",
    var fecha: Date,
    var Status:String ="",
    var idlocacion:String="",
    var idUsuario:String=""
)