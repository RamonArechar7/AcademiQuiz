package com.rar.academiquiz1.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rar.academiquiz1.databinding.ActivityRegisterBinding
import com.rar.academiquiz1.models.Usuario
import com.rar.academiquiz1.repositories.UsuarioRepository
import com.rar.academiquiz1.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        usuarioRepository = UsuarioRepository()

        setupSpinner()
        setupListeners()
    }

    private fun setupSpinner() {
        val roles = arrayOf("ESTUDIANTE", "MAESTRO", "ADMIN")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        binding.spinnerRol.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val rol = binding.spinnerRol.selectedItem.toString()

            if (validarCampos(nombre, email, password, confirmPassword)) {
                registrarUsuario(nombre, email, password, rol)
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun validarCampos(nombre: String, email: String, password: String, confirmPassword: String): Boolean {
        if (nombre.isEmpty()) {
            binding.tilNombre.error = "Ingresa tu nombre"
            return false
        }
        if (email.isEmpty()) {
            binding.tilEmail.error = "Ingresa tu correo"
            return false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Ingresa tu contraseña"
            return false
        }
        if (password.length < Constants.MIN_PASSWORD_LENGTH) {
            binding.tilPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }
        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Las contraseñas no coinciden"
            return false
        }

        binding.tilNombre.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
        return true
    }

    private fun registrarUsuario(nombre: String, email: String, password: String, rol: String) {
        mostrarCargando(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener

                val usuario = Usuario(
                    nombre = nombre,
                    email = email,
                    rol = rol
                ).apply {
                    id_usuario = userId
                }

                lifecycleScope.launch {
                    val result = usuarioRepository.crearUsuario(usuario)
                    mostrarCargando(false)

                    result.onSuccess {
                        Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        finish()
                    }.onFailure { e ->
                        Toast.makeText(this@RegisterActivity, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                mostrarCargando(false)
                Toast.makeText(this, "Error de registro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarCargando(mostrar: Boolean) {
        binding.progressBar.visibility = if (mostrar) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnRegister.isEnabled = !mostrar
    }
}