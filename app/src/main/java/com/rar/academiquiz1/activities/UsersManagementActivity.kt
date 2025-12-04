package com.rar.academiquiz1.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.rar.academiquiz1.R
import com.rar.academiquiz1.adapters.UsuarioAdapter
import com.rar.academiquiz1.databinding.ActivityUsersManagementBinding
import com.rar.academiquiz1.models.Usuario
import com.rar.academiquiz1.repositories.UsuarioRepository
import kotlinx.coroutines.launch

class UsersManagementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersManagementBinding
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var usuarioAdapter: UsuarioAdapter
    private lateinit var auth: FirebaseAuth

    private var allUsers = listOf<Usuario>()
    private var filteredUsers = listOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioRepository = UsuarioRepository()
        auth = FirebaseAuth.getInstance()

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupListeners()
        cargarUsuarios()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        usuarioAdapter = UsuarioAdapter(
            onEditClick = { usuario ->
                mostrarDialogoEditarUsuario(usuario)
            },
            onDeleteClick = { usuario ->
                eliminarUsuario(usuario)
            }
        )

        binding.rvUsers.apply {
            layoutManager = LinearLayoutManager(this@UsersManagementActivity)
            adapter = usuarioAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarUsuarios(newText ?: "")
                return true
            }
        })
    }

    private fun setupListeners() {
        binding.fabAddUser.setOnClickListener {
            mostrarDialogoCrearUsuario()
        }
    }

    private fun cargarUsuarios() {
        mostrarCargando(true)

        lifecycleScope.launch {
            val result = usuarioRepository.obtenerTodosUsuarios()

            mostrarCargando(false)

            result.onSuccess { usuarios ->
                allUsers = usuarios
                filteredUsers = usuarios
                actualizarUI()
                actualizarEstadisticas()
            }.onFailure { e ->
                Toast.makeText(
                    this@UsersManagementActivity,
                    "Error al cargar usuarios: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun filtrarUsuarios(query: String) {
        filteredUsers = if (query.isEmpty()) {
            allUsers
        } else {
            allUsers.filter { usuario ->
                usuario.nombre?.contains(query, ignoreCase = true) == true ||
                usuario.email?.contains(query, ignoreCase = true) == true
            }
        }
        actualizarUI()
    }

    private fun actualizarUI() {
        binding.tvTotalUsers.text = "${filteredUsers.size} usuarios totales"

        if (filteredUsers.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvUsers.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvUsers.visibility = View.VISIBLE
            usuarioAdapter.submitList(filteredUsers)
        }
    }

    private fun actualizarEstadisticas() {
        val estudiantes = allUsers.count { it.rol == "ESTUDIANTE" }
        val profesores = allUsers.count { it.rol == "MAESTRO" }
        val admins = allUsers.count { it.rol == "ADMIN" }

        binding.tvTotalEstudiantes.text = estudiantes.toString()
        binding.tvTotalProfesores.text = profesores.toString()
        binding.tvTotalAdmins.text = admins.toString()
    }

    private fun mostrarDialogoCrearUsuario() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_user, null)
        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.etNombre)
        val etEmail = dialogView.findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = dialogView.findViewById<TextInputEditText>(R.id.etPassword)
        val spinnerRol = dialogView.findViewById<Spinner>(R.id.spinnerRol)

        val roles = arrayOf("ESTUDIANTE", "MAESTRO", "ADMIN")
        spinnerRol.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

        AlertDialog.Builder(this)
            .setTitle("Crear Usuario")
            .setView(dialogView)
            .setPositiveButton("Crear") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                val rol = spinnerRol.selectedItem.toString()

                if (validarDatosUsuario(nombre, email, password)) {
                    crearUsuario(nombre, email, password, rol)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditarUsuario(usuario: Usuario) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_user, null)
        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.etNombre)
        val spinnerRol = dialogView.findViewById<Spinner>(R.id.spinnerRol)

        etNombre.setText(usuario.nombre)

        val roles = arrayOf("ESTUDIANTE", "MAESTRO", "ADMIN")
        spinnerRol.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        spinnerRol.setSelection(roles.indexOf(usuario.rol))

        AlertDialog.Builder(this)
            .setTitle("Editar Usuario")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = etNombre.text.toString().trim()
                val nuevoRol = spinnerRol.selectedItem.toString()

                if (nuevoNombre.isNotEmpty()) {
                    actualizarUsuario(usuario, nuevoNombre, nuevoRol)
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun validarDatosUsuario(nombre: String, email: String, password: String): Boolean {
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun crearUsuario(nombre: String, email: String, password: String, rol: String) {
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

                    result.onSuccess {
                        Toast.makeText(this@UsersManagementActivity, "Usuario creado", Toast.LENGTH_SHORT).show()
                        cargarUsuarios()
                    }.onFailure { e ->
                        Toast.makeText(this@UsersManagementActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al crear usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarUsuario(usuario: Usuario, nuevoNombre: String, nuevoRol: String) {
        usuario.nombre = nuevoNombre
        usuario.rol = nuevoRol

        lifecycleScope.launch {
            val result = usuarioRepository.actualizarUsuario(usuario)

            result.onSuccess {
                Toast.makeText(this@UsersManagementActivity, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                cargarUsuarios()
            }.onFailure { e ->
                Toast.makeText(this@UsersManagementActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun eliminarUsuario(usuario: Usuario) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de eliminar a '${usuario.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    val result = usuarioRepository.eliminarUsuario(usuario.id_usuario!!)

                    result.onSuccess {
                        Toast.makeText(this@UsersManagementActivity, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                        cargarUsuarios()
                    }.onFailure { e ->
                        Toast.makeText(this@UsersManagementActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarCargando(mostrar: Boolean) {
        binding.progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        binding.rvUsers.visibility = if (mostrar) View.GONE else View.VISIBLE
    }
}
