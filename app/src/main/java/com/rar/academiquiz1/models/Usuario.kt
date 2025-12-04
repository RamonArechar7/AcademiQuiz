package com.rar.academiquiz1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Usuario(
    var id_usuario: String? = null,
    var nombre: String? = null,
    var email: String? = null,
    var rol: String? = null, // ESTUDIANTE, MAESTRO, ADMIN
    var fecha_registro: Date? = null,
    var ultimo_acceso: Date? = null
) : Parcelable {

    constructor() : this(null, null, null, null, null, null)

    constructor(nombre: String?, email: String?, rol: String?) : this(
        null, nombre, email, rol, Date(), Date()
    )

    companion object {
        const val COLLECTION = "usuarios"
    }
}