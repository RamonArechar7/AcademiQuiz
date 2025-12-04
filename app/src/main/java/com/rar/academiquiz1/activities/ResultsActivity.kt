package com.rar.academiquiz1.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.adapters.ResultAdapter
import com.rar.academiquiz1.databinding.ActivityResultsBinding
import com.rar.academiquiz1.repositories.ResultadoRepository
import com.rar.academiquiz1.utils.SessionManager
import kotlinx.coroutines.launch

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
        supportActionBar?.title = "Mis Resultados"
    }

    private fun setupRecyclerView() {
        resultAdapter = ResultAdapter { resultado ->
            val intent = android.content.Intent(this, ResultDetailsActivity::class.java)
            intent.putExtra("RESULTADO", resultado)
            startActivity(intent)
        }

        binding.rvResults.apply {
            layoutManager = LinearLayoutManager(this@ResultsActivity)
            adapter = resultAdapter
        }
    }

    private fun cargarResultados() {
        mostrarCargando(true)

        lifecycleScope.launch {
            val userId = sessionManager.getUserId()
            if (userId == null) {
                mostrarCargando(false)
                Toast.makeText(this@ResultsActivity, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            
            val result = resultadoRepository.obtenerResultadosPorUsuario(userId)

            mostrarCargando(false)

            result.onSuccess { resultados ->
                if (resultados.isEmpty()) {
                    binding.tvEmpty.visibility = android.view.View.VISIBLE
                    binding.rvResults.visibility = android.view.View.GONE
                    binding.tvPromedio.text = "Promedio General: --"
                } else {
                    binding.tvEmpty.visibility = android.view.View.GONE
                    binding.rvResults.visibility = android.view.View.VISIBLE
                    resultAdapter.submitList(resultados)

                    // Calcular estadísticas
                    val promedio = resultados.map { it.puntaje }.average()
                    binding.tvPromedio.text = "Promedio General: ${String.format("%.1f%%", promedio)}"
                }
            }.onFailure { e ->
                Toast.makeText(
                    this@ResultsActivity,
                    "Error al cargar resultados: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                binding.tvEmpty.visibility = android.view.View.VISIBLE
                binding.tvEmpty.text = "Error al cargar resultados. Intenta de nuevo."
                binding.rvResults.visibility = android.view.View.GONE
            }
        }
    }

    private fun mostrarDetalles(resultado: com.rar.academiquiz1.models.Resultado) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Detalles del Resultado")
            .setMessage("""
                Quiz: ${resultado.nombre_quiz}
                Calificación: ${resultado.getPuntajeFormatted()}
                Tiempo usado: ${resultado.duracion_usada} minutos
                Estado: ${resultado.getEstado()}
            """.trimIndent())
            .setPositiveButton("OK", null)
            .show()
    }

    private fun mostrarCargando(mostrar: Boolean) {
        binding.progressBar.visibility = if (mostrar) android.view.View.VISIBLE else android.view.View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
