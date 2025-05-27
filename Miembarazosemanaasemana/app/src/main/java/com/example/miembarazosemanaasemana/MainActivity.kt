package com.example.miembarazosemanaasemana

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.miembarazosemanaasemana.databinding.ActivityMainBinding
import com.example.miembarazosemanaasemana.bbdd.UsuarioDatabase
import com.example.miembarazosemanaasemana.repositorio.UsuarioRepositorio
import com.example.miembarazosemanaasemana.viewmodel.AppViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var appViewModel: AppViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ViewModel
        val dao = UsuarioDatabase.getDatabase(this).usuarioDAO()
        val repo = UsuarioRepositorio(dao)
        val factory = UsuarioViewModelFactory(repo)
        appViewModel = ViewModelProvider(this, factory)[AppViewModel::class.java]

        // Toolbar y NavController
        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_home -> {
                // Navegar a la pantalla de inicio (login)
                navController.navigate(R.id.loginFragment)
                showToast("Pantalla de inicio")
                true
            }
            R.id.action_back -> {
                if (navController.previousBackStackEntry != null) {
                    navController.navigateUp()
                } else {
                    showToast("No hay pantalla anterior")
                }
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_exit -> {
                finishAffinity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        // Limpiar SharedPreferences
        getSharedPreferences("usuario", Context.MODE_PRIVATE).edit().clear().apply()

        // Limpiar datos del ViewModel
        appViewModel.logout()

        // Volver al login
        navController.navigate(R.id.loginFragment)

        showToast("Sesión cerrada")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
