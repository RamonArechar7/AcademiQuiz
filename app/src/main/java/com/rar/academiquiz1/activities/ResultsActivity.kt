package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.adapters.ResultAdapter
import com.rar.academiquiz1.databinding.ActivityResultsBinding
import com.rar.academiquiz1.repositories.ResultadoRepository
import com.rar.academiquiz1.utils.Constants
import com.rar.academiquiz1.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * Actividad que muestra el historial de resultados del usuario actual.
 * Provee acceso al detalle de cada intento.
 */
class ResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding
    private lateinit var resultadoRepository: ResultadoRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var resultAdapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultadoRepository = ResultadoRepository()
        sessionManager = SessionManager(this)

        setupToolbar()
        setupRecyclerView()
        cargarResultados()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        resultAdapter = ResultAdapter { resultado ->
            val intent = Intent(this, ResultDetailsActivity::class.java)
            intent.putExtra(Constants.INTENT_RESULT_ID, resultado.id_resultado)
            intent.putExtra(Constants.INTENT_QUIZ_ID, resultado.id_quiz)
            startActivity(intent)
        }
        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.rvResults.adapter = resultAdapter
    }

    /**
     * Obtiene los resultados desde Firestore y calcula el promedio general.
     */
    private fun cargarResultados() {
        val userId = sessionManager.getUserId() ?: return
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = resultadoRepository.obtenerResultadosPorUsuario(userId)
            
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess { resultados ->
                if (resultados.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvResults.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvResults.visibility = View.VISIBLE
                    resultAdapter.submitList(resultados)
                    
                    // Calcular promedio
                    val promedio = if (resultados.isNotEmpty()) resultados.map { it.puntaje }.average() else 0.0
                    binding.tvPromedio.text = "Promedio General: ${String.format("%.1f", promedio)}"
                }
            }.onFailure { e ->
                Toast.makeText(this@ResultsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
