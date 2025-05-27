package com.example.miembarazosemanaasemana

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.miembarazosemanaasemana.databinding.FragmentLoginBinding
import com.example.miembarazosemanaasemana.viewmodel.AppViewModel

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
            val username = binding.editUsuarioLogin.text.toString().trim()
            if (username.isEmpty()) {
                Toast.makeText(context, "Ingresa un nombre de usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mostrar progreso y deshabilitar botón
            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false

            appViewModel.buscarUsuarioPorId(username).observeOnce(viewLifecycleOwner) { usuario ->
                // Ocultar progreso y reactivar botón
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true

                if (usuario != null) {
                    // Guardar usuario en SharedPreferences
                    val prefs = requireActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE)
                    prefs.edit().putString("usuarioId", usuario.usuario).apply()

                    // Navegar al siguiente fragmento
                    findNavController().navigate(R.id.action_loginFragment_to_FirstFragment)
                } else {
                    Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnIrARegistro.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_SecondFragment)
        }
    }

    // Extensión para observar solo una vez
    private fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                removeObserver(this)
                observer(value)
            }
        })
    }
}
