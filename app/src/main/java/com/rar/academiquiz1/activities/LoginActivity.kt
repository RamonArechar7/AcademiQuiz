package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rar.academiquiz1.databinding.ActivityLoginBinding
import com.rar.academiquiz1.repositories.UsuarioRepository
import com.rar.academiquiz1.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        usuarioRepository = UsuarioRepository()
        sessionManager = SessionManager(this)

        // Verificar si ya está logueado y la sesión de Firebase es válida
        if (sessionManager.isLoggedIn()) {
            if (auth.currentUser != null) {
                navegarDashboard()
                return
            } else {
                // Si está marcado como logueado pero no hay usuario de Firebase, limpiar sesión
                sessionManager.clearSession()
                Toast.makeText(this, "Tu sesión ha expirado", Toast.LENGTH_SHORT).show()
            }
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validarCampos(email, password)) {
                iniciarSesion(email, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validarCampos(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.tilEmail.error = "Ingresa tu correo"
            return false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Ingresa tu contraseña"
            return false
        }
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        return true
    }

    private fun iniciarSesion(email: String, password: String) {
        mostrarCargando(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener

                lifecycleScope.launch {
                    val result = usuarioRepository.obtenerUsuario(userId)
                    mostrarCargando(false)

                    result.onSuccess { usuario ->
                        if (usuario != null) {
                            sessionManager.saveUserSession(usuario)
                            usuarioRepository.actualizarUltimoAcceso(userId)
                            navegarDashboard()
                        } else {
                            Toast.makeText(this@LoginActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }.onFailure { e ->
                        Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                mostrarCargando(false)
                Toast.makeText(this, "Error de autenticación: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarCargando(mostrar: Boolean) {
        binding.progressBar.visibility = if (mostrar) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnLogin.isEnabled = !mostrar
    }

    private fun navegarDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}