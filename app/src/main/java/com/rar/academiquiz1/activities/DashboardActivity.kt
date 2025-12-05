package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.R
import com.rar.academiquiz1.adapters.QuizAdapter
import com.rar.academiquiz1.databinding.ActivityDashboardBinding
import com.rar.academiquiz1.repositories.QuizRepository
import com.rar.academiquiz1.repositories.ResultadoRepository
import com.rar.academiquiz1.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * Pantalla principal de la aplicación.
 * Muestra opciones y estadísticas personalizadas según el rol del usuario (Estudiante, Maestro, Admin).
 */
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var quizRepository: QuizRepository
    private lateinit var resultadoRepository: ResultadoRepository
    private val quizAdapter = QuizAdapter { quiz ->
        // Al hacer click en un quiz del dashboard (acceso rápido)
        val intent = Intent(this, TakeQuizActivity::class.java)
        intent.putExtra("ID_QUIZ", quiz.id_quiz)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        quizRepository = QuizRepository()
        resultadoRepository = ResultadoRepository()

        setupUI()
        setupListeners()
        setupRecyclerView()
        cargarDatos()
    }

    /**
     * Configura la interfaz de usuario basándose en los datos de la sesión y roles.
     */
    private fun setupUI() {
        val userName = sessionManager.getUserName()
        val userRole = sessionManager.getUserRol()

        binding.tvUserName.text = "Hola, $userName"
        binding.tvWelcome.text = if (userRole == "ESTUDIANTE") "Tus Estadísticas" else "Panel de Control"

        // Configurar visibilidad según rol
        if (userRole == "ESTUDIANTE") {
            binding.fabCreateQuiz.visibility = View.GONE
            binding.ivManageUsers.visibility = View.GONE
        } else if (userRole == "MAESTRO") {
            binding.fabCreateQuiz.visibility = View.VISIBLE
            binding.ivManageUsers.visibility = View.GONE
        } else {
            // ADMIN
            binding.fabCreateQuiz.visibility = View.VISIBLE
            binding.ivManageUsers.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        binding.rvQuizzes.layoutManager = LinearLayoutManager(this)
        binding.rvQuizzes.adapter = quizAdapter
    }

    private fun setupListeners() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true // Ya estamos aquí
                R.id.nav_quizzes -> {
                    startActivity(Intent(this, QuizListActivity::class.java))
                    false
                }
                R.id.nav_results -> {
                    startActivity(Intent(this, ResultsActivity::class.java))
                    false
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    false
                }
                else -> false
            }
        }

        binding.ivManageUsers.setOnClickListener {
            startActivity(Intent(this, UsersManagementActivity::class.java))
        }

        binding.fabCreateQuiz.setOnClickListener {
            startActivity(Intent(this, CreateQuizActivity::class.java))
        }

        binding.ivLogout.setOnClickListener {
            cerrarSesion()
        }
    }

    /**
     * Carga estadísticas y lista de quizzes.
     */
    private fun cargarDatos() {
        val userId = sessionManager.getUserId() ?: return
        val userRole = sessionManager.getUserRol()

        lifecycleScope.launch {
            // Cargar Quizzes Recientes para la lista
            val quizzesResult = quizRepository.obtenerTodosQuizzes() // Simplificado: mostrar todos
            quizzesResult.onSuccess { quizzes ->
                quizAdapter.submitList(quizzes.take(5)) // Mostrar solo 5 recientes
                binding.tvValueDisponibles.text = quizzes.size.toString()
            }

            // Estadísticas
            if (userRole == "ESTUDIANTE") {
                val resultados = resultadoRepository.obtenerResultadosPorUsuario(userId).getOrDefault(emptyList())
                binding.tvValueCompletados.text = resultados.size.toString()
                
                val promedio = if (resultados.isNotEmpty()) resultados.map { it.puntaje }.average() else 0.0
                binding.tvValuePromedio.text = String.format("%.1f", promedio)

                val mejor = if (resultados.isNotEmpty()) resultados.maxOf { it.puntaje } else 0.0
                binding.tvValueMejor.text = String.format("%.1f", mejor)
                
            } else {
                // Maestro/Admin
                binding.tvLabelCompletados.text = "Quizzes Creados"
                binding.tvLabelPromedio.text = "Intentos Totales"
                binding.tvLabelMejor.text = "Alumnos Activos" // Placeholder

                val quizzes = if (userRole == "ADMIN") {
                    quizRepository.obtenerTodosQuizzes().getOrDefault(emptyList())
                } else {
                    quizRepository.obtenerQuizzesPorCreador(userId).getOrDefault(emptyList())
                }
                
                binding.tvValueCompletados.text = quizzes.size.toString()
                
                val intentos = quizzes.sumOf { it.intentos_totales }
                binding.tvValuePromedio.text = intentos.toString()
                
                binding.tvValueMejor.text = "-" // Implementar lógica real si existe
            }
        }
    }

    private fun cerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                sessionManager.clearSession()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        setupUI() // Refresh UI on return (e.g. name change)
        cargarDatos()
        binding.bottomNavigation.selectedItemId = R.id.nav_dashboard
    }
}