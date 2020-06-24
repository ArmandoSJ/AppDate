package com.example.appdate.database

import android.arch.persistence.room.*

@Dao
interface UsuarioDao {

    @Query("select * from usuario_entity")
    fun loadAllRegistro(): UsuarioEntity

    @Insert
    fun insertUsuario(usuarioEntity: UsuarioEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUsuario(usuarioEntity: UsuarioEntity)
}