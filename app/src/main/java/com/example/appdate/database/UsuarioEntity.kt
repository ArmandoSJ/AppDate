package com.example.appdate.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "usuario_entity")

data class UsuarioEntity (
    @PrimaryKey (autoGenerate = true)
    var idusu: Int = 0,
    var nomusu:String="",
    var correo:String="",
    var telefono:String="",
    var pwd:String="",
    var fecha: Date
)