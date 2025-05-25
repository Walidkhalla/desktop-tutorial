package com.example.miembarazosemanaasemana.viewmodel

import androidx.lifecycle.*
import com.example.miembarazosemanaasemana.modelo.Usuario
import com.example.miembarazosemanaasemana.repositorio.UsuarioRepositorio
import kotlinx.coroutines.launch

class UsuarioViewModel(private val repo: UsuarioRepositorio) : ViewModel() {

    // Variable para observar un usuario por nombre
    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> get() = _usuario

    // Buscar un usuario por nombre (o ID si usas ID)
    fun buscarUsuario(nombre: String) {
        viewModelScope.launch {
            repo.buscarUsuarioPorNombre(nombre).collect {
                _usuario.postValue(it)
            }
        }
    }

    fun insertar(usuario: Usuario) = viewModelScope.launch {
        repo.insertar(usuario)
    }

    fun modificar(usuario: Usuario) = viewModelScope.launch {
        repo.modificar(usuario)
    }

    fun borrar(usuario: Usuario) = viewModelScope.launch {
        repo.borrar(usuario)
    }

    fun limpiar() {
        _usuario.value = null
    }

}
