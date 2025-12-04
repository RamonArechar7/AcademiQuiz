package com.rar.academiquiz1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rar.academiquiz1.databinding.ItemQuizBinding
import com.rar.academiquiz1.models.Quiz

class QuizAdapter(
    private val onQuizClick: (Quiz) -> Unit,
    private val onEditClick: ((Quiz) -> Unit)? = null,
    private val onDeleteClick: ((Quiz) -> Unit)? = null,
    private val isTeacher: Boolean = false
) : ListAdapter<Quiz, QuizAdapter.QuizViewHolder>(QuizDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = ItemQuizBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuizViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuizViewHolder(
        private val binding: ItemQuizBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(quiz: Quiz) {
            binding.apply {
                tvTitulo.text = quiz.titulo
                tvNumPreguntas.text = "${quiz.num_preguntas} preguntas"
                tvDuracion.text = "${quiz.duracion_min} min"

                if (isTeacher) {
                    tvIntentos.text = "${quiz.intentos_totales} intentos"
                    tvPromedio.text = quiz.getPromedioFormatted()
                    groupEstadisticas.visibility = android.view.View.VISIBLE
                } else {
                    groupEstadisticas.visibility = android.view.View.GONE
                }

                // Estado activo/inactivo
                val estadoColor = if (quiz.isActivo) {
                    android.R.color.holo_green_light
                } else {
                    android.R.color.darker_gray
                }
                viewEstado.setBackgroundColor(
                    binding.root.context.getColor(estadoColor)
                )

                // Mostrar botones de edición solo para maestros/admins
                if (isTeacher) {
                    btnEdit.visibility = android.view.View.VISIBLE
                    btnDelete.visibility = android.view.View.VISIBLE

                    btnEdit.setOnClickListener {
                        onEditClick?.invoke(quiz)
                    }

                    btnDelete.setOnClickListener {
                        onDeleteClick?.invoke(quiz)
                    }
                } else {
                    btnEdit.visibility = android.view.View.GONE
                    btnDelete.visibility = android.view.View.GONE
                }

                root.setOnClickListener {
                    onQuizClick(quiz)
                }
            }
        }
    }

    class QuizDiffCallback : DiffUtil.ItemCallback<Quiz>() {
        override fun areItemsTheSame(oldItem: Quiz, newItem: Quiz): Boolean {
            return oldItem.id_quiz == newItem.id_quiz
        }

        override fun areContentsTheSame(oldItem: Quiz, newItem: Quiz): Boolean {
            return oldItem == newItem
        }
    }
}