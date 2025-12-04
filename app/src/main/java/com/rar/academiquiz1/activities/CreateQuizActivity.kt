package com.rar.academiquiz1.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rar.academiquiz1.databinding.ActivityCreateQuizBinding
import com.rar.academiquiz1.models.Opcion
import com.rar.academiquiz1.models.Pregunta
import com.rar.academiquiz1.models.Quiz
import com.rar.academiquiz1.repositories.PreguntaRepository
import com.rar.academiquiz1.repositories.QuizRepository
import com.rar.academiquiz1.repositories.GeminiRepository
import com.rar.academiquiz1.utils.Constants
import com.rar.academiquiz1.utils.SessionManager
import kotlinx.coroutines.launch

class CreateQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateQuizBinding
    private lateinit var quizRepository: QuizRepository
    private lateinit var preguntaRepository: PreguntaRepository
    private lateinit var geminiRepository: GeminiRepository
    private lateinit var sessionManager: SessionManager

    private var quizEditar: Quiz? = null
    private val preguntas = mutableListOf<Pregunta>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizRepository = QuizRepository()
        preguntaRepository = PreguntaRepository()
        geminiRepository = GeminiRepository("AIzaSyBwMQK0VPmkyT7DeU92Om4Jl4s5qFUJR4w")
        sessionManager = SessionManager(this)

        quizEditar = intent.getParcelableExtra(Constants.INTENT_QUIZ)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        if (quizEditar != null) {
            binding.tvTitle.text = "Editar Cuestionario"
            binding.etTitulo.setText(quizEditar?.titulo)
            binding.etNumPreguntas.setText(quizEditar?.num_preguntas.toString())
            binding.etDuracion.setText(quizEditar?.duracion_min.toString())
            binding.switchActivo.isChecked = quizEditar?.isActivo ?: true
        } else {
            binding.tvTitle.text = "Crear Cuestionario"
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            guardarQuiz()
        }

        binding.btnAddPregunta.setOnClickListener {
            agregarPregunta()
        }

        binding.switchIA.setOnCheckedChangeListener { _, isChecked ->
            binding.tilTemaIA.visibility = if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
            binding.tvDifficultyLabel.visibility = if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
            binding.rgDifficulty.visibility = if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
            binding.btnGenerarIA.visibility = if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
            binding.etNumPreguntas.isEnabled = !isChecked // Si es IA, usamos el número para generar
        }

        binding.btnGenerarIA.setOnClickListener {
            generarQuizIA()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun guardarQuiz() {
        val titulo = binding.etTitulo.text.toString().trim()
        val numPreguntas = binding.etNumPreguntas.text.toString().toIntOrNull() ?: 0
        val duracion = binding.etDuracion.text.toString().toIntOrNull() ?: 0
        val activo = binding.switchActivo.isChecked

        if (titulo.isEmpty() || numPreguntas == 0 || duracion == 0) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        mostrarCargando(true)

        val quiz = Quiz(
            titulo = titulo,
            num_preguntas = numPreguntas,
            duracion_min = duracion,
            id_creador = sessionManager.getUserId() ?: ""
        )
        
        quiz.id_quiz = quizEditar?.id_quiz ?: ""
        quiz.isActivo = activo
        quiz.intentos_totales = quizEditar?.intentos_totales ?: 0
        quiz.promedio_general = quizEditar?.promedio_general ?: 0.0

        lifecycleScope.launch {
            val result = if (quizEditar != null) {
                quizRepository.actualizarQuiz(quiz)
                Result.success(quiz.id_quiz)
            } else {
                quizRepository.crearQuiz(quiz)
            }

            mostrarCargando(false)

            result.onSuccess { quizId ->
                if (preguntas.isNotEmpty()) {
                    guardarPreguntas(quizId!!)
                } else {
                    Toast.makeText(this@CreateQuizActivity, "Quiz guardado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.onFailure { e ->
                Toast.makeText(this@CreateQuizActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun agregarPregunta() {
        val enunciado = binding.etEnunciado.text.toString().trim()
        val opcion1 = binding.etOpcion1.text.toString().trim()
        val opcion2 = binding.etOpcion2.text.toString().trim()
        val opcion3 = binding.etOpcion3.text.toString().trim()
        val opcion4 = binding.etOpcion4.text.toString().trim()
        val correcta = binding.radioGroupOpciones.checkedRadioButtonId

        if (enunciado.isEmpty() || opcion1.isEmpty() || opcion2.isEmpty()) {
            Toast.makeText(this, "Completa al menos 2 opciones", Toast.LENGTH_SHORT).show()
            return
        }

        val opciones = mutableListOf<Opcion>()
        opciones.add(Opcion(id_opcion = java.util.UUID.randomUUID().toString(), texto = opcion1, es_correcta = correcta == binding.rbOpcion1.id))
        opciones.add(Opcion(id_opcion = java.util.UUID.randomUUID().toString(), texto = opcion2, es_correcta = correcta == binding.rbOpcion2.id))
        if (opcion3.isNotEmpty()) {
            opciones.add(Opcion(id_opcion = java.util.UUID.randomUUID().toString(), texto = opcion3, es_correcta = correcta == binding.rbOpcion3.id))
        }
        if (opcion4.isNotEmpty()) {
            opciones.add(Opcion(id_opcion = java.util.UUID.randomUUID().toString(), texto = opcion4, es_correcta = correcta == binding.rbOpcion4.id))
        }

        val pregunta = Pregunta(
            enunciado = enunciado,
            tipo = Pregunta.TIPO_MULTIPLE,
            puntaje = 1.0,
            opciones = opciones
        )

        preguntas.add(pregunta)
        limpiarFormularioPregunta()
        Toast.makeText(this, "Pregunta agregada (${preguntas.size})", Toast.LENGTH_SHORT).show()
    }

    private fun guardarPreguntas(quizId: String) {
        lifecycleScope.launch {
            preguntas.forEach { pregunta ->
                pregunta.id_quiz = quizId
                preguntaRepository.crearPregunta(pregunta)
            }
            Toast.makeText(this@CreateQuizActivity, "Quiz y preguntas guardados", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun limpiarFormularioPregunta() {
        binding.etEnunciado.text?.clear()
        binding.etOpcion1.text?.clear()
        binding.etOpcion2.text?.clear()
        binding.etOpcion3.text?.clear()
        binding.etOpcion4.text?.clear()
        binding.radioGroupOpciones.clearCheck()
    }

    private fun generarQuizIA() {
        val tema = binding.etTemaIA.text.toString().trim()
        val numPreguntas = binding.etNumPreguntas.text.toString().toIntOrNull() ?: 5
        
        // Obtener dificultad seleccionada
        val difficulty = when (binding.rgDifficulty.checkedRadioButtonId) {
            binding.rbFacil.id -> "fácil"
            binding.rbIntermedio.id -> "intermedio"
            binding.rbDificil.id -> "difícil"
            else -> "intermedio"
        }

        if (tema.isEmpty()) {
            Toast.makeText(this, "Ingresa un tema", Toast.LENGTH_SHORT).show()
            return
        }

        mostrarCargando(true)
        lifecycleScope.launch {
            val result = geminiRepository.generateQuiz(tema, numQuestions = numPreguntas, difficulty = difficulty)
            mostrarCargando(false)

            result.onSuccess { preguntasGeneradas ->
                preguntas.clear()
                
                // Asignar UUIDs a las opciones generadas por IA
                preguntasGeneradas.forEach { pregunta ->
                    pregunta.opciones.forEach { opcion ->
                        if (opcion.id_opcion.isNullOrEmpty()) {
                            opcion.id_opcion = java.util.UUID.randomUUID().toString()
                        }
                    }
                }
                
                preguntas.addAll(preguntasGeneradas)
                
                // Limpiar el campo de tema después de generar exitosamente
                binding.etTemaIA.text?.clear()
                
                Toast.makeText(this@CreateQuizActivity, "¡${preguntas.size} preguntas generadas con éxito!", Toast.LENGTH_SHORT).show()
                // Opcional: Mostrar las preguntas en una lista o log
            }.onFailure { e ->
                Toast.makeText(this@CreateQuizActivity, "Error IA: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun mostrarCargando(mostrar: Boolean) {
        binding.progressBar.visibility = if (mostrar) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnSave.isEnabled = !mostrar
        binding.btnGenerarIA.isEnabled = !mostrar
    }
}