package com.rar.academiquiz1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Resultado(
    var id_resultado: String? = null,
    var id_usuario: String? = null,
    var id_quiz: String? = null,
    var puntaje: Double = 0.0,
    var fecha_intento: Date? = null,
    var duracion_usada: Int = 0,
    var nombre_quiz: String? = null,
    var nombre_usuario: String? = null
) : Parcelable {

    constructor() : this(null, null, null, 0.0, null, 0, null, null)

    constructor(id_usuario: String?, id_quiz: String?, puntaje: Double, duracion_usada: Int, nombre_quiz: String?, nombre_usuario: String?) : this(
        null, id_usuario, id_quiz, puntaje, Date(), duracion_usada, nombre_quiz, nombre_usuario
    )

    fun getPuntajeFormatted(): String {
        return String.format("%.1f%%", puntaje)
    }

    fun getEstado(): String {
        return if (puntaje >= 70) "Aprobado" else "Reprobado"
    }

    companion object {
        const val COLLECTION = "resultados"
    }
}