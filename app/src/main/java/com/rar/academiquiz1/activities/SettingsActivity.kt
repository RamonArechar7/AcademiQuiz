package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.rar.academiquiz1.R
import com.rar.academiquiz1.databinding.ActivitySettingsBinding
import com.rar.academiquiz1.repositories.ResultadoRepository
import com.rar.academiquiz1.repositories.UsuarioRepository
import com.rar.academiquiz1.utils.SessionManager
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var auth: FirebaseAuth
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var resultadoRepository: ResultadoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()
        usuarioRepository = UsuarioRepository()
        resultadoRepository = ResultadoRepository()

        setupToolbar()
        setupUI()
        setupListeners()
        cargarEstadisticas()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupUI() {
        val userName = sessionManager.getUserName() ?: "Usuario"
        val userEmail = sessionManager.getUserEmail() ?: "email@example.com"
        val userRole = sessionManager.getUserRol() ?: "ESTUDIANTE"

        binding.tvUserName.text = userName
        binding.tvUserEmail.text = userEmail
        binding.tvUserRole.text = "Rol: ${getRoleDisplayName(userRole)}"

        // Mostrar estadísticas solo para estudiantes
        if (userRole != "ESTUDIANTE") {
            binding.cardStats.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            mostrarDialogoEditarPerfil()
        }

        binding.btnChangePassword.setOnClickListener {
            mostrarDialogoCambiarPassword()
        }

        binding.btnLogout.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cargarEstadisticas() {
        val userRole = sessionManager.getUserRol()
        if (userRole != "ESTUDIANTE") return

        val userId = sessionManager.getUserId() ?: return

        lifecycleScope.launch {
            val result = resultadoRepository.obtenerResultadosPorUsuario(userId)
            
            result.onSuccess { resultados ->
                val totalQuizzes = resultados.size
                val promedio = if (resultados.isNotEmpty()) {
                    resultados.map { it.puntaje }.average()
                } else 0.0

                binding.tvTotalQuizzes.text = totalQuizzes.toString()
                binding.tvAvgScore.text = String.format("%.1f%%", promedio)
            }
        }
    }

    private fun mostrarDialogoEditarPerfil() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.etNombre)
        
        etNombre.setText(sessionManager.getUserName())

        AlertDialog.Builder(this)
            .setTitle("Editar Perfil")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = etNombre.text.toString().trim()
                if (nuevoNombre.isNotEmpty()) {
                    actualizarPerfil(nuevoNombre)
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoCambiarPassword() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val etCurrentPassword = dialogView.findViewById<TextInputEditText>(R.id.etCurrentPassword)
        val etNewPassword = dialogView.findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<TextInputEditText>(R.id.etConfirmPassword)

        AlertDialog.Builder(this)
            .setTitle("Cambiar Contraseña")
            .setView(dialogView)
            .setPositiveButton("Cambiar") { _, _ ->
                val currentPassword = etCurrentPassword.text.toString()
                val newPassword = etNewPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

                if (validarCambioPassword(currentPassword, newPassword, confirmPassword)) {
                    cambiarPassword(currentPassword, newPassword)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun validarCambioPassword(current: String, new: String, confirm: String): Boolean {
        if (current.isEmpty() || new.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        if (new.length < 6) {
            Toast.makeText(this, "La nueva contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }

        if (new != confirm) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun actualizarPerfil(nuevoNombre: String) {
        val userId = sessionManager.getUserId() ?: return

        lifecycleScope.launch {
            val result = usuarioRepository.obtenerUsuario(userId)
            
            result.onSuccess { usuario ->
                if (usuario != null) {
                    usuario.nombre = nuevoNombre
                    
                    usuarioRepository.actualizarUsuario(usuario).onSuccess {
                        sessionManager.saveUser(usuario)
                        binding.tvUserName.text = nuevoNombre
                        Toast.makeText(this@SettingsActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    }.onFailure { e ->
                        Toast.makeText(this@SettingsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun cambiarPassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser
        val email = user?.email ?: return

        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                auth.signOut()
                sessionManager.clearSession()
                
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun getRoleDisplayName(role: String): String {
        return when (role) {
            "ESTUDIANTE" -> "Estudiante"
            "MAESTRO" -> "Profesor"
            "ADMIN" -> "Administrador"
            else -> role
        }
    }
}
