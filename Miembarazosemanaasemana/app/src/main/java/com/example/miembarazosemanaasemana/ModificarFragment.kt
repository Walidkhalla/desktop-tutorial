package com.example.miembarazosemanaasemana

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.miembarazosemanaasemana.databinding.FragmentModificarBinding
import com.example.miembarazosemanaasemana.viewmodel.AppViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ModificarFragment : Fragment() {

    private lateinit var binding: FragmentModificarBinding
    private lateinit var viewModel: AppViewModel
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentModificarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).appViewModel

        // Configuración inicial
        setupUsuarioData()
        setupDatePicker()
        setupButtons()
    }
    
    private fun setupUsuarioData() {
        val usuario = viewModel.usuarioActual.value
        if (usuario != null) {
            binding.editNombre.setText(usuario.nombre)
            binding.editFechaRegla.setText(usuario.fechaRegla)
            
            // Mostrar la fecha en el TextView en formato legible
            try {
                val date = dateFormatter.parse(usuario.fechaRegla)
                date?.let {
                    val displayFormat = SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES"))
                    binding.tvFechaSeleccionada.text = displayFormat.format(it)
                    
                    // Actualizar el calendario con la fecha guardada
                    calendar.time = it
                }
            } catch (e: Exception) {
                binding.tvFechaSeleccionada.text = "Selecciona una fecha"
            }
        }
    }
    
    private fun setupDatePicker() {
        binding.btnSeleccionarFecha.setOnClickListener {
            showDatePicker()
        }
    }
    
    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerTheme,
            { _, year, month, dayOfMonth ->
                // Actualizar el calendario
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                
                // Formatear la fecha para mostrar
                updateDateDisplay()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Configurar rango máximo hasta hoy
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        
        // Configurar rango mínimo a un año atrás
        val minDate = Calendar.getInstance()
        minDate.add(Calendar.YEAR, -1)
        datePickerDialog.datePicker.minDate = minDate.timeInMillis
        
        datePickerDialog.show()
    }
    
    private fun updateDateDisplay() {
        // Formatear fecha para mostrar en UI (formato legible)
        val displayFormat = SimpleDateFormat("dd MMMM yyyy", Locale("es", "ES"))
        binding.tvFechaSeleccionada.text = displayFormat.format(calendar.time)
        
        // Guardar fecha en formato yyyy-MM-dd en el campo oculto
        binding.editFechaRegla.setText(dateFormatter.format(calendar.time))
    }
    
    private fun setupButtons() {
        binding.btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }
    }
    
    private fun guardarCambios() {
        val nuevoNombre = binding.editNombre.text.toString().trim()
        val nuevaFecha = binding.editFechaRegla.text.toString().trim()
        val usuario = viewModel.usuarioActual.value

        when {
            nuevoNombre.isBlank() -> {
                mostrarError("Por favor ingresa tu nombre")
                binding.editNombre.requestFocus()
            }
            nuevaFecha.isBlank() -> {
                mostrarError("Por favor selecciona la fecha de tu última regla")
            }
            usuario == null -> {
                mostrarError("Error: No se encontró información del usuario")
            }
            else -> {
                // Todo está correcto, actualizar datos
                val actualizado = usuario.copy(nombre = nuevoNombre, fechaRegla = nuevaFecha)
                viewModel.modificarUsuario(actualizado)
                mostrarExito("¡Datos actualizados correctamente!")
                
                // Esperar un momento para que el usuario vea el mensaje de éxito
                view?.postDelayed({
                    findNavController().navigateUp()
                }, 1500)
            }
        }
    }
    
    private fun mostrarError(mensaje: String) {
        Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))
            .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            .show()
    }
    
    private fun mostrarExito(mensaje: String) {
        Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light))
            .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            .show()
    }
        binding.btnEliminarUsuario.setOnClickListener {
            val usuario = viewModel.usuarioActual.value
            if (usuario != null) {
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás segura de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.")
                    .setPositiveButton("Sí, eliminar") { _, _ ->
                        viewModel.borrarUsuario(usuario)
                        
                        // Mostrar mensaje de confirmación
                        Snackbar.make(binding.root, "Usuario eliminado correctamente", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
                            .setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                            .show()

                        // Limpiar sesión
                        val prefs = requireActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE)
                        prefs.edit().clear().apply()

                        // Esperar un momento y volver al login
                        view?.postDelayed({
                            findNavController().navigate(R.id.loginFragment)
                        }, 1500)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)

        // En esta pantalla mostrar solo botón de regresar
        menu.findItem(R.id.action_logout)?.isVisible = false
        menu.findItem(R.id.action_home)?.isVisible = false
        menu.findItem(R.id.action_back)?.isVisible = true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_back -> {
                // Preguntar si hay cambios sin guardar
                val nuevoNombre = binding.editNombre.text.toString().trim()
                val nuevaFecha = binding.editFechaRegla.text.toString().trim()
                val usuario = viewModel.usuarioActual.value
                
                if (usuario != null && (nuevoNombre != usuario.nombre || nuevaFecha != usuario.fechaRegla)) {
                    // Hay cambios sin guardar
                    androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Cambios sin guardar")
                        .setMessage("Tienes cambios que no han sido guardados. ¿Quieres salir sin guardar?")
                        .setPositiveButton("Salir") { _, _ ->
                            findNavController().navigateUp()
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                } else {
                    findNavController().navigateUp()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}

