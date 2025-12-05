package com.rar.academiquiz1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rar.academiquiz1.databinding.ItemResultDetailBinding
import com.rar.academiquiz1.models.Pregunta
import com.rar.academiquiz1.models.Respuesta

/**
 * Adaptador para mostrar el detalle de cada pregunta en la revisión de un examen.
 * Indica si la respuesta del usuario fue correcta o incorrecta y muestra la opción correcta en caso de error.
 */
class ResultDetailAdapter(
    private var items: List<DetailItem> = emptyList()
) : RecyclerView.Adapter<ResultDetailAdapter.DetailViewHolder>() {

    /**
     * Modelo de datos para representar un item en la lista de detalles.
     * Combina la pregunta, la respuesta del usuario y las cadenas de texto correspondientes.
     */
    data class DetailItem(
        val pregunta: Pregunta,
        val respuesta: Respuesta?,
        val opcionSeleccionada: String?,
        val opcionCorrecta: String?
    )

    /**
     * Actualiza la lista de items y notifica al adaptador.
     */
    fun submitList(newItems: List<DetailItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ItemResultDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    /**
     * ViewHolder para el detalle de una pregunta.
     */
    inner class DetailViewHolder(private val binding: ItemResultDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Vincula los datos del detalle a la vista.
         * Muestra íconos y colores indicativos de acierto o error.
         */
        fun bind(item: DetailItem) {
            binding.tvQuestion.text = item.pregunta.enunciado

            val isCorrect = item.respuesta?.isCorrecta == true
            
            if (isCorrect) {
                binding.tvYourAnswer.text = "Tu respuesta: ${item.opcionSeleccionada}"
                binding.tvYourAnswer.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
                binding.ivStatus.setImageResource(android.R.drawable.checkbox_on_background)
                binding.ivStatus.setColorFilter(binding.root.context.getColor(android.R.color.holo_green_dark))
                binding.tvCorrectAnswer.visibility = View.GONE
            } else {
                binding.tvYourAnswer.text = "Tu respuesta: ${item.opcionSeleccionada ?: "Sin responder"}"
                binding.tvYourAnswer.setTextColor(binding.root.context.getColor(android.R.color.holo_red_dark))
                binding.ivStatus.setImageResource(android.R.drawable.ic_delete)
                binding.ivStatus.setColorFilter(binding.root.context.getColor(android.R.color.holo_red_dark))
                
                binding.tvCorrectAnswer.visibility = View.VISIBLE
                binding.tvCorrectAnswer.text = "Correcta: ${item.opcionCorrecta}"
            }
        }
    }
}
