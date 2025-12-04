package com.rar.academiquiz1.utils

import android.content.Context
import android.content.SharedPreferences
import com.rar.academiquiz1.models.Usuario

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREF_NAME,
        Context.MODE_PRIVATE
    )

    fun saveUserSession(usuario: Usuario) {
        prefs.edit().apply {
            putString(Constants.KEY_USER_ID, usuario.id_usuario)
            putString(Constants.KEY_USER_EMAIL, usuario.email)
            putString(Constants.KEY_USER_NAME, usuario.nombre)
            putString(Constants.KEY_USER_ROL, usuario.rol)
            putBoolean(Constants.KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun saveUser(usuario: Usuario) {
        prefs.edit().apply {
            putString(Constants.KEY_USER_ID, usuario.id_usuario)
            putString(Constants.KEY_USER_EMAIL, usuario.email)
            putString(Constants.KEY_USER_NAME, usuario.nombre)
            putString(Constants.KEY_USER_ROL, usuario.rol)
            putBoolean(Constants.KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUserId(): String? = prefs.getString(Constants.KEY_USER_ID, null)

    fun getUserEmail(): String? = prefs.getString(Constants.KEY_USER_EMAIL, null)

    fun getUserName(): String? = prefs.getString(Constants.KEY_USER_NAME, null)

    fun getUserRol(): String? = prefs.getString(Constants.KEY_USER_ROL, null)

    fun isLoggedIn(): Boolean = prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}