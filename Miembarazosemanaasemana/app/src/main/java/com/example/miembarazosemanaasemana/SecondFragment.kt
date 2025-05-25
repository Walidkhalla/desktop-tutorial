package com.example.miembarazosemanaasemana

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.miembarazosemanaasemana.bbdd.UsuarioDatabase
import com.example.miembarazosemanaasemana.databinding.FragmentSecondBinding
import com.example.miembarazosemanaasemana.modelo.Usuario
import com.example.miembarazosemanaasemana.repositorio.UsuarioRepositorio
import com.example.miembarazosemanaasemana.viewmodel.UsuarioViewModel


class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: UsuarioViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel setup
        val dao = UsuarioDatabase.getDatabase(requireContext()).usuarioDAO()
        val repo = UsuarioRepositorio(dao)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UsuarioViewModel(repo) as T
            }
        })[UsuarioViewModel::class.java]

        binding.btnInsertar.setOnClickListener {
            val usuario = binding.editUsuario.text.toString().trim()
            val nombre = binding.editNombre.text.toString().trim()
            val fecha = binding.editFecha.text.toString().trim()

            if (usuario.isEmpty() || nombre.isEmpty() || fecha.isEmpty()) {
                Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevo = Usuario(usuario, nombre, fecha)
            viewModel.insertar(nuevo)

            // Guardar sesión
            val prefs = requireActivity().getSharedPreferences("datos", Context.MODE_PRIVATE)
            with(prefs.edit()) {
                putString("usuario", usuario)
                apply()
            }

            Toast.makeText(requireContext(), "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        binding.btnBuscar.setOnClickListener {
            val userId = binding.editUsuario.text.toString().trim()
            if (userId.isNotEmpty()) {
                viewModel.buscarUsuario(userId)
                viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
                    if (usuario != null) {
                        binding.txtResultado.text = "Hola, ${usuario.nombre}.\nÚltima regla: ${usuario.fechaRegla}"
                    } else {
                        binding.txtResultado.text = "Usuario no encontrado"
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
