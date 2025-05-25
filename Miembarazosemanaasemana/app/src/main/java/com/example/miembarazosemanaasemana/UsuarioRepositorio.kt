package com.example.miembarazosemanaasemana.repositorio

import com.example.miembarazosemanaasemana.bbdd.UsuarioDAO
import com.example.miembarazosemanaasemana.modelo.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepositorio(private val dao: UsuarioDAO) {

    fun buscarUsuarioPorNombre(nombre: String): Flow<Usuario?> {
        return dao.buscarPorUsuario(nombre)
    }

    suspend fun insertar(usuario: Usuario) {
        dao.insertar(usuario)
    }

    suspend fun borrar(usuario: Usuario) {
        dao.borrar(usuario)
    }

    suspend fun modificar(usuario: Usuario) {
        dao.modificar(usuario)
    }

    fun getTodos(): Flow<List<Usuario>> {
        return dao.getAllUsuarios()
    }
}
