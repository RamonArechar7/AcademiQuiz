package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
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
import java.util.concurrent.TimeUnit
import android.os.Vibrator
import android.os.VibrationEffect
import android.content.Context
import android.os.Build

class TakeQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTakeQuizBinding
    private lateinit var quiz: Quiz
    private lateinit var preguntaRepository: PreguntaRepository
    private lateinit var resultadoRepository: ResultadoRepository
    private lateinit var quizRepository: QuizRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var preguntaAdapter: PreguntaAdapter

    private var preguntas = listOf<Pregunta>()
    private val respuestasUsuario = mutableMapOf<String, Opcion>()
    private var timer: CountDownTimer? = null
    private var tiempoTranscurrido = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakeQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar autenticación de Firebase antes de continuar
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        android.util.Log.d("TakeQuizActivity", "Firebase Auth User: ${auth.currentUser?.uid}")
        android.util.Log.d("TakeQuizActivity", "Firebase Auth Email: ${auth.currentUser?.email}")
        android.util.Log.d("TakeQuizActivity", "SessionManager UserID: ${SessionManager(this).getUserId()}")
        
        if (auth.currentUser == null) {
            android.util.Log.e("TakeQuizActivity", "Usuario NO autenticado en Firebase!")
            Toast.makeText(this, "Debes iniciar sesión para tomar el quiz", Toast.LENGTH_LONG).show()
            SessionManager(this).clearSession()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        quiz = intent.getParcelableExtra(Constants.INTENT_QUIZ) ?: run {
            finish()
            return
        }

        preguntaRepository = PreguntaRepository()
        resultadoRepository = ResultadoRepository()
        quizRepository = QuizRepository()
        sessionManager = SessionManager(this)

        setupUI()
        setupRecyclerView()
        cargarPreguntas()
        iniciarTemporizador()
    }

    private fun setupUI() {
        binding.tvQuizTitle.text = quiz.titulo
        binding.tvQuizInfo.text = "${quiz.num_preguntas} preguntas • ${quiz.duracion_min} minutos"

        binding.btnSubmit.setOnClickListener {
            confirmarEnvio()
        }
    }

    private fun setupRecyclerView() {
        preguntaAdapter = PreguntaAdapter { pregunta, opcion ->
            // Use id_pregunta if available, otherwise use enunciado as key
            val key = pregunta.id_pregunta ?: pregunta.enunciado ?: ""
            respuestasUsuario[key] = opcion
            actualizarProgreso()
            vibrate(50) // Vibrate for 50ms on selection
        }

        binding.rvPreguntas.apply {
            layoutManager = LinearLayoutManager(this@TakeQuizActivity)
            adapter = preguntaAdapter
        }
    }

    private fun cargarPreguntas() {
        lifecycleScope.launch {
            val result = preguntaRepository.obtenerPreguntasPorQuiz(quiz.id_quiz!!)

            result.onSuccess { listaPreguntas ->
                if (listaPreguntas.isEmpty()) {
                    Toast.makeText(this@TakeQuizActivity, "Este quiz no tiene preguntas. Por favor contacta al profesor.", Toast.LENGTH_LONG).show()
                    finish()
                    return@onSuccess
                }
                preguntas = listaPreguntas.shuffled().take(quiz.num_preguntas)
                
                // Assign UUIDs to options that don't have IDs (fixes old data and Firestore serialization issues)
                preguntas.forEach { pregunta ->
                    pregunta.opciones.forEach { opcion ->
                        if (opcion.id_opcion.isNullOrEmpty()) {
                            opcion.id_opcion = java.util.UUID.randomUUID().toString()
                        }
                    }
                }
                
                // Debug: Log loaded questions and options
                preguntas.forEachIndexed { qIndex, pregunta ->
                    android.util.Log.d("TakeQuiz", "Pregunta $qIndex: id=${pregunta.id_pregunta}, opciones count=${pregunta.opciones.size}")
                    pregunta.opciones.forEachIndexed { oIndex, opcion ->
                        android.util.Log.d("TakeQuiz", "  Opcion $oIndex: id=${opcion.id_opcion}, texto=${opcion.texto}")
                    }
                }
                
                preguntaAdapter.submitList(preguntas)
                actualizarProgreso()
            }.onFailure { e ->
                Toast.makeText(this@TakeQuizActivity, "Error al cargar preguntas: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun iniciarTemporizador() {
        val tiempoTotal = quiz.duracion_min * 60 * 1000L

        timer = object : CountDownTimer(tiempoTotal, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoTranscurrido = tiempoTotal - millisUntilFinished
                val minutos = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val segundos = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                binding.tvTimer.text = String.format("%02d:%02d", minutos, segundos)
            }

            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                Toast.makeText(this@TakeQuizActivity, "¡Tiempo terminado!", Toast.LENGTH_SHORT).show()
                enviarRespuestas()
            }
        }.start()
    }

    private fun actualizarProgreso() {
        if (preguntas.isEmpty()) {
            binding.progressBar.progress = 0
            binding.tvProgreso.text = "0/0 respondidas"
            return
        }
        val progreso = (respuestasUsuario.size * 100) / preguntas.size
        binding.progressBar.progress = progreso
        binding.tvProgreso.text = "${respuestasUsuario.size}/${preguntas.size} respondidas"
    }

    private fun confirmarEnvio() {
        if (respuestasUsuario.size < preguntas.size) {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirmar envío")
                .setMessage("Aún quedan ${preguntas.size - respuestasUsuario.size} preguntas sin responder. ¿Deseas enviar de todas formas?")
                .setPositiveButton("Enviar") { _, _ -> enviarRespuestas() }
                .setNegativeButton("Cancelar", null)
                .show()
        } else {
            enviarRespuestas()
        }
    }

    private fun enviarRespuestas() {
        timer?.cancel()
        mostrarCargando(true)

        lifecycleScope.launch {
            val respuestas = mutableListOf<Respuesta>()
            var puntajeTotal = 0.0
            var puntajeMaximo = 0.0

            preguntas.forEach { pregunta ->
                puntajeMaximo += pregunta.puntaje
                // Use same key logic as adapter: id_pregunta if available, otherwise enunciado
                val key = pregunta.id_pregunta ?: pregunta.enunciado ?: ""
                val opcionSeleccionada = respuestasUsuario[key]
                val opcionCorrecta = pregunta.getOpcionCorrecta()

                val esCorrecta = if (!opcionSeleccionada?.id_opcion.isNullOrEmpty() && !opcionCorrecta?.id_opcion.isNullOrEmpty()) {
                    opcionSeleccionada?.id_opcion == opcionCorrecta?.id_opcion
                } else {
                    // Fallback: comparar por texto si no hay IDs (para quizzes antiguos o generados sin ID)
                    opcionSeleccionada?.texto == opcionCorrecta?.texto
                }

                if (esCorrecta) {
                    puntajeTotal += pregunta.puntaje
                }

                respuestas.add(
                    Respuesta(
                        id_pregunta = pregunta.id_pregunta ?: "",
                        id_opcion = opcionSeleccionada?.id_opcion ?: "",
                        correcta = esCorrecta
                    )
                )
            }

            val porcentaje = (puntajeTotal / puntajeMaximo) * 100
            val duracionMinutos = TimeUnit.MILLISECONDS.toMinutes(tiempoTranscurrido).toInt()

            val resultado = Resultado(
                id_usuario = sessionManager.getUserId() ?: "",
                id_quiz = quiz.id_quiz ?: "",
                puntaje = porcentaje,
                duracion_usada = duracionMinutos,
                nombre_quiz = quiz.titulo,
                nombre_usuario = sessionManager.getUserName() ?: ""
            )

            val resultGuardar = resultadoRepository.crearResultado(resultado, respuestas)

            resultGuardar.onSuccess {
                quizRepository.incrementarIntentos(quiz.id_quiz!!)

                // Calcular y actualizar promedio
                val promedioResult = resultadoRepository.calcularPromedioQuiz(quiz.id_quiz!!)
                promedioResult.onSuccess { promedio ->
                    quizRepository.actualizarPromedio(quiz.id_quiz!!, promedio)
                }

                mostrarCargando(false)
                // Calcular conteo de correctas para mostrar
                val correctas = respuestas.count { it.isCorrecta }
                mostrarResultado(resultado, correctas, preguntas.size)
            }.onFailure { e ->
                mostrarCargando(false)
                Toast.makeText(this@TakeQuizActivity, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarResultado(resultado: Resultado, correctas: Int, total: Int) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("¡Quiz Completado!")
            .setMessage("""
                Tu calificación: ${resultado.getPuntajeFormatted()}
                Aciertos: $correctas de $total
                Estado: ${resultado.getEstado()}
            """.trimIndent())
            .setPositiveButton("Ver Resultados") { _, _ ->
                val intent = android.content.Intent(this, ResultsActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun mostrarCargando(mostrar: Boolean) {
        binding.progressBarSubmit.visibility = if (mostrar) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnSubmit.isEnabled = !mostrar
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    private fun vibrate(duration: Long) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        }
    }
}
