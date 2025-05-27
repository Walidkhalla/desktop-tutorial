package com.example.miembarazosemanaasemana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miembarazosemanaasemana.adapter.SemanaAdapter
import com.example.miembarazosemanaasemana.databinding.FragmentSemanasBinding
import com.example.miembarazosemanaasemana.modelo.SemanaInfo

class SemanasFragment : Fragment() {

    private lateinit var binding: FragmentSemanasBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSemanasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val lista = generarSemanas()

        binding.recyclerSemanas.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSemanas.adapter = SemanaAdapter(lista)
    }

    private fun generarSemanas(): List<SemanaInfo> {
        return listOf(
            SemanaInfo(3, "Ahora como un grano de arroz", "Grano de arroz", R.drawable.semana3),
            // tengo que compeltar las 40 semnas.
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        // Ejemplo: en Login solo mostrar logout y home
        menu.findItem(R.id.action_back)?.isVisible = false
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Cerrar sesión: limpiar prefs y volver a login
                findNavController().navigate(R.id.loginFragment)
                true
            }
            R.id.action_home -> {
                findNavController().navigate(R.id.loginFragment)
                true
            }
            R.id.action_back -> {
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}

