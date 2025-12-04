package com.rar.academiquiz1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Respuesta(
    var id_respuesta: String? = null,
    var id_resultado: String? = null,
    var id_pregunta: String? = null,
    var id_opcion: String? = null,
    var isCorrecta: Boolean = false
) : Parcelable {

    constructor() : this(null, null, null, null, false)

    constructor(id_pregunta: String?, id_opcion: String?, correcta: Boolean) : this(
        null, null, id_pregunta, id_opcion, correcta
    )

    companion object {
        const val COLLECTION = "respuestas"
    }
}