package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.adapters.QuizAdapter
import com.rar.academiquiz1.databinding.ActivityQuizListBinding
import com.rar.academiquiz1.models.Quiz
import com.rar.academiquiz1.repositories.QuizRepository
import com.rar.academiquiz1.utils.Constants
import com.rar.academiquiz1.utils.SessionManager
import kotlinx.coroutines.launch

class QuizListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizListBinding
    private lateinit var quizRepository: QuizRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var quizAdapter: QuizAdapter
    
    private var allQuizzes = listOf<Quiz>()
    private var filteredQuizzes = listOf<Quiz>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizRepository = QuizRepository()
        sessionManager = SessionManager(this)

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        cargarQuizzes()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        val userRole = sessionManager.getUserRol()
        val isTeacher = userRole != "ESTUDIANTE"

        quizAdapter = QuizAdapter(
            onQuizClick = { quiz ->
                if (userRole == "ESTUDIANTE") {
                    // Estudiante: Tomar el quiz
                    val intent = Intent(this, TakeQuizActivity::class.java)
                    intent.putExtra(Constants.INTENT_QUIZ, quiz)
                    startActivity(intent)
                } else {
                    // Profesor/Admin: Ver detalles o editar
                    val intent = Intent(this, CreateQuizActivity::class.java)
                    intent.putExtra(Constants.INTENT_QUIZ, quiz)
                    startActivity(intent)
                }
            },
            onEditClick = if (isTeacher) {
                { quiz ->
                    val intent = Intent(this, CreateQuizActivity::class.java)
                    intent.putExtra(Constants.INTENT_QUIZ, quiz)
                    startActivity(intent)
                }
            } else null,
            onDeleteClick = if (isTeacher) {
                { quiz ->
                    eliminarQuiz(quiz)
                }
            } else null,
            isTeacher = isTeacher
        )

        binding.rvQuizzes.apply {
            layoutManager = LinearLayoutManager(this@QuizListActivity)
            adapter = quizAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarQuizzes(newText ?: "")
                return true
            }
        })
    }

    private fun cargarQuizzes() {
        mostrarCargando(true)
        
        lifecycleScope.launch {
            val userRole = sessionManager.getUserRol()
            
            val result = when (userRole) {
                "ESTUDIANTE" -> quizRepository.obtenerQuizzesActivos()
                "ADMIN" -> quizRepository.obtenerTodosQuizzes()
                else -> { // MAESTRO
                    val userId = sessionManager.getUserId()
                    if (userId != null) {
                        quizRepository.obtenerQuizzesPorCreador(userId)
                    } else {
                        Result.failure(Exception("Usuario no encontrado"))
                    }
                }
            }

            mostrarCargando(false)

            result.onSuccess { quizzes ->
                allQuizzes = quizzes
                filteredQuizzes = quizzes
                actualizarUI()
            }.onFailure { e ->
                Toast.makeText(
                    this@QuizListActivity,
                    "Error al cargar quizzes: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun filtrarQuizzes(query: String) {
        filteredQuizzes = if (query.isEmpty()) {
            allQuizzes
        } else {
            allQuizzes.filter { quiz ->
                quiz.titulo?.contains(query, ignoreCase = true) == true
            }
        }
        actualizarUI()
    }

    private fun actualizarUI() {
        binding.tvTotalQuizzes.text = "${filteredQuizzes.size} quizzes disponibles"
        
        if (filteredQuizzes.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvQuizzes.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvQuizzes.visibility = View.VISIBLE
            quizAdapter.submitList(filteredQuizzes)
        }
    }

    private fun eliminarQuiz(quiz: Quiz) {
        // Validar que el quiz tenga un ID válido
        if (quiz.id_quiz.isNullOrEmpty()) {
            android.util.Log.e("QuizListActivity", "Intento de eliminar quiz con ID nulo o vacío")
            Toast.makeText(this, "Error: Quiz sin ID válido", Toast.LENGTH_SHORT).show()
            return
        }
        
        android.util.Log.d("QuizListActivity", "Intentando eliminar quiz: ${quiz.titulo} (ID: ${quiz.id_quiz})")
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Eliminar Quiz")
            .setMessage("¿Estás seguro de eliminar '${quiz.titulo}'?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                android.util.Log.d("QuizListActivity", "Usuario confirmó eliminación")
                lifecycleScope.launch {
                    try {
                        android.util.Log.d("QuizListActivity", "Llamando a quizRepository.eliminarQuiz(${quiz.id_quiz})")
                        val result = quizRepository.eliminarQuiz(quiz.id_quiz!!)
                        
                        result.onSuccess {
                            android.util.Log.d("QuizListActivity", "Quiz eliminado exitosamente")
                            Toast.makeText(this@QuizListActivity, "Quiz eliminado", Toast.LENGTH_SHORT).show()
                            cargarQuizzes()
                        }.onFailure { e ->
                            android.util.Log.e("QuizListActivity", "Error al eliminar quiz", e)
                            Toast.makeText(
                                this@QuizListActivity, 
                                "Error al eliminar: ${e.message}", 
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("QuizListActivity", "Excepción inesperada al eliminar", e)
                        Toast.makeText(
                            this@QuizListActivity, 
                            "Error inesperado: ${e.message}", 
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                android.util.Log.d("QuizListActivity", "Usuario canceló eliminación")
                dialog.dismiss()
            }
            .show()
    }

    private fun mostrarCargando(mostrar: Boolean) {
        binding.progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        binding.rvQuizzes.visibility = if (mostrar) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        cargarQuizzes()
    }
}
