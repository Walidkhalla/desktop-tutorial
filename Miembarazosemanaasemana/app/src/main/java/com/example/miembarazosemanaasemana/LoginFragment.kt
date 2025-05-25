package com.example.miembarazosemanaasemana

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.miembarazosemanaasemana.bbdd.UsuarioDatabase
import com.example.miembarazosemanaasemana.databinding.FragmentLoginBinding
import com.example.miembarazosemanaasemana.repositorio.UsuarioRepositorio
import com.example.miembarazosemanaasemana.viewmodel.AppViewModel
import com.example.miembarazosemanaasemana.viewmodel.UsuarioViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var appViewModel: AppViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appViewModel = (activity as MainActivity).appViewModel

        binding.btnLogin.setOnClickListener {
            val user = binding.editUsuarioLogin.text.toString()
            if (user.isNotBlank()) {
                appViewModel.buscarUsuarioPorId(user)
                appViewModel.usuarioActual.observe(viewLifecycleOwner) { usuario ->
                    if (usuario != null) {
                        val prefs = requireActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE)
                        prefs.edit().putString("usuarioId", usuario.usuario).apply()
                        findNavController().navigate(R.id.action_loginFragment_to_FirstFragment)
                    } else {
                        Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnIrARegistro.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_SecondFragment)
        }
    }
}
