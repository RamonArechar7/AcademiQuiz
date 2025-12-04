package com.rar.academiquiz1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rar.academiquiz1.databinding.ItemPreguntaBinding
import com.rar.academiquiz1.models.Opcion
import com.rar.academiquiz1.models.Pregunta

class PreguntaAdapter(
    private val onOpcionSelected: (Pregunta, Opcion) -> Unit
) : ListAdapter<Pregunta, PreguntaAdapter.PreguntaViewHolder>(PreguntaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreguntaViewHolder {
        val binding = ItemPreguntaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PreguntaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PreguntaViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    inner class PreguntaViewHolder(
        private val binding: ItemPreguntaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pregunta: Pregunta, numero: Int) {
            binding.apply {
                tvPreguntaNumero.text = "Pregunta $numero"
                tvEnunciado.text = pregunta.enunciado
                tvPuntaje.text = "Valor: ${pregunta.puntaje} pts"

                radioGroupOpciones.removeAllViews()

                pregunta.opciones.forEachIndexed { index, opcion ->
                    val radioButton = android.widget.RadioButton(root.context).apply {
                        id = android.view.View.generateViewId()
                        text = opcion.texto
                        textSize = 16f
                        setPadding(16, 16, 16, 16)
                        setOnClickListener {
                            onOpcionSelected(pregunta, opcion)
                        }
                    }
                    radioGroupOpciones.addView(radioButton)
                }
            }
        }
    }

    class PreguntaDiffCallback : DiffUtil.ItemCallback<Pregunta>() {
        override fun areItemsTheSame(oldItem: Pregunta, newItem: Pregunta): Boolean {
            return oldItem.enunciado == newItem.enunciado // Using enunciado as ID since id_pregunta might be null for new questions
        }

        override fun areContentsTheSame(oldItem: Pregunta, newItem: Pregunta): Boolean {
            return oldItem == newItem
        }
    }
}
