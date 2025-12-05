package com.rar.academiquiz1.utils

/**
 * Constantes globales utilizadas en toda la aplicación.
 * Incluye claves para SharedPreferences, nombres de Intents y configuraciones generales.
 */
object Constants {
    const val PREFS_NAME = "AcademiQuizPrefs"
    const val KEY_IS_LOGGED_IN = "isLoggedIn"
    const val KEY_USER_ID = "userId"
    const val KEY_USER_NAME = "userName"
    const val KEY_USER_EMAIL = "userEmail"
    const val KEY_USER_ROLE = "userRole"

    const val INTENT_QUIZ = "intent_quiz"
    const val INTENT_RESULT_ID = "intent_result_id"
    const val INTENT_QUIZ_ID = "intent_quiz_id"
    
    const val MIN_PASSWORD_LENGTH = 6
}