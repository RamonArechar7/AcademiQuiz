package com.rar.academiquiz1.utils

import android.content.Context
import android.content.SharedPreferences
import com.rar.academiquiz1.models.Usuario
import com.google.gson.Gson

/**
 * Gestor de sesiones basado en SharedPreferences.
 * Almacena información básica del usuario logueado para acceso rápido sin ir a Firestore constantemente.
 */
class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()
    private val gson = Gson()

    /**
     * Guarda la sesión del usuario tras un login exitoso.
     * @param usuario El objeto [Usuario] con los datos a guardar.
     */
    fun saveUserSession(usuario: Usuario) {
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true)
        editor.putString(Constants.KEY_USER_ID, usuario.id_usuario)
        editor.putString(Constants.KEY_USER_NAME, usuario.nombre)
        editor.putString(Constants.KEY_USER_EMAIL, usuario.email)
        editor.putString(Constants.KEY_USER_ROLE, usuario.rol)
        editor.apply()
    }
    
    /**
     * Actualiza los datos almacenados del usuario (ej. tras editar perfil).
     */
    fun saveUser(usuario: Usuario) {
        // Actualizar datos sin cambiar estado de login
        editor.putString(Constants.KEY_USER_NAME, usuario.nombre)
        editor.putString(Constants.KEY_USER_ROLE, usuario.rol)
        editor.apply()
    }

    /**
     * Verifica si existe una sesión activa.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(): String? {
        return prefs.getString(Constants.KEY_USER_ID, null)
    }

    fun getUserName(): String? {
        return prefs.getString(Constants.KEY_USER_NAME, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(Constants.KEY_USER_EMAIL, null)
    }

    fun getUserRol(): String? {
        return prefs.getString(Constants.KEY_USER_ROLE, "ESTUDIANTE")
    }

    /**
     * Cierra la sesión y borra todos los datos guardados.
     */
    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}