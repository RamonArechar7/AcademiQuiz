package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.R
import com.rar.academiquiz1.adapters.QuizAdapter
import com.rar.academiquiz1.databinding.ActivityDashboardBinding
import com.rar.academiquiz1.models.Quiz
import com.rar.academiquiz1.repositories.QuizRepository
import com.rar.academiquiz1.repositories.ResultadoRepository
import com.rar.academiquiz1.utils.Constants
import com.rar.academiquiz1.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var quizRepository: QuizRepository
    private lateinit var resultadoRepository: ResultadoRepository
    private lateinit var quizAdapter: QuizAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        quizRepository = QuizRepository()
        resultadoRepository = ResultadoRepository()

        if (!sessionManager.isLoggedIn()) {
            navegarLogin()
            return
        }

        setupUI()
        setupRecyclerView()
        setupListeners()
        cargarDatos()
    }

    private fun setupUI() {
        val userName = sessionManager.getUserName() ?: "Usuario"
        val userRol = sessionManager.getUserRol() ?: "ESTUDIANTE"

        binding.tvUserName.text = userName
        binding.tvWelcome.text = "Bienvenido, $userName"

        // Ajustar labels según rol
        if (userRol == "MAESTRO" || userRol == "ADMIN") {
            binding.tvLabelDisponibles.text = "Creados"
        } else {
            binding.tvLabelDisponibles.text = "Disponibles"
        }

        // FAB para crear quiz (solo profesores y admin)
        if (userRol == "MAESTRO" || userRol == "ADMIN") {
            binding.fabCreateQuiz.visibility = View.VISIBLE
        } else {
            binding.fabCreateQuiz.visibility = View.GONE
        }

        // Icono de gestión de usuarios (solo admin)
        if (userRol == "ADMIN") {
            binding.ivManageUsers.visibility = View.VISIBLE
        } else {
            binding.ivManageUsers.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        quizAdapter = QuizAdapter(
            onQuizClick = { quiz ->
                val intent = Intent(this, TakeQuizActivity::class.java)
                intent.putExtra(Constants.INTENT_QUIZ, quiz)
                startActivity(intent)
            },
            onEditClick = { quiz ->
                val intent = Intent(this, CreateQuizActivity::class.java)
                intent.putExtra(Constants.INTENT_QUIZ, quiz)
                startActivity(intent)
            },
            onDeleteClick = { quiz ->
                eliminarQuiz(quiz)
            },
            isTeacher = sessionManager.getUserRol() != "ESTUDIANTE"
        )

        binding.rvQuizzes.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = quizAdapter
            layoutAnimation = android.view.animation.AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        }
    }

    private fun setupListeners() {
        binding.ivLogout.setOnClickListener {
            cerrarSesion()
        }

        binding.ivManageUsers.setOnClickListener {
            startActivity(Intent(this, UsersManagementActivity::class.java))
        }

        binding.fabCreateQuiz.setOnClickListener {
            startActivity(Intent(this, CreateQuizActivity::class.java))
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    cargarDatos()
                    true
                }
                R.id.nav_quizzes -> {
                    startActivity(Intent(this, QuizListActivity::class.java))
                    true
                }
                R.id.nav_results -> {
                    startActivity(Intent(this, ResultsActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun cargarDatos() {
        mostrarCargando(true)

        lifecycleScope.launch {
            val userRol = sessionManager.getUserRol()
            val result = if (userRol == "ESTUDIANTE") {
                quizRepository.obtenerQuizzesActivos()
            } else {
                quizRepository.obtenerTodosQuizzes()
            }

            mostrarCargando(false)

            result.onSuccess { quizzes ->
                quizAdapter.submitList(quizzes)
                cargarEstadisticas()
            }.onFailure { e ->
                Toast.makeText(this@DashboardActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarEstadisticas() {
        lifecycleScope.launch {
            val userId = sessionManager.getUserId() ?: return@launch
            val userRol = sessionManager.getUserRol() ?: "ESTUDIANTE"
            val result = resultadoRepository.obtenerResultadosPorUsuario(userId)

            result.onSuccess { resultados ->
                // Calcular estadísticas
                val totalCompletados = resultados.size
                val promedio = if (resultados.isNotEmpty()) {
                    resultados.map { it.puntaje }.average()
                } else 0.0
                val mejorPuntaje = if (resultados.isNotEmpty()) {
                    resultados.maxOf { it.puntaje }
                } else 0

                // Actualizar UI
                binding.tvValueCompletados.text = totalCompletados.toString()
                binding.tvValuePromedio.text = String.format("%.1f", promedio)
                binding.tvValueMejor.text = mejorPuntaje.toString()

                // Cargar total de quizzes disponibles/creados
                cargarTotalQuizzes(userRol)
            }
        }
    }

    private fun cargarTotalQuizzes(userRol: String) {
        lifecycleScope.launch {
            val result = if (userRol == "ESTUDIANTE") {
                quizRepository.obtenerQuizzesActivos()
            } else {
                quizRepository.obtenerTodosQuizzes()
            }

            result.onSuccess { quizzes ->
                binding.tvValueDisponibles.text = quizzes.size.toString()
            }
        }
    }

    private fun eliminarQuiz(quiz: Quiz) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Eliminar Quiz")
            .setMessage("¿Estás seguro de eliminar '${quiz.titulo}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    val result = quizRepository.eliminarQuiz(quiz.id_quiz!!)
                    result.onSuccess {
                        Toast.makeText(this@DashboardActivity, "Quiz eliminado", Toast.LENGTH_SHORT).show()
                        cargarDatos()
                    }.onFailure { e ->
                        Toast.makeText(this@DashboardActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarCargando(mostrar: Boolean) {
        // Implementar loading
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        sessionManager.clearSession()
        navegarLogin()
    }

    private fun navegarLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}