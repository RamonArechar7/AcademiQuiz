package com.rar.academiquiz1.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.adapters.ResultDetailAdapter
import com.rar.academiquiz1.databinding.ActivityResultDetailsBinding
import com.rar.academiquiz1.models.Resultado
import com.rar.academiquiz1.repositories.PreguntaRepository
import com.rar.academiquiz1.repositories.ResultadoRepository
import com.rar.academiquiz1.utils.Constants
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ResultDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultDetailsBinding
    private lateinit var resultado: Resultado
    private lateinit var preguntaRepository: PreguntaRepository
    private lateinit var resultadoRepository: ResultadoRepository
    private lateinit var adapter: ResultDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultado = intent.getParcelableExtra("RESULTADO") ?: run {
            finish()
            return
        }

        if (resultado.id_resultado == null) {
            Toast.makeText(this, "Error: Identificador de resultado no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        preguntaRepository = PreguntaRepository()
        resultadoRepository = ResultadoRepository()

        setupToolbar()
        setupUI()
        setupRecyclerView()
        cargarDetalles()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalles del Quiz"
    }

    private fun setupUI() {
        binding.tvQuizTitle.text = resultado.nombre_quiz
        binding.tvScore.text = "Puntaje: ${resultado.getPuntajeFormatted()}"
        binding.tvStatus.text = resultado.getEstado()
        
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        binding.tvDate.text = "Fecha: ${resultado.fecha_intento?.let { dateFormat.format(it) } ?: "N/A"}"

        if (resultado.puntaje >= 70) {
            binding.tvStatus.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            binding.tvStatus.setTextColor(getColor(android.R.color.holo_red_dark))
        }
    }

    private fun setupRecyclerView() {
        adapter = ResultDetailAdapter()
        binding.rvDetails.layoutManager = LinearLayoutManager(this)
        binding.rvDetails.adapter = adapter
    }

    private fun cargarDetalles() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            // 1. Obtener preguntas del quiz
            val preguntasResult = preguntaRepository.obtenerPreguntasPorQuiz(resultado.id_quiz!!)
            
            // 2. Obtener respuestas del usuario para este resultado
            val respuestasResult = resultadoRepository.obtenerRespuestasPorResultado(resultado.id_resultado!!)

            if (preguntasResult.isSuccess && respuestasResult.isSuccess) {
                val preguntas = preguntasResult.getOrDefault(emptyList())
                val respuestas = respuestasResult.getOrDefault(emptyList())

                // Debug logging
                android.util.Log.d("ResultDetails", "Preguntas count: ${preguntas.size}")
                android.util.Log.d("ResultDetails", "Respuestas count: ${respuestas.size}")
                preguntas.forEachIndexed { index, pregunta ->
                    android.util.Log.d("ResultDetails", "Pregunta $index ID: ${pregunta.id_pregunta}")
                }
                respuestas.forEachIndexed { index, respuesta ->
                    android.util.Log.d("ResultDetails", "Respuesta $index - id_pregunta: ${respuesta.id_pregunta}, id_opcion: ${respuesta.id_opcion}")
                }

                val detailItems = preguntas.map { pregunta ->
                    val respuestaUsuario = respuestas.find { it.id_pregunta == pregunta.id_pregunta }
                    
                    android.util.Log.d("ResultDetails", "Pregunta ${pregunta.id_pregunta}: respuestaUsuario = $respuestaUsuario")
                    
                    // Encontrar texto de opción seleccionada
                    val opcionSeleccionada = if (respuestaUsuario != null) {
                        pregunta.opciones.find { it.id_opcion == respuestaUsuario.id_opcion }?.texto 
                            ?: "Respuesta no encontrada"
                    } else {
                        null
                    }

                    // Encontrar texto de opción correcta
                    val opcionCorrecta = pregunta.getOpcionCorrecta()?.texto

                    ResultDetailAdapter.DetailItem(
                        pregunta = pregunta,
                        respuesta = respuestaUsuario,
                        opcionSeleccionada = opcionSeleccionada,
                        opcionCorrecta = opcionCorrecta
                    )
                }

                adapter.submitList(detailItems)
            } else {
                Toast.makeText(this@ResultDetailsActivity, "Error al cargar detalles", Toast.LENGTH_SHORT).show()
            }
            
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
