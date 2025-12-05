package com.rar.academiquiz1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rar.academiquiz1.databinding.ItemResultBinding
import com.rar.academiquiz1.models.Resultado
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adaptador para mostrar el historial de resultados de quizzes.
 * Muestra el puntaje, fecha y estado de aprobación.
 *
 * @property onResultClick Callback invocado al seleccionar un resultado para ver detalles.
 */
class ResultAdapter(
    private val onResultClick: (Resultado) -> Unit
) : ListAdapter<Resultado, ResultAdapter.ResultViewHolder>(ResultDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder para los items de resultado.
     */
    inner class ResultViewHolder(
        private val binding: ItemResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        /**
         * Vincula los datos del resultado a la vista.
         * Aplica colores condicionales según el puntaje obtenido.
         *
         * @param resultado El objeto [Resultado] a mostrar.
         */
        fun bind(resultado: Resultado) {
            binding.apply {
                tvQuizName.text = resultado.nombre_quiz
                tvScore.text = resultado.getPuntajeFormatted()
                tvDate.text = dateFormat.format(resultado.fecha_intento)
                tvDuration.text = "${resultado.duracion_usada} min"
                tvStatus.text = resultado.getEstado()

                // Color según calificación (Verde: Excelente, Amarillo: Aprobado, Rojo: Reprobado)
                val color = when {
                    resultado.puntaje >= 90 -> com.rar.academiquiz1.R.color.success
                    resultado.puntaje >= 70 -> com.rar.academiquiz1.R.color.warning
                    else -> com.rar.academiquiz1.R.color.error
                }

                tvScore.setTextColor(root.context.getColor(color))
                tvStatus.setTextColor(root.context.getColor(color))

                root.setOnClickListener { onResultClick(resultado) }
            }
        }
    }

    class ResultDiffCallback : DiffUtil.ItemCallback<Resultado>() {
        override fun areItemsTheSame(oldItem: Resultado, newItem: Resultado): Boolean {
            return oldItem.id_resultado == newItem.id_resultado
        }

        override fun areContentsTheSame(oldItem: Resultado, newItem: Resultado): Boolean {
            return oldItem == newItem
        }
    }
}
