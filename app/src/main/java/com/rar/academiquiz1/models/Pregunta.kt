package com.rar.academiquiz1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pregunta(
    var id_pregunta: String? = null,
    var id_quiz: String? = null,
    var enunciado: String? = null,
    var tipo: String? = null, // opcion_multiple, verdadero_falso
    var puntaje: Double = 0.0,
    var opciones: MutableList<Opcion> = ArrayList()
) : Parcelable {

    constructor() : this(null, null, null, null, 0.0, ArrayList())

    fun getOpcionCorrecta(): Opcion? {
        return opciones.find { it.es_correcta }
    }

    companion object {
        const val COLLECTION = "preguntas"
        const val TIPO_MULTIPLE = "opcion_multiple"
    }
}