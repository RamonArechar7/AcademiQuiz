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

/**
 * Actividad de inicio de sesión.
 * Maneja la autenticación de usuarios mediante Firebase Auth y valida credenciales.
 */
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

        // Verificar si ya hay sesión activa
        if (sessionManager.isLoggedIn() && auth.currentUser != null) {
            irAlDashboard()
            return
        }

        setupListeners()
    }

    /**
     * Configura los listeners de los botones de login y registro.
     */
    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Inicia el proceso de login con Firebase.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     */
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        obtenerDatosUsuario(userId)
                    }
                } else {
                    Toast.makeText(this, "Error de autenticación: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Recupera los datos extendidos del usuario desde Firestore y guarda la sesión local.
     * @param userId ID del usuario autenticado.
     */
    private fun obtenerDatosUsuario(userId: String) {
        lifecycleScope.launch {
            val result = usuarioRepository.obtenerUsuario(userId)
            
            result.onSuccess { usuario ->
                if (usuario != null) {
                    sessionManager.saveUserSession(usuario)
                    usuarioRepository.actualizarUltimoAcceso(userId)
                    irAlDashboard()
                } else {
                    Toast.makeText(this@LoginActivity, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                Toast.makeText(this@LoginActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun irAlDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}