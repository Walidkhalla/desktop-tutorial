package com.example.miembarazosemanaasemana.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val usuario: String,
    val nombre: String,
    val fechaRegla: String  )
