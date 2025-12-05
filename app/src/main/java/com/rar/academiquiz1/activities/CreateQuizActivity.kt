package com.rar.academiquiz1.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.R
import com.rar.academiquiz1.adapters.PreguntaAdapter
import com.rar.academiquiz1.databinding.ActivityCreateQuizBinding
import com.rar.academiquiz1.databinding.DialogAddQuestionBinding
import com.rar.academiquiz1.models.Opcion
import com.rar.academiquiz1.models.Pregunta
import com.rar.academiquiz1.models.Quiz
import com.rar.academiquiz1.repositories.GeminiRepository
import com.rar.academiquiz1.repositories.PreguntaRepository
import com.rar.academiquiz1.repositories.QuizRepository
import com.rar.academiquiz1.utils.Constants
import com.rar.academiquiz1.utils.SessionManager
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Actividad para crear y editar quizzes.
 * Permite agregar preguntas manualmente o generarlas automáticamente usando IA (Gemini).
 */
class CreateQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateQuizBinding
    private lateinit var quizRepository: QuizRepository
    private lateinit var preguntaRepository: PreguntaRepository
    private lateinit var geminiRepository: GeminiRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var preguntaAdapter: PreguntaAdapter

    private var quizActual: Quiz = Quiz()
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizRepository = QuizRepository()
        preguntaRepository = PreguntaRepository()
        // REEMPLAZAR CON TU API KEY REAL O USAR REMOTE CONFIG
        geminiRepository = GeminiRepository("AIzaSyBwMQK0VPmkyT7DeU92Om4Jl4s5qFUJR4w") 
        sessionManager = SessionManager(this)

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        
        // Verificar si es edición
        if (intent.hasExtra(Constants.INTENT_QUIZ)) {
            val quiz = intent.getParcelableExtra<Quiz>(Constants.INTENT_QUIZ)
            if (quiz != null) {
                isEditMode = true
                quizActual = quiz
                cargarDatosQuiz()
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (isEditMode) "Editar Quiz" else "Crear Quiz"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        preguntaAdapter = PreguntaAdapter { pregunta, _ ->
            // Al hacer click en una pregunta (opcional: editar pregunta)
            mostrarDialogoEditarPregunta(pregunta)
        }
        binding.rvQuestions.layoutManager = LinearLayoutManager(this)
        binding.rvQuestions.adapter = preguntaAdapter
    }

    private fun setupListeners() {
        binding.btnAddQuestionManual.setOnClickListener {
            mostrarDialogoAgregarPregunta()
        }

        binding.btnGenerateAI.setOnClickListener {
            mostrarDialogoGenerarIA()
        }

        binding.btnSaveQuiz.setOnClickListener {
            guardarQuiz()
        }
    }

    /**
     * Carga los datos de un quiz existente para edición, incluidas sus preguntas.
     */
    private fun cargarDatosQuiz() {
        binding.etTitle.setText(quizActual.titulo)
        binding.etDuration.setText(quizActual.duracion_min.toString())
        binding.switchActive.isChecked = quizActual.isActivo
        
        // Cargar preguntas si no vienen en el objeto
        if (quizActual.preguntas.isEmpty() && quizActual.id_quiz != null) {
            lifecycleScope.launch {
                val result = preguntaRepository.obtenerPreguntasPorQuiz(quizActual.id_quiz!!)
                result.onSuccess { preguntas ->
                    quizActual.preguntas = preguntas.toMutableList()
                    preguntaAdapter.submitList(quizActual.preguntas.toList())
                    actualizarContadorPreguntas()
                }
            }
        } else {
            preguntaAdapter.submitList(quizActual.preguntas.toList())
            actualizarContadorPreguntas()
        }
    }

    private fun mostrarDialogoAgregarPregunta() {
        val dialogBinding = DialogAddQuestionBinding.inflate(layoutInflater)
        
        AlertDialog.Builder(this)
            .setTitle("Agregar Pregunta")
            .setView(dialogBinding.root)
            .setPositiveButton("Agregar") { _, _ ->
                val enunciado = dialogBinding.etEnunciado.text.toString()
                val op1 = dialogBinding.etOption1.text.toString()
                val op2 = dialogBinding.etOption2.text.toString()
                val op3 = dialogBinding.etOption3.text.toString()
                val op4 = dialogBinding.etOption4.text.toString()
                
                if (enunciado.isNotEmpty() && op1.isNotEmpty() && op2.isNotEmpty()) {
                    val opciones = mutableListOf<Opcion>()
                    opciones.add(Opcion(UUID.randomUUID().toString(), op1, dialogBinding.radio1.isChecked))
                    opciones.add(Opcion(UUID.randomUUID().toString(), op2, dialogBinding.radio2.isChecked))
                    if (op3.isNotEmpty()) opciones.add(Opcion(UUID.randomUUID().toString(), op3, dialogBinding.radio3.isChecked))
                    if (op4.isNotEmpty()) opciones.add(Opcion(UUID.randomUUID().toString(), op4, dialogBinding.radio4.isChecked))
                    
                    val pregunta = Pregunta(
                        id_pregunta = null,
                        id_quiz = quizActual.id_quiz,
                        enunciado = enunciado,
                        tipo = Pregunta.TIPO_MULTIPLE,
                        puntaje = 10.0,
                        opciones = opciones
                    )
                    
                    quizActual.preguntas.add(pregunta)
                    preguntaAdapter.submitList(quizActual.preguntas.toList())
                    actualizarContadorPreguntas()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun mostrarDialogoEditarPregunta(pregunta: Pregunta) {
        // Implementación futura
    }

    private fun mostrarDialogoGenerarIA() {
        val view = layoutInflater.inflate(R.layout.dialog_generate_ai, null)
        val etTopic = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etTopic)
        val etCount = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etCount)
        val spinnerDiff = view.findViewById<android.widget.Spinner>(R.id.spinnerDifficulty)

        val difficulties = arrayOf("fácil", "intermedio", "difícil")
        spinnerDiff.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, difficulties)

        AlertDialog.Builder(this)
            .setTitle("Generar con IA")
            .setView(view)
            .setPositiveButton("Generar") { _, _ ->
                val topic = etTopic.text.toString()
                val countStr = etCount.text.toString()
                val difficulty = spinnerDiff.selectedItem.toString()

                if (topic.isNotEmpty() && countStr.isNotEmpty()) {
                    generarPreguntasIA(topic, countStr.toInt(), difficulty)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Solicita a Gemini la generación de preguntas sobre un tema específico.
     */
    private fun generarPreguntasIA(topic: String, count: Int, difficulty: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.alpha = 0.5f
        
        lifecycleScope.launch {
            val result = geminiRepository.generateQuiz(topic, count, difficulty)
            
            binding.progressBar.visibility = View.GONE
            binding.contentLayout.alpha = 1.0f
            
            result.onSuccess { preguntas ->
                quizActual.preguntas.addAll(preguntas)
                preguntaAdapter.submitList(quizActual.preguntas.toList())
                actualizarContadorPreguntas()
                Toast.makeText(this@CreateQuizActivity, "${preguntas.size} preguntas generadas", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(this@CreateQuizActivity, "Error IA: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Guarda el quiz completo (datos básicos y preguntas) en Firestore.
     */
    private fun guardarQuiz() {
        val titulo = binding.etTitle.text.toString()
        val duracionStr = binding.etDuration.text.toString()
        
        if (titulo.isEmpty() || duracionStr.isEmpty()) {
            Toast.makeText(this, "Título y duración son requeridos", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (quizActual.preguntas.isEmpty()) {
            Toast.makeText(this, "Agrega al menos una pregunta", Toast.LENGTH_SHORT).show()
            return
        }

        quizActual.titulo = titulo
        quizActual.duracion_min = duracionStr.toInt()
        quizActual.isActivo = binding.switchActive.isChecked
        quizActual.num_preguntas = quizActual.preguntas.size
        
        if (!isEditMode) {
            quizActual.id_creador = sessionManager.getUserId()
        }

        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            if (isEditMode) {
                // Actualizar quiz existente
                quizRepository.actualizarQuiz(quizActual)
                
                // Guardar preguntas nuevas
                quizActual.preguntas.filter { it.id_pregunta == null }.forEach { pregunta ->
                    pregunta.id_quiz = quizActual.id_quiz
                    preguntaRepository.crearPregunta(pregunta)
                }
                
                Toast.makeText(this@CreateQuizActivity, "Quiz actualizado", Toast.LENGTH_SHORT).show()
            } else {
                // Crear nuevo quiz
                val result = quizRepository.crearQuiz(quizActual)
                result.onSuccess { quizId ->
                    // Guardar todas las preguntas
                    quizActual.preguntas.forEach { pregunta ->
                        pregunta.id_quiz = quizId
                        preguntaRepository.crearPregunta(pregunta)
                    }
                    Toast.makeText(this@CreateQuizActivity, "Quiz creado exitosamente", Toast.LENGTH_SHORT).show()
                }
            }
            
            binding.progressBar.visibility = View.GONE
            finish()
        }
    }

    private fun actualizarContadorPreguntas() {
        binding.tvQuestionCount.text = "Total Preguntas: ${quizActual.preguntas.size}"
    }
}