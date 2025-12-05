package com.rar.academiquiz1.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.R
import com.rar.academiquiz1.adapters.UsuarioAdapter
import com.rar.academiquiz1.databinding.ActivityUsersManagementBinding
import com.rar.academiquiz1.databinding.DialogCreateUserBinding
import com.rar.academiquiz1.models.Usuario
import com.rar.academiquiz1.repositories.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * Actividad para la gestión de usuarios (CRUD) reservada para administradores.
 * Permite listar, filtrar, agregar, editar y eliminar usuarios.
 */
class UsersManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersManagementBinding
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var usuarioAdapter: UsuarioAdapter
    
    private var allUsuarios = listOf<Usuario>()
    private var filteredUsuarios = listOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioRepository = UsuarioRepository()

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupListeners()
        cargarUsuarios()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        usuarioAdapter = UsuarioAdapter(
            onEditClick = { usuario -> mostrarDialogoEditarUsuario(usuario) },
            onDeleteClick = { usuario -> confirmarEliminacion(usuario) }
        )
        binding.rvUsuarios.layoutManager = LinearLayoutManager(this)
        binding.rvUsuarios.adapter = usuarioAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarUsuarios(newText ?: "")
                return true
            }
        })
    }

    private fun setupListeners() {
        binding.fabAddUser.setOnClickListener {
            mostrarDialogoAgregarUsuario()
        }
    }

    private fun cargarUsuarios() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = usuarioRepository.obtenerTodosUsuarios()
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess { usuarios ->
                allUsuarios = usuarios
                filteredUsuarios = usuarios
                actualizarUI()
            }.onFailure { e ->
                Toast.makeText(this@UsersManagementActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filtrarUsuarios(query: String) {
        filteredUsuarios = if (query.isEmpty()) {
            allUsuarios
        } else {
            allUsuarios.filter { 
                it.nombre?.contains(query, ignoreCase = true) == true || 
                it.email?.contains(query, ignoreCase = true) == true 
            }
        }
        actualizarUI()
    }

    private fun actualizarUI() {
        usuarioAdapter.submitList(filteredUsuarios)
        binding.tvTotalUsers.text = "Total: ${filteredUsuarios.size} usuarios"
        
        if (filteredUsuarios.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvUsuarios.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvUsuarios.visibility = View.VISIBLE
        }
    }

    private fun mostrarDialogoAgregarUsuario() {
        val dialogBinding = DialogCreateUserBinding.inflate(layoutInflater)
        val roles = arrayOf("ESTUDIANTE", "MAESTRO", "ADMIN")
        dialogBinding.spinnerRol.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

        AlertDialog.Builder(this)
            .setTitle("Agregar Usuario")
            .setView(dialogBinding.root)
            .setPositiveButton("Crear") { _, _ ->
                val nombre = dialogBinding.etNombre.text.toString()
                val email = dialogBinding.etEmail.text.toString()
                val password = dialogBinding.etPassword.text.toString()
                val rol = dialogBinding.spinnerRol.selectedItem.toString()

                if (nombre.isNotEmpty() && email.isNotEmpty() && password.length >= 6) {
                    crearUsuarioEnFirebase(nombre, email, password, rol)
                } else {
                    Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Simula la creación del usuario. En un entorno real, esto requeriría un Backend o Cloud Functions
     * para no cerrar la sesión del administrador actual al crear otro usuario.
     */
    private fun crearUsuarioEnFirebase(nombre: String, email: String, pass: String, rol: String) {
        Toast.makeText(this, "Funcionalidad limitada en cliente: Se requiere Backend para crear usuarios sin cerrar sesión", Toast.LENGTH_LONG).show()
        
        val nuevoUsuario = Usuario(nombre, email, rol).apply {
            id_usuario = java.util.UUID.randomUUID().toString() // ID temporal simulado
        }
        
        lifecycleScope.launch {
            usuarioRepository.crearUsuario(nuevoUsuario)
            cargarUsuarios()
        }
    }

    private fun mostrarDialogoEditarUsuario(usuario: Usuario) {
        val dialogBinding = DialogCreateUserBinding.inflate(layoutInflater)
        dialogBinding.etNombre.setText(usuario.nombre)
        dialogBinding.etEmail.setText(usuario.email)
        dialogBinding.etEmail.isEnabled = false // No editar email
        dialogBinding.etPassword.visibility = View.GONE 
        dialogBinding.tilPassword.visibility = View.GONE
        
        val roles = arrayOf("ESTUDIANTE", "MAESTRO", "ADMIN")
        dialogBinding.spinnerRol.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        val roleIndex = roles.indexOf(usuario.rol)
        if (roleIndex >= 0) dialogBinding.spinnerRol.setSelection(roleIndex)

        AlertDialog.Builder(this)
            .setTitle("Editar Usuario")
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = dialogBinding.etNombre.text.toString()
                val nuevoRol = dialogBinding.spinnerRol.selectedItem.toString()
                
                if (nuevoNombre.isNotEmpty()) {
                    usuario.nombre = nuevoNombre
                    usuario.rol = nuevoRol
                    actualizarUsuario(usuario)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarUsuario(usuario: Usuario) {
        lifecycleScope.launch {
            usuarioRepository.actualizarUsuario(usuario)
            cargarUsuarios()
            Toast.makeText(this@UsersManagementActivity, "Usuario actualizado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmarEliminacion(usuario: Usuario) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de eliminar a ${usuario.nombre}? Esta acción borrará todos sus resultados.")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    usuarioRepository.eliminarUsuario(usuario.id_usuario!!)
                    cargarUsuarios()
                    Toast.makeText(this@UsersManagementActivity, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
