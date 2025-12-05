package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rar.academiquiz1.databinding.ActivitySettingsBinding
import com.rar.academiquiz1.databinding.DialogEditProfileBinding
import com.rar.academiquiz1.models.Usuario
import com.rar.academiquiz1.repositories.UsuarioRepository
import com.rar.academiquiz1.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * Actividad donde el usuario puede ver y editar su perfil.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var usuarioRepository: UsuarioRepository
    
    private var currentUser: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        usuarioRepository = UsuarioRepository()

        setupToolbar()
        setupListeners()
        cargarDatosUsuario()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Configuración"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            currentUser?.let { mostrarDialogoEditarPerfil(it) }
        }

        binding.btnChangePassword.setOnClickListener {
            Toast.makeText(this, "Funcionalidad de cambio de contraseña no implementada", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cargarDatosUsuario() {
        // Cargar datos locales de sesión primero
        binding.tvUserName.text = sessionManager.getUserName()
        binding.tvUserEmail.text = sessionManager.getUserEmail()
        binding.tvUserRole.text = "Rol: ${sessionManager.getUserRol()}"

        // Cargar estadísticas si es estudiante
        if (sessionManager.getUserRol() == "ESTUDIANTE") {
            binding.cardStats.visibility = View.VISIBLE
            // TODO: Cargar estadísticas reales
        } else {
            binding.cardStats.visibility = View.GONE
        }

        // Actualizar con datos de red
        val userId = sessionManager.getUserId() ?: return
        lifecycleScope.launch {
            usuarioRepository.obtenerUsuario(userId).onSuccess { usuario ->
                currentUser = usuario
                binding.tvUserName.text = usuario.nombre
                binding.tvUserEmail.text = usuario.email
                binding.tvUserRole.text = "Rol: ${usuario.rol}"
            }
        }
    }

    private fun mostrarDialogoEditarPerfil(usuario: Usuario) {
        val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
        dialogBinding.etNombre.setText(usuario.nombre)

        AlertDialog.Builder(this)
            .setTitle("Editar Perfil")
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = dialogBinding.etNombre.text.toString().trim()
                if (nuevoNombre.isNotEmpty()) {
                    guardarCambios(usuario, nuevoNombre)
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarCambios(usuario: Usuario, nuevoNombre: String) {
        usuario.nombre = nuevoNombre
        
        lifecycleScope.launch {
            usuarioRepository.actualizarUsuario(usuario).onSuccess {
                sessionManager.saveUser(usuario)
                cargarDatosUsuario()
                Toast.makeText(this@SettingsActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this@SettingsActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        sessionManager.clearSession()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
