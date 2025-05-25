package com.example.miembarazosemanaasemana

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.miembarazosemanaasemana.repositorio.UsuarioRepositorio
import com.example.miembarazosemanaasemana.viewmodel.AppViewModel
import com.example.miembarazosemanaasemana.viewmodel.UsuarioViewModel

class UsuarioViewModelFactory(private val repo: UsuarioRepositorio) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UsuarioViewModel::class.java) -> {
                UsuarioViewModel(repo) as T
            }
            modelClass.isAssignableFrom(AppViewModel::class.java) -> {
                AppViewModel(repo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

