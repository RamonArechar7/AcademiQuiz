package com.rar.academiquiz1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rar.academiquiz1.databinding.ItemPreguntaBinding
import com.rar.academiquiz1.models.Opcion
import com.rar.academiquiz1.models.Pregunta

/**
 * Adaptador para mostrar preguntas y sus opciones durante la realización de un quiz.
 * Maneja la selección de respuestas mediante RadioButtons dinámicos.
 *
 * @property onOptionSelected Callback invocado cuando el usuario selecciona una opción.
 */
class PreguntaAdapter(
    private val onOptionSelected: ((Pregunta, Opcion) -> Unit)? = null
) : ListAdapter<Pregunta, PreguntaAdapter.PreguntaViewHolder>(PreguntaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreguntaViewHolder {
        val binding = ItemPreguntaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PreguntaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PreguntaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder para los items de pregunta.
     */
    inner class PreguntaViewHolder(private val binding: ItemPreguntaBinding) : RecyclerView.ViewHolder(binding.root) {
        
        /**
         * Vincula una pregunta a la vista, generando dinámicamente los RadioButtons para las opciones.
         *
         * @param pregunta La pregunta a mostrar.
         */
        fun bind(pregunta: Pregunta) {
            binding.tvEnunciado.text = pregunta.enunciado
            binding.radioGroup.removeAllViews()
            binding.radioGroup.clearCheck()

            pregunta.opciones.forEach { opcion ->
                val radioButton = RadioButton(binding.root.context)
                radioButton.text = opcion.texto
                radioButton.id = View.generateViewId()
                
                radioButton.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        onOptionSelected?.invoke(pregunta, opcion)
                    }
                }
                
                binding.radioGroup.addView(radioButton)
            }
        }
    }

    /**
     * Callback para DiffUtil de Preguntas.
     */
    class PreguntaDiffCallback : DiffUtil.ItemCallback<Pregunta>() {
        override fun areItemsTheSame(oldItem: Pregunta, newItem: Pregunta): Boolean {
            // Usar enunciado como fallback si id es nulo (conveniente para preguntas recién creadas localmente)
            return (oldItem.id_pregunta ?: oldItem.enunciado) == (newItem.id_pregunta ?: newItem.enunciado)
        }

        override fun areContentsTheSame(oldItem: Pregunta, newItem: Pregunta): Boolean {
            return oldItem == newItem
        }
    }
}
