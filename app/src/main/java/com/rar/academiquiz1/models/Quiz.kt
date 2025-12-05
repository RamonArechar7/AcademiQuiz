package com.rar.academiquiz1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Quiz(
    var id_quiz: String? = null,
    var titulo: String? = null,
    var num_preguntas: Int = 0,
    var duracion_min: Int = 0,
    var isActivo: Boolean = false,
    var intentos_totales: Int = 0,
    var promedio_general: Double = 0.0,
    var id_creador: String? = null,
    var preguntas: MutableList<Pregunta> = ArrayList()
) : Parcelable {

    constructor() : this(null, null, 0, 0, false, 0, 0.0, null, ArrayList())

    constructor(titulo: String?, num_preguntas: Int, duracion_min: Int, id_creador: String?) : this(
        null, titulo, num_preguntas, duracion_min, true, 0, 0.0, id_creador, ArrayList()
    )

    fun getPromedioFormatted(): String {
        return String.format("%.1f", promedio_general)
    }

    companion object {
        const val COLLECTION = "quizzes"
    }
}