package com.example.miembarazosemanaasemana.bbdd

import androidx.room.*
import com.example.miembarazosemanaasemana.modelo.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE usuario = :userId LIMIT 1")
    fun buscarPorUsuario(userId: String): Flow<Usuario?>

    @Query("SELECT * FROM usuarios")
    fun getAllUsuarios(): Flow<List<Usuario>>


    @Delete
    suspend fun borrar(usuario: Usuario)

    @Update
    suspend fun modificar(usuario: Usuario)
}
