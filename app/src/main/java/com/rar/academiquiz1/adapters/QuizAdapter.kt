package com.rar.academiquiz1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rar.academiquiz1.databinding.ItemQuizBinding
import com.rar.academiquiz1.models.Quiz

/**
 * Adaptador para mostrar una lista de [Quiz] en un RecyclerView.
 * Soporta modos de visualización diferenciados para estudiantes y maestros/administradores.
 *
 * @property onQuizClick Callback invocado al hacer clic en un quiz (para tomarlo o ver detalles).
 * @property onEditClick Callback opcional para editar un quiz (solo visible para maestros/admin).
 * @property onDeleteClick Callback opcional para eliminar un quiz (solo visible para maestros/admin).
 * @property isTeacher Bandera que indica si el usuario actual tiene privilegios de edición (Maestro/Admin).
 */
class QuizAdapter(
    private val onQuizClick: (Quiz) -> Unit,
    private val onEditClick: ((Quiz) -> Unit)? = null,
    private val onDeleteClick: ((Quiz) -> Unit)? = null,
    private val isTeacher: Boolean = false
) : ListAdapter<Quiz, QuizAdapter.QuizViewHolder>(QuizDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder que mantiene las referencias a las vistas de un item de quiz.
     */
    inner class QuizViewHolder(private val binding: ItemQuizBinding) : RecyclerView.ViewHolder(binding.root) {
        
        /**
         * Vincula los datos del quiz a los elementos de la interfaz.
         * Configura la visibilidad de los botones de administración según el rol.
         *
         * @param quiz El objeto [Quiz] a mostrar.
         */
        fun bind(quiz: Quiz) {
            binding.tvTitle.text = quiz.titulo
            binding.tvDuration.text = "${quiz.duracion_min} min"
            binding.tvQuestions.text = "${quiz.num_preguntas} preguntas"
            
            if (isTeacher) {
                binding.layoutTeacherStats.visibility = View.VISIBLE
                binding.tvAttempts.text = "Intentos: ${quiz.intentos_totales}"
                binding.tvAverage.text = "Promedio: ${quiz.getPromedioFormatted()}"
                
                binding.btnEdit.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE
                
                binding.btnEdit.setOnClickListener { onEditClick?.invoke(quiz) }
                binding.btnDelete.setOnClickListener { onDeleteClick?.invoke(quiz) }
            } else {
                binding.layoutTeacherStats.visibility = View.GONE
                binding.btnEdit.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                onQuizClick(quiz)
            }
        }
    }

    /**
     * Callback para calcular las diferencias entre listas de quizzes y optimizar actualizaciones.
     */
    class QuizDiffCallback : DiffUtil.ItemCallback<Quiz>() {
        override fun areItemsTheSame(oldItem: Quiz, newItem: Quiz): Boolean {
            return oldItem.id_quiz == newItem.id_quiz
        }

        override fun areContentsTheSame(oldItem: Quiz, newItem: Quiz): Boolean {
            return oldItem == newItem
        }
    }
}