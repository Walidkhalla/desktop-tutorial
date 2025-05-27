package com.example.miembarazosemanaasemana

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
                binding.btnModificarDatos.setOnClickListener {
                    findNavController().navigate(R.id.action_FirstFragment_to_modificarFragment)
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
            SemanaInfo(1, "Aunque técnicamente aún no estás embarazada, los médicos cuentan la semana 1 desde el primer día de tu última menstruación.", "Preparación", R.drawable.ic_launcher_foreground),
            SemanaInfo(2, "El óvulo es fecundado por un espermatozoide. En este momento, empiezan a formarse las primeras células que darán lugar a tu bebé.", "Fecundación", R.drawable.ic_launcher_foreground),
            SemanaInfo(3, "El óvulo fecundado, ahora llamado blastocisto, se implanta en el útero. Tu cuerpo comienza a producir la hormona del embarazo, hCG.", "Implantación", R.drawable.ic_launcher_foreground),
            SemanaInfo(4, "Tu bebé, que ahora es oficialmente un embrión, tiene el tamaño de una semillita de amapola (1-2 mm). Se está formando el tubo neural que se convertirá en el cerebro y la médula espinal.", "Semilla de amapola", R.drawable.semana3),
            SemanaInfo(5, "Tu bebé mide aproximadamente 5 mm, como una semilla de sésamo. El corazón comienza a latir y se están formando las extremidades.", "Semilla de sésamo", R.drawable.ic_launcher_foreground),
            SemanaInfo(6, "Tu bebé mide unos 8 mm, similar a una lenteja. Se están formando los ojos, oídos y sistema digestivo.", "Lenteja", R.drawable.ic_launcher_foreground),
            SemanaInfo(7, "Tu bebé ha crecido hasta los 13 mm, como un arándano. Se están desarrollando los riñones y el hígado.", "Arándano", R.drawable.ic_launcher_foreground),
            SemanaInfo(8, "Tu bebé mide aproximadamente 16 mm, como un frijol. Los dedos de manos y pies comienzan a formarse.", "Frijol", R.drawable.ic_launcher_foreground),
            SemanaInfo(9, "Tu bebé ahora mide unos 2.5 cm, como una uva. Todos los órganos esenciales están formándose.", "Uva", R.drawable.ic_launcher_foreground),
            SemanaInfo(10, "Tu bebé mide aproximadamente 3 cm, como una fresa. Ahora se le considera un feto y no un embrión.", "Fresa", R.drawable.ic_launcher_foreground),
            SemanaInfo(11, "Tu bebé mide unos 4 cm, como una lima. Los párpados están formados pero permanecen cerrados.", "Lima", R.drawable.ic_launcher_foreground),
            SemanaInfo(12, "Tu bebé mide aproximadamente 5-6 cm, como un limón. Está comenzando a moverse, aunque aún no puedas sentirlo.", "Limón", R.drawable.ic_launcher_foreground),
            SemanaInfo(13, "Tu bebé mide unos 7 cm, como un melocotón. Las huellas dactilares se están formando.", "Melocotón", R.drawable.ic_launcher_foreground),
            SemanaInfo(14, "Tu bebé mide aproximadamente 8.5 cm, como una naranja. Puede hacer expresiones faciales y succionar su pulgar.", "Naranja", R.drawable.ic_launcher_foreground),
            SemanaInfo(15, "Tu bebé mide unos 10 cm, como una manzana. El cabello comienza a crecer.", "Manzana", R.drawable.ic_launcher_foreground),
            SemanaInfo(16, "Tu bebé mide aproximadamente 12 cm, como un aguacate. Puede mover sus ojos aunque los párpados estén cerrados.", "Aguacate", R.drawable.ic_launcher_foreground),
            SemanaInfo(17, "Tu bebé mide unos 13 cm, como una pera. Está acumulando grasa debajo de la piel.", "Pera", R.drawable.ic_launcher_foreground),
            SemanaInfo(18, "Tu bebé mide aproximadamente 14 cm, como un pimiento. Puedes comenzar a sentir sus movimientos.", "Pimiento", R.drawable.ic_launcher_foreground),
            SemanaInfo(19, "Tu bebé mide unos 15 cm, como un mango. Sus órganos sensoriales continúan desarrollándose.", "Mango", R.drawable.ic_launcher_foreground),
            SemanaInfo(20, "Tu bebé mide aproximadamente 16 cm, como un plátano. Se pueden ver sus dedos y uñas claramente.", "Plátano", R.drawable.ic_launcher_foreground),
            SemanaInfo(21, "Tu bebé mide unos 26 cm, como una zanahoria. Está desarrollando un patrón regular de sueño y vigilia.", "Zanahoria", R.drawable.ic_launcher_foreground),
            SemanaInfo(22, "Tu bebé mide aproximadamente 28 cm, como un pepino. Sus cejas y pestañas son visibles.", "Pepino", R.drawable.ic_launcher_foreground),
            SemanaInfo(23, "Tu bebé mide unos 30 cm, como una berenjena. Los pulmones se están preparando para respirar aire.", "Berenjena", R.drawable.ic_launcher_foreground),
            SemanaInfo(24, "Tu bebé mide aproximadamente 32 cm, como una mazorca de maíz. Su piel es aún transparente pero se vuelve más opaca.", "Mazorca de maíz", R.drawable.ic_launcher_foreground),
            SemanaInfo(25, "Tu bebé mide unos 34 cm, como un coliflor. Está engordando y sus movimientos son más fuertes.", "Coliflor", R.drawable.ic_launcher_foreground),
            SemanaInfo(26, "Tu bebé mide aproximadamente 35 cm, como una lechuga. Sus ojos comienzan a abrirse y cerrarse.", "Lechuga", R.drawable.ic_launcher_foreground),
            SemanaInfo(27, "Tu bebé mide unos 36 cm, como una coliflor. Su cerebro está creciendo rápidamente.", "Coliflor", R.drawable.ic_launcher_foreground),
            SemanaInfo(28, "Tu bebé mide aproximadamente 38 cm, como una berenjena grande. Puede responder a sonidos del exterior.", "Berenjena grande", R.drawable.ic_launcher_foreground),
            SemanaInfo(29, "Tu bebé mide unos 39 cm, como un pomelo. Sus huesos se están endureciendo.", "Pomelo", R.drawable.ic_launcher_foreground),
            SemanaInfo(30, "Tu bebé mide aproximadamente 40 cm, como un repollo. Sus movimientos son más limitados debido al espacio.", "Repollo", R.drawable.ic_launcher_foreground),
            SemanaInfo(31, "Tu bebé mide unos 41 cm, como un coco. Está ganando peso rápidamente.", "Coco", R.drawable.ic_launcher_foreground),
            SemanaInfo(32, "Tu bebé mide aproximadamente 42 cm, como una piña. Practica la respiración moviendo el diafragma.", "Piña", R.drawable.ic_launcher_foreground),
            SemanaInfo(33, "Tu bebé mide unos 43 cm, como un melón. Su sistema inmune se está desarrollando.", "Melón", R.drawable.ic_launcher_foreground),
            SemanaInfo(34, "Tu bebé mide aproximadamente 45 cm, como una papaya. Sus uñas llegan hasta la punta de los dedos.", "Papaya", R.drawable.ic_launcher_foreground),
            SemanaInfo(35, "Tu bebé mide unos 46 cm, como una melón honeydew. Está practicando succión y deglución.", "Melón honeydew", R.drawable.ic_launcher_foreground),
            SemanaInfo(36, "Tu bebé mide aproximadamente 47 cm, como una col rizada. El vernix (capa protectora) cubre su piel.", "Col rizada", R.drawable.ic_launcher_foreground),
            SemanaInfo(37, "Tu bebé mide unos 48 cm, como un apio. Se considera que está a término completo.", "Apio", R.drawable.ic_launcher_foreground),
            SemanaInfo(38, "Tu bebé mide aproximadamente 49 cm, como un calabacín. Está listo para nacer en cualquier momento.", "Calabacín", R.drawable.ic_launcher_foreground),
            SemanaInfo(39, "Tu bebé mide unos 50 cm, como una sandía pequeña. Los pulmones están completamente maduros.", "Sandía pequeña", R.drawable.ic_launcher_foreground),
            SemanaInfo(40, "Tu bebé mide aproximadamente 51 cm, como una sandía. ¡El gran día está muy cerca!", "Sandía", R.drawable.ic_launcher_foreground)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
