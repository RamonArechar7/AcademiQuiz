package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

/**
 * Actividad que muestra la lista de quizzes disponibles.
 * Permite buscar, filtrar y seleccionar quizzes para tomarlos o editarlos (si es maestro).
 */
class QuizListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizListBinding
    private lateinit var quizRepository: QuizRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var quizAdapter: QuizAdapter

    private var allQuizzes = listOf<Quiz>()
    private var isTeacher = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizRepository = QuizRepository()
        sessionManager = SessionManager(this)

        // Verificar rol
        val rol = sessionManager.getUserRol()
        isTeacher = rol == "MAESTRO" || rol == "ADMIN"

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        
        cargarQuizzes()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
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
                confirmarEliminacion(quiz)
            },
            isTeacher = isTeacher
        )
        binding.rvQuizzes.layoutManager = LinearLayoutManager(this)
        binding.rvQuizzes.adapter = quizAdapter
    }
    
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarQuizzes(newText ?: "")
                return true
            }
        })
    }

    /**
     * Carga todos los quizzes activos desde Firestore.
     * Si el usuario es ADMIN, carga todos (activos e inactivos).
     */
    private fun cargarQuizzes() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            val result = if (sessionManager.getUserRol() == "ADMIN") {
                quizRepository.obtenerTodosQuizzes()
            } else {
                quizRepository.obtenerQuizzesActivos()
            }
            
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess { quizzes ->
                allQuizzes = quizzes
                actualizarLista(quizzes)
            }.onFailure { e ->
                Toast.makeText(this@QuizListActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filtrarQuizzes(query: String) {
        val filtrados = if (query.isEmpty()) {
            allQuizzes
        } else {
            allQuizzes.filter { it.titulo.contains(query, ignoreCase = true) }
        }
        actualizarLista(filtrados)
    }

    private fun actualizarLista(quizzes: List<Quiz>) {
        quizAdapter.submitList(quizzes)
        if (quizzes.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvQuizzes.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvQuizzes.visibility = View.VISIBLE
        }
    }

    private fun confirmarEliminacion(quiz: Quiz) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Quiz")
            .setMessage("¿Estás seguro de eliminar '${quiz.titulo}'? Se borrarán todos los resultados asociados.")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarQuiz(quiz.id_quiz!!)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarQuiz(quizId: String) {
        lifecycleScope.launch {
            val result = quizRepository.eliminarQuiz(quizId)
            result.onSuccess {
                Toast.makeText(this@QuizListActivity, "Quiz eliminado", Toast.LENGTH_SHORT).show()
                cargarQuizzes()
            }.onFailure {
                Toast.makeText(this@QuizListActivity, "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        cargarQuizzes()
    }
}
