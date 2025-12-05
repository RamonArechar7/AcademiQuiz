package com.rar.academiquiz1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Opcion(
    var id_opcion: String? = null,
    var texto: String? = null,
    var es_correcta: Boolean = false
) : Parcelable {
    constructor() : this(null, null, false)
}