package com.example.miembarazosemanaasemana

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.miembarazosemanaasemana.bbdd.UsuarioDatabase
import com.example.miembarazosemanaasemana.databinding.FragmentFirstBinding
import com.example.miembarazosemanaasemana.modelo.SemanaInfo
import com.example.miembarazosemanaasemana.repositorio.UsuarioRepositorio
import com.example.miembarazosemanaasemana.viewmodel.UsuarioViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val usuarioId = prefs.getString("usuarioId", null)

        if (usuarioId == null) {
            findNavController().navigate(R.id.action_FirstFragment_to_loginFragment)
            return
        }

        val dao = UsuarioDatabase.getDatabase(requireContext()).usuarioDAO()
        val repo = UsuarioRepositorio(dao)
        val viewModel = UsuarioViewModel(repo)

        viewModel.buscarUsuario(usuarioId)
        viewModel.usuario.observe(viewLifecycleOwner) { usuario ->
            if (usuario != null) {
                val semanaActual = calcularSemana(usuario.fechaRegla)

                binding.tvNombre.text = "Hola, ${usuario.nombre} 👶"
                binding.tvSemana.text = "Semana $semanaActual del embarazo"
                binding.progresoBarra.progress = semanaActual
                binding.tvProgresoTexto.text = "Semana $semanaActual de 40"


                val infoSemana = generarSemanas().find { it.semana == semanaActual }

                if (infoSemana != null) {
                    binding.tvDescripcion.text = infoSemana.descripcion
                    binding.imgFrutaActual.setImageResource(infoSemana.imagenResId)
                } else {
                    binding.tvDescripcion.text = "Aún estás en las primeras semanas del ciclo. El embarazo suele confirmarse a partir de la semana 4."
                    binding.imgFrutaActual.setImageResource(R.drawable.embrion_inicio) // Imagen genérica
                }

                binding.btnVerSemanas.setOnClickListener {
                    findNavController().navigate(R.id.action_FirstFragment_to_semanasFragment)
                }

                binding.btnLogout.setOnClickListener {
                    androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Cerrar sesión")
                        .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                        .setPositiveButton("Sí") { _, _ ->
                            prefs.edit().clear().apply()
                            viewModel.limpiar()

                            val navOptions = androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.nav_graph, true)
                                .build()

                            findNavController().navigate(R.id.loginFragment, null, navOptions)
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }

            } else {
                Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_FirstFragment_to_loginFragment)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calcularSemana(fechaRegla: String): Int {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val fecha = LocalDate.parse(fechaRegla, formatter)
            val hoy = LocalDate.now()
            val dias = Period.between(fecha, hoy).days
            val semanas = dias / 7 + Period.between(fecha, hoy).months * 4
            semanas.coerceIn(1, 40)
        } catch (e: Exception) {
            1
        }
    }

    private fun generarSemanas(): List<SemanaInfo> {
        return listOf(
            SemanaInfo(4, "Tu bebé, que ahora es oficialmente un embrión, tiene el tamaño de una semillita de amapola.", "Semilla de amapola", R.drawable.semana3),

            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
