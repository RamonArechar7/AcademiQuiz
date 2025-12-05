package com.rar.academiquiz1.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.adapters.ResultDetailAdapter
import com.rar.academiquiz1.databinding.ActivityResultDetailsBinding
import com.rar.academiquiz1.models.Pregunta
import com.rar.academiquiz1.models.Respuesta
import com.rar.academiquiz1.repositories.PreguntaRepository
import com.rar.academiquiz1.repositories.ResultadoRepository
import com.rar.academiquiz1.utils.Constants
import kotlinx.coroutines.launch

/**
 * Actividad que muestra el detalle paso a paso de un intento de quiz.
 * Permite al usuario revisar en qué preguntas acertó y en cuáles falló.
 */
class ResultDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultDetailsBinding
    private lateinit var preguntaRepository: PreguntaRepository
    private lateinit var resultadoRepository: ResultadoRepository
    private lateinit var adapter: ResultDetailAdapter

    private var resultId: String? = null
    private var quizId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultId = intent.getStringExtra(Constants.INTENT_RESULT_ID)
        quizId = intent.getStringExtra(Constants.INTENT_QUIZ_ID)

        if (resultId == null || quizId == null) {
            finish()
            return
        }

        preguntaRepository = PreguntaRepository()
        resultadoRepository = ResultadoRepository()

        setupToolbar()
        setupRecyclerView()
        cargarDetalles()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = ResultDetailAdapter()
        binding.rvDetails.layoutManager = LinearLayoutManager(this)
        binding.rvDetails.adapter = adapter
    }

    /**
     * Carga las preguntas originales y las respuestas enviadas por el usuario para compararlas.
     */
    private fun cargarDetalles() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            // Cargar preguntas y respuestas en paralelo
            val preguntasResult = preguntaRepository.obtenerPreguntasPorQuiz(quizId!!)
            val respuestasResult = resultadoRepository.obtenerRespuestasPorResultado(resultId!!)

            binding.progressBar.visibility = View.GONE

            if (preguntasResult.isSuccess && respuestasResult.isSuccess) {
                val preguntas = preguntasResult.getOrDefault(emptyList())
                val respuestas = respuestasResult.getOrDefault(emptyList())
                
                combinarDatos(preguntas, respuestas)
            } else {
                Toast.makeText(this@ResultDetailsActivity, "Error al cargar detalles", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Combina la lista de preguntas y respuestas para generar los items de detalle visuales.
     */
    private fun combinarDatos(preguntas: List<Pregunta>, respuestas: List<Respuesta>) {
        val detailItems = preguntas.map { pregunta ->
            val respuesta = respuestas.find { it.id_pregunta == pregunta.id_pregunta }
            
            // Encontrar texto de opción seleccionada
            val opcionSeleccionada = pregunta.opciones.find { it.id_opcion == respuesta?.id_opcion }?.texto
            
            // Encontrar texto de opción correcta
            val opcionCorrecta = pregunta.getOpcionCorrecta()?.texto
            
            ResultDetailAdapter.DetailItem(
                pregunta = pregunta,
                respuesta = respuesta,
                opcionSeleccionada = opcionSeleccionada,
                opcionCorrecta = opcionCorrecta
            )
        }
        
        adapter.submitList(detailItems)
    }
}
