package com.rar.academiquiz1.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.adapters.PreguntaAdapter
import com.rar.academiquiz1.databinding.ActivityTakeQuizBinding
import com.rar.academiquiz1.models.Opcion
import com.rar.academiquiz1.models.Pregunta
import com.rar.academiquiz1.models.Quiz
import com.rar.academiquiz1.models.Respuesta
import com.rar.academiquiz1.models.Resultado
import com.rar.academiquiz1.repositories.PreguntaRepository
import com.rar.academiquiz1.repositories.QuizRepository
import com.rar.academiquiz1.repositories.ResultadoRepository
import com.rar.academiquiz1.utils.Constants
import com.rar.academiquiz1.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * Actividad para la realización de un quiz.
 * Gestiona la carga de preguntas, el temporizador, el guardado de respuestas y el cálculo del resultado final.
 */
class TakeQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTakeQuizBinding
    private lateinit var quiz: Quiz
    private lateinit var preguntaRepository: PreguntaRepository
    private lateinit var resultadoRepository: ResultadoRepository
    private lateinit var quizRepository: QuizRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var preguntaAdapter: PreguntaAdapter
    
    private var preguntas = listOf<Pregunta>()
    private val respuestasUsuario = mutableMapOf<String, Opcion>() // Map<PreguntaID, Opcion>
    private var countDownTimer: CountDownTimer? = null
    private var tiempoRestanteMs: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakeQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quiz = intent.getParcelableExtra(Constants.INTENT_QUIZ) ?: run {
            finish()
            return
        }

        preguntaRepository = PreguntaRepository()
        resultadoRepository = ResultadoRepository()
        quizRepository = QuizRepository()
        sessionManager = SessionManager(this)

        setupToolbar()
        setupRecyclerView()
        cargarPreguntas()
        
        binding.btnSubmit.setOnClickListener {
            confirmarEnvio()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = quiz.titulo
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        preguntaAdapter = PreguntaAdapter { pregunta, opcion ->
            // Guardar respuesta seleccionada
            val key = pregunta.id_pregunta ?: pregunta.enunciado ?: ""
            respuestasUsuario[key] = opcion
            actualizarProgreso()
        }
        binding.rvPreguntas.layoutManager = LinearLayoutManager(this)
        binding.rvPreguntas.adapter = preguntaAdapter
    }

    private fun cargarPreguntas() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            val result = preguntaRepository.obtenerPreguntasPorQuiz(quiz.id_quiz!!)
            
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess { listaPreguntas ->
                preguntas = listaPreguntas
                preguntaAdapter.submitList(preguntas)
                iniciarTemporizador()
                actualizarProgreso()
            }.onFailure {
                Toast.makeText(this@TakeQuizActivity, "Error al cargar preguntas", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Inicia un temporizador basado en la duración del quiz.
     * Termina automáticamente el intento si el tiempo expira.
     */
    private fun iniciarTemporizador() {
        val duracionMs = quiz.duracion_min * 60 * 1000L
        
        countDownTimer = object : CountDownTimer(duracionMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestanteMs = millisUntilFinished
                val minutos = millisUntilFinished / 1000 / 60
                val segundos = (millisUntilFinished / 1000) % 60
                binding.tvTimer.text = String.format("%02d:%02d", minutos, segundos)
                
                if (millisUntilFinished < 60000) { // Menos de 1 min
                    binding.tvTimer.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            }

            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                Toast.makeText(this@TakeQuizActivity, "¡Tiempo terminado!", Toast.LENGTH_LONG).show()
                enviarRespuestas()
            }
        }.start()
    }

    private fun actualizarProgreso() {
        val respondidas = respuestasUsuario.size
        val total = preguntas.size
        binding.tvProgreso.text = "Respondidas: $respondidas / $total"
        binding.progressBar.max = total
        binding.progressBar.progress = respondidas
    }

    private fun confirmarEnvio() {
        val noRespondidas = preguntas.size - respuestasUsuario.size
        val mensaje = if (noRespondidas > 0) {
            "Te faltan $noRespondidas preguntas por responder. ¿Estás seguro de enviar?"
        } else {
            "¿Estás seguro de enviar tus respuestas?"
        }

        AlertDialog.Builder(this)
            .setTitle("Enviar Quiz")
            .setMessage(mensaje)
            .setPositiveButton("Enviar") { _, _ ->
                enviarRespuestas()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Califica el quiz, calcula el puntaje y guarda el resultado y las respuestas en Firestore.
     */
    private fun enviarRespuestas() {
        countDownTimer?.cancel()
        binding.btnSubmit.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        // Calcular puntaje
        var puntajeTotal = 0.0
        val respuestasParaGuardar = mutableListOf<Respuesta>()

        preguntas.forEach { pregunta ->
            val key = pregunta.id_pregunta ?: pregunta.enunciado ?: ""
            val opcionSeleccionada = respuestasUsuario[key]
            
            val esCorrecta = opcionSeleccionada?.es_correcta == true
            if (esCorrecta) {
                puntajeTotal += pregunta.puntaje
            }

            // Crear objeto Respuesta
            if (opcionSeleccionada != null) {
                respuestasParaGuardar.add(Respuesta(
                    id_pregunta = pregunta.id_pregunta,
                    id_opcion = opcionSeleccionada.id_opcion,
                    correcta = esCorrecta
                ))
            }
        }

        // Normalizar puntaje a 0-100
        val puntajeMaximo = preguntas.sumOf { it.puntaje }
        val puntajeFinal = if (puntajeMaximo > 0) (puntajeTotal / puntajeMaximo) * 100 else 0.0

        val resultado = Resultado(
            id_usuario = sessionManager.getUserId(),
            id_quiz = quiz.id_quiz,
            puntaje = puntajeFinal,
            duracion_usada = quiz.duracion_min - (tiempoRestanteMs / 1000 / 60).toInt(),
            nombre_quiz = quiz.titulo,
            nombre_usuario = sessionManager.getUserName()
        )

        lifecycleScope.launch {
            val result = resultadoRepository.crearResultado(resultado, respuestasParaGuardar)
            
            // Actualizar estadísticas del quiz
            quizRepository.incrementarIntentos(quiz.id_quiz!!)
            
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess {
                mostrarDialogoResultado(puntajeFinal)
            }.onFailure { e ->
                Toast.makeText(this@TakeQuizActivity, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun mostrarDialogoResultado(puntaje: Double) {
        val mensaje = if (puntaje >= 70) "¡Felicidades! Aprobaste." else "Sigue intentando."
        
        AlertDialog.Builder(this)
            .setTitle("Quiz Completado")
            .setMessage("Tu calificación: ${String.format("%.1f", puntaje)}%\n$mensaje")
            .setPositiveButton("Ver Resultados") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Salir del Quiz")
            .setMessage("Si sales ahora, perderás tu progreso. ¿Estás seguro?")
            .setPositiveButton("Salir") { _, _ ->
                countDownTimer?.cancel()
                super.onBackPressed()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
