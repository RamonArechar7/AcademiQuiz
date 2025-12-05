package com.rar.academiquiz1.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rar.academiquiz1.R
import com.rar.academiquiz1.databinding.ActivityRegisterBinding
import com.rar.academiquiz1.models.Usuario
import com.rar.academiquiz1.repositories.UsuarioRepository
import com.rar.academiquiz1.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * Actividad para el registro de nuevos usuarios.
 * Permite crear cuentas de Estudiante o Maestro y guarda la información en Firebase.
 */
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
        val roles = arrayOf("ESTUDIANTE", "MAESTRO")
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

    /**
     * Valida que los campos requeridos estén completos y las contraseñas coincidan.
     */
    private fun validarCampos(nombre: String, email: String, pass: String, confirm: String): Boolean {
        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass.length < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass != confirm) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Crea el usuario en Firebase Authentication y guarda los datos adicionales en Firestore.
     */
    private fun registrarUsuario(nombre: String, email: String, pass: String, rol: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val nuevoUsuario = Usuario(nombre, email, rol).apply {
                        id_usuario = userId
                    }
                    
                    guardarUsuarioEnFirestore(nuevoUsuario)
                } else {
                    Toast.makeText(this, "Error en registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun guardarUsuarioEnFirestore(usuario: Usuario) {
        lifecycleScope.launch {
            val result = usuarioRepository.crearUsuario(usuario)
            
            result.onSuccess {
                Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure { e ->
                Toast.makeText(this@RegisterActivity, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}