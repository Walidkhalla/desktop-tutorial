package com.example.miembarazosemanaasemana.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miembarazosemanaasemana.modelo.Usuario
import com.example.miembarazosemanaasemana.repositorio.UsuarioRepositorio
import kotlinx.coroutines.launch

class AppViewModel(private val repo: UsuarioRepositorio) : ViewModel() {

    // Usuario actual logeado (como en el anterior proyecto)
    var usuarioActual: MutableLiveData<Usuario?> = MutableLiveData(null)

    // Insertar usuario en BBDD y actualizar LiveData
    fun insertarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repo.insertar(usuario)
            usuarioActual.value = usuario
        }
    }

    // Buscar usuario desde la BBDD
    fun buscarUsuarioPorId(id: String) {
        viewModelScope.launch {
            repo.buscarUsuarioPorNombre(id).collect { usuario ->
                usuarioActual.postValue(usuario)
            }
        }
    }

    fun borrarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repo.borrar(usuario)
            usuarioActual.postValue(null)
        }
    }

    fun modificarUsuario(usuario: Usuario) {
        viewModelScope.launch {
            repo.modificar(usuario)
            usuarioActual.postValue(usuario)
        }
    }

    fun logout() {
        usuarioActual.value = null
    }
}
