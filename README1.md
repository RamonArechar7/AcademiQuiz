# AcademiQuiz - Sistema de Cuestionarios Educativos

<div align="center">

![AcademiQuiz Logo](docs/screenshots/login_screen_1764964596522.png)

**Aplicación Android para la creación, gestión y evaluación de cuestionarios educativos**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple.svg)](https://kotlinlang.org/)
[![Firebase](https://img.shields.io/badge/Firebase-32.7.0-orange.svg)](https://firebase.google.com/)
[![Android](https://img.shields.io/badge/Android-API%2024+-green.svg)](https://developer.android.com/)
[![Gemini AI](https://img.shields.io/badge/Gemini%20AI-Enabled-blue.svg)](https://ai.google.dev/)

</div>

---

## Tabla de Contenidos

- [Descripción del Proyecto](#descripción-del-proyecto)
- [Características Principales](#características-principales)
- [Stack Tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Instalación y Configuración](#instalación-y-configuración)
- [Capturas de Pantalla](#capturas-de-pantalla)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Código Fuente Completo](#código-fuente-completo)
- [Contribución](#contribución)
- [Licencia](#licencia)

---

## Descripción del Proyecto

**AcademiQuiz** es una aplicación Android nativa completa para la gestión de cuestionarios educativos. Diseñada para facilitar la evaluación de conocimientos en entornos educativos, la aplicación soporta tres roles de usuario diferentes (Estudiante, Maestro y Admin) con funcionalidades específicas para cada uno.

La aplicación permite a los maestros crear cuestionarios de forma manual o utilizando Google Gemini AI para generar preguntas automáticamente, mientras que los estudiantes pueden tomar los quizzes disponibles y visualizar sus resultados detallados.

### Objetivos del Proyecto

- **Educación Accesible**: Facilitar la creación y distribución de evaluaciones educativas
- **Eficiencia**: Automatizar la generación de preguntas usando IA
- **Análisis**: Proporcionar estadísticas detalladas de desempeño
- **Escalabilidad**: Arquitectura robusta con Firebase para manejo de datos en la nube

---

## Características Principales

### Para Estudiantes

- Visualización de quizzes disponibles
- Tomar cuestionarios con temporizador
- Visualización de resultados en tiempo real
- Historial completo de intentos
- Estadísticas de desempeño personalizadas

### Para Maestros

- Creación manual de cuestionarios
- Generación automática de preguntas con Google Gemini AI
- Configuración de dificultad (Fácil, Intermedio, Difícil)
- Gestión completa de quizzes (editar, eliminar, activar/desactivar)
- Visualización de estadísticas de clase
- Seguimiento del promedio y número de intentos por quiz

### Para Administradores

- Todas las funcionalidades de Maestro
- Gestión completa de usuarios
- Creación y eliminación de cuentas
- Modificación de roles de usuario
- Estadísticas globales del sistema

### Inteligencia Artificial

- Integración con **Google Gemini AI**
- Generación automática de preguntas de opción múltiple
- Soporte para diferentes niveles de dificultad
- Validación automática de respuestas correctas
- Generación de múltiples preguntas en segundos

---

## Stack Tecnológico

### Lenguaje y Framework

- **Kotlin 1.9.0**: Lenguaje principal de desarrollo
- **Android SDK API 24+**: Compatible con Android 7.0 Nougat y superior
- **Jetpack Compose**: Para componentes modernos de UI (parcial)
- **View Binding**: Vinculación de vistas segura

### Backend y Base de Datos

- **Firebase Authentication**: Gestión de usuarios y autenticación
- **Cloud Firestore**: Base de datos NoSQL en tiempo real
- **Firebase Analytics**: Análisis de uso de la aplicación

### Inteligencia Artificial

- **Google Gemini AI 0.9.0**: Generación de preguntas con IA
- **Modelos soportados**: `gemini-2.0-flash`, `gemini-flash-latest`

### Bibliotecas Principales

| Biblioteca | Versión | Uso |
|-----------|---------|-----|
| Firebase BOM | 32.7.0 | Plataforma Firebase |
| Material Design | 1.11.0 | Componentes UI Material |
| Lifecycle | 2.6.2 | ViewModel, LiveData, Runtime |
| Coroutines | 1.7.3 | Programación asíncrona |
| RecyclerView | 1.3.2 | Listas eficientes |
| Navigation | 2.7.6 | Navegación entre pantallas |

---

## Arquitectura

El proyecto utiliza una **arquitectura MVVM (Model-View-ViewModel)** con **Repository Pattern** para la separación de responsabilidades y facilitar el testing y mantenimiento.

```mermaid
graph TD
    A[Activities/UI] -->|Observa| B[ViewModels]
    B -->|Solicita datos| C[Repositories]
    C -->|Accede a| D[Firebase Firestore]
    C -->|Autenticación| E[Firebase Auth]
    C -->|IA| F[Gemini AI API]
    
    G[Models] -.Define estructura.- A
    G -.Define estructura.- B
    G -.Define estructura.- C
    
    H[Utils] -.Soporte.- A
    H -.Soporte.- B
    H -.Soporte.- C
    
    I[Adapters] -.Renderiza.- A
```

---

## Instalación y Configuración

### Requisitos Previos

- **Android Studio** Arctic Fox o superior
- **JDK 11** o superior
- **Cuenta de Firebase** con proyecto configurado
- **API Key de Google Gemini AI**

### Pasos de Instalación

#### 1. Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/AcademiQuiz.git
cd AcademiQuiz
```

#### 2. Configurar Firebase

1. Crear un proyecto en [Firebase Console](https://console.firebase.google.com/)
2. Agregar una aplicación Android con el paquete `com.rar.academiquiz1`
3. Descargar el archivo `google-services.json`
4. Colocar `google-services.json` en `app/`

5. Habilitar en Firebase Console:
    - **Authentication** → Sign-in method → Email/Password
    - **Firestore Database** → Create database

6. Configurar reglas de Firestore:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

#### 3. Configurar Gemini AI

1. Obtener una API Key de [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Editar `CreateQuizActivity.kt` línea 36:

```kotlin
geminiRepository = GeminiRepository("TU_API_KEY_AQUI")
```

> **Nota de Seguridad**: Para producción, almacena la API key en variables de entorno o usa Firebase Remote Config.

#### 4. Sincronizar y Compilar

```bash
# En Android Studio:
# File > Sync Project with Gradle Files
# Build > Make Project
```

#### 5. Ejecutar la Aplicación

- Conectar un dispositivo Android físico o iniciar un emulador
- Click en Run en Android Studio

---

## Capturas de Pantalla

<div align="center">

### Pantalla de Login
![Login](docs/screenshots/login_screen_1764964596522.png)

### Dashboard Principal
![Dashboard](docs/screenshots/dashboard_screen_1764964613868.png)

### Crear Quiz con IA
![Create Quiz](docs/screenshots/create_quiz_screen_1764964627704.png)

### Tomar Quiz
![Take Quiz](docs/screenshots/take_quiz_screen_1764964641642.png)

### Resultados
![Results](docs/screenshots/results_screen_1764964654733.png)

### Gestión de Usuarios (Admin)
![Users Management](docs/screenshots/users_management_screen_1764964669667.png)

</div>

---

## Estructura del Proyecto

```
AcademiQuiz1/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/rar/academiquiz1/
│   │   │   │   ├── activities/          # Activities de la aplicación
│   │   │   │   ├── adapters/            # RecyclerView Adapters
│   │   │   │   ├── models/              # Modelos de datos
│   │   │   │   ├── repositories/        # Capa de datos
│   │   │   │   └── utils/               # Utilidades
│   │   │   ├── res/                     # Recursos
│   │   │   └── AndroidManifest.xml      # Manifiesto de la app
│   ├── build.gradle.kts                 # Configuración de Gradle
│   └── google-services.json            # Config de Firebase (no en repo)
├── docs/
├── .gitignore
└── README.md
```

---

## Código Fuente Completo

A continuación se presenta el código fuente completo de **TODOS** los archivos del proyecto.

### Models (Modelos de Datos)

#### Quiz.kt

```kotlin
package com.rar.academiquiz1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modelo de datos para representar un cuestionario (Quiz).
 *
 * @property id_quiz Identificador único del quiz en Firestore
 * @property titulo Título descriptivo del cuestionario
 * @property num_preguntas Número total de preguntas del quiz
 * @property duracion_min Duración máxima en minutos para completar el quiz
 * @property isActivo Indica si el quiz está activo y disponible para estudiantes
 * @property intentos_totales Número total de intentos realizados por todos los usuarios
 * @property promedio_general Promedio de calificaciones de todos los intentos
 * @property id_creador ID del usuario que creó el quiz (maestro o admin)
 * @property preguntas Lista mutable de preguntas asociadas al quiz
 */
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

    /** Constructor vacío requerido por Firestore */
    constructor() : this(null, null, 0, 0, false, 0, 0.0, null, ArrayList())

    /**
     * Constructor para crear un nuevo quiz con parámetros básicos
     *
     * @param titulo Título del quiz
     * @param num_preguntas Número de preguntas
     * @param duracion_min Duración en minutos
     * @param id_creador ID del creador
     */
    constructor(titulo: String?, num_preguntas: Int, duracion_min: Int, id_creador: String?) : this(
        null, titulo, num_preguntas, duracion_min, true, 0, 0.0, id_creador, ArrayList()
    )

    /**
     * Obtiene el promedio formateado a 1 decimal
     *
     * @return String con el promedio formateado (ej: "85.5")
     */
    fun getPromedioFormatted(): String {
        return String.format("%.1f", promedio_general)
    }

    companion object {
        /** Nombre de la colección en Firestore */
        const val COLLECTION = "quizzes"
    }
}
```

#### Usuario.kt

```kotlin
package com.rar.academiquiz1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Modelo de datos para representar un usuario del sistema.
 *
 * Los usuarios pueden tener uno de tres roles:
 * - ESTUDIANTE: Puede tomar quizzes y ver sus resultados
 * - MAESTRO: Puede crear y gestionar quizzes además de las funcionalidades de estudiante
 * - ADMIN: Tiene todas las funcionalidades anteriores más gestión de usuarios
 *
 * @property id_usuario Identificador único del usuario (UID de Firebase Auth)
 * @property nombre Nombre completo del usuario
 * @property email Correo electrónico del usuario
 * @property rol Rol del usuario (ESTUDIANTE, MAESTRO, ADMIN)
 * @property fecha_registro Fecha y hora de registro del usuario
 * @property ultimo_acceso Fecha y hora del último acceso del usuario
 */
@Parcelize
data class Usuario(
    var id_usuario: String? = null,
    var nombre: String? = null,
    var email: String? = null,
    var rol: String? = null, // ESTUDIANTE, MAESTRO, ADMIN
    var fecha_registro: Date? = null,
    var ultimo_acceso: Date? = null
) : Parcelable {

    /** Constructor vacío requerido por Firestore */
    constructor() : this(null, null, null, null, null, null)

    /**
     * Constructor para crear un nuevo usuario
     *
     * @param nombre Nombre del usuario
     * @param email Email del usuario
     * @param rol Rol asignado
     */
    constructor(nombre: String?, email: String?, rol: String?) : this(
        null, nombre, email, rol, Date(), Date()
    )

    companion object {
        /** Nombre de la colección en Firestore */
        const val COLLECTION = "usuarios"
    }
}
```

#### Pregunta.kt

```kotlin
package com.rar.academiquiz1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modelo de datos para representar una pregunta de un quiz.
 *
 * @property id_pregunta Identificador único de la pregunta en Firestore
 * @property id_quiz ID del quiz al que pertenece esta pregunta
 * @property enunciado Texto de la pregunta
 * @property tipo Tipo de pregunta (opcion_multiple, verdadero_falso)
 * @property puntaje Valor en puntos de la pregunta
 * @property opciones Lista mutable de opciones de respuesta
 */
@Parcelize
data class Pregunta(
    var id_pregunta: String? = null,
    var id_quiz: String? = null,
    var enunciado: String? = null,
    var tipo: String? = null, // opcion_multiple, verdadero_falso
    var puntaje: Double = 0.0,
    var opciones: MutableList<Opcion> = ArrayList()
) : Parcelable {

    /** Constructor vacío requerido por Firestore */
    constructor() : this(null, null, null, null, 0.0, ArrayList())

    /**
     * Obtiene la opción correcta de la pregunta.
     *
     * @return La primera opción marcada como correcta, o null si no existe
     */
    fun getOpcionCorrecta(): Opcion? {
        return opciones.find { it.es_correcta }
    }

    companion object {
        /** Nombre de la colección en Firestore */
        const val COLLECTION = "preguntas"
        
        /** Constante para preguntas de opción múltiple */
        const val TIPO_MULTIPLE = "opcion_multiple"
    }
}
```

#### Opcion.kt

```kotlin
package com.rar.academiquiz1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modelo de datos para representar una opción de respuesta.
 *
 * @property id_opcion Identificador único de la opción (UUID)
 * @property texto Texto de la opción de respuesta
 * @property es_correcta Indica si esta es la opción correcta
 */
@Parcelize
data class Opcion(
    var id_opcion: String? = null,
    var texto: String? = null,
    var es_correcta: Boolean = false
) : Parcelable {
    
    /** Constructor vacío requerido por Firestore */
    constructor() : this(null, null, false)
}
```

#### Resultado.kt

```kotlin
package com.rar.academiquiz1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Modelo de datos para representar el resultado de un intento de quiz.
 *
 * @property id_resultado Identificador único del resultado en Firestore
 * @property id_usuario ID del usuario que realizó el quiz
 * @property id_quiz ID del quiz que fue realizado
 * @property puntaje Calificación obtenida (porcentaje de 0-100)
 * @property fecha_intento Fecha y hora en que se completó el quiz
 * @property duracion_usada Tiempo usado en minutos
 * @property nombre_quiz Nombre del quiz (desnormalizado para consultas rápidas)
 * @property nombre_usuario Nombre del usuario (desnormalizado para consultas rápidas)
 */
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

    /** Constructor vacío requerido por Firestore */
    constructor() : this(null, null, null, 0.0, null, 0, null, null)

    /**
     * Constructor para crear un nuevo resultado
     *
     * @param id_usuario ID del usuario
     * @param id_quiz ID del quiz
     * @param puntaje Puntaje obtenido
     * @param duracion_usada Tiempo usado en minutos
     * @param nombre_quiz Nombre del quiz
     * @param nombre_usuario Nombre del usuario
     */
    constructor(id_usuario: String?, id_quiz: String?, puntaje: Double, duracion_usada: Int, nombre_quiz: String?, nombre_usuario: String?) : this(
        null, id_usuario, id_quiz, puntaje, Date(), duracion_usada, nombre_quiz, nombre_usuario
    )

    /**
     * Obtiene el puntaje formateado como porcentaje.
     *
     * @return String con formato "XX.X%" (ej: "85.5%")
     */
    fun getPuntajeFormatted(): String {
        return String.format("%.1f%%", puntaje)
    }

    /**
     * Determina el estado del resultado basado en el puntaje.
     * Umbral de aprobación: 70%
     *
     * @return "Aprobado" si puntaje >= 70, "Reprobado" en caso contrario
     */
    fun getEstado(): String {
        return if (puntaje >= 70) "Aprobado" else "Reprobado"
    }

    companion object {
        /** Nombre de la colección en Firestore */
        const val COLLECTION = "resultados"
    }
}
```

#### Respuesta.kt

```kotlin
package com.rar.academiquiz1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modelo de datos para representar una respuesta individual en un quiz.
 * Conecta una pregunta con la opción seleccionada por el usuario.
 *
 * @property id_respuesta Identificador único de la respuesta en Firestore
 * @property id_resultado ID del resultado al que pertenece esta respuesta
 * @property id_pregunta ID de la pregunta respondida
 * @property id_opcion ID de la opción seleccionada por el usuario
 * @property isCorrecta Indica si la respuesta seleccionada fue correcta
 */
@Parcelize
data class Respuesta(
    var id_respuesta: String? = null,
    var id_resultado: String? = null,
    var id_pregunta: String? = null,
    var id_opcion: String? = null,
    var isCorrecta: Boolean = false
) : Parcelable {

    /** Constructor vacío requerido por Firestore */
    constructor() : this(null, null, null, null, false)

    /**
     * Constructor para crear una nueva respuesta
     *
     * @param id_pregunta ID de la pregunta
     * @param id_opcion ID de la opción seleccionada
     * @param correcta Indica si es correcta
     */
    constructor(id_pregunta: String?, id_opcion: String?, correcta: Boolean) : this(
        null, null, id_pregunta, id_opcion, correcta
    )

    companion object {
        /** Nombre de la colección en Firestore */
        const val COLLECTION = "respuestas"
    }
}
```

### Repositories (Capa de Datos)

#### QuizRepository.kt

\\\kotlin
package com.rar.academiquiz1.repositories

import com.rar.academiquiz1.models.Quiz
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class QuizRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection(Quiz.COLLECTION)

    /**
     * Crea un nuevo quiz en Firestore.
     * @param quiz El objeto Quiz a guardar.
     * @return Result con el ID del documento generado.
     */
    suspend fun crearQuiz(quiz: Quiz): Result<String> {
        return try {
            val docRef = collection.add(quiz).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene un quiz por su ID.
     * @param id ID del quiz.
     * @return Result con el objeto Quiz (o null si no existe).
     */
    suspend fun obtenerQuiz(id: String): Result<Quiz?> {
        return try {
            val snapshot = collection.document(id).get().await()
            val quiz = snapshot.toObject(Quiz::class.java)
            quiz?.id_quiz = snapshot.id
            Result.success(quiz)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza un quiz existente.
     * @param quiz Objeto Quiz con los datos actualizados.
     * @return Result de Unit.
     */
    suspend fun actualizarQuiz(quiz: Quiz): Result<Unit> {
        return try {
            collection.document(quiz.id_quiz!!).set(quiz).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un quiz y todos sus datos relacionados (preguntas, resultados).
     * @param id ID del quiz a eliminar.
     * @return Result de Unit.
     */
    suspend fun eliminarQuiz(id: String): Result<Unit> {
        return try {
            // Eliminar preguntas asociadas al quiz
            val preguntaRepo = PreguntaRepository()
            preguntaRepo.eliminarPreguntasPorQuiz(id)
            
            // Eliminar resultados asociados al quiz
            val resultadoRepo = ResultadoRepository()
            resultadoRepo.eliminarResultadosPorQuiz(id)
            
            // Eliminar el documento del quiz
            collection.document(id).delete().await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todos los quizzes ordenados por título.
     * @return Result con la lista de quizzes.
     */
    suspend fun obtenerTodosQuizzes(): Result<List<Quiz>> {
        return try {
            val snapshot = collection
                .orderBy("titulo", Query.Direction.ASCENDING)
                .get()
                .await()
            val quizzes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Quiz::class.java)?.apply {
                    id_quiz = doc.id
                }
            }
            Result.success(quizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene solo los quizzes marcados como activos.
     * @return Result con la lista de quizzes activos.
     */
    suspend fun obtenerQuizzesActivos(): Result<List<Quiz>> {
        return try {
            val snapshot = collection
                .whereEqualTo("activo", true)
                .get()
                .await()
            val quizzes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Quiz::class.java)?.apply {
                    id_quiz = doc.id
                }
            }
            Result.success(quizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los quizzes creados por un usuario específico (Maestro/Admin).
     * @param idCreador ID del usuario creador.
     * @return Result con la lista de quizzes.
     */
    suspend fun obtenerQuizzesPorCreador(idCreador: String): Result<List<Quiz>> {
        return try {
            val snapshot = collection
                .whereEqualTo("id_creador", idCreador)
                .get()
                .await()
            val quizzes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Quiz::class.java)?.apply {
                    id_quiz = doc.id
                }
            }
            Result.success(quizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Incrementa el contador de intentos totales de un quiz de forma atómica.
     * @param idQuiz ID del quiz.
     * @return Result de Unit.
     */
    suspend fun incrementarIntentos(idQuiz: String): Result<Unit> {
        return try {
            val quizDoc = collection.document(idQuiz)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(quizDoc)
                val intentos = snapshot.getLong("intentos_totales") ?: 0
                transaction.update(quizDoc, "intentos_totales", intentos + 1)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza el promedio general de calificaciones de un quiz.
     * @param idQuiz ID del quiz.
     * @param nuevoPromedio Nuevo valor del promedio.
     * @return Result de Unit.
     */
    suspend fun actualizarPromedio(idQuiz: String, nuevoPromedio: Double): Result<Unit> {
        return try {
            collection.document(idQuiz)
                .update("promedio_general", nuevoPromedio)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
\\\

#### UsuarioRepository.kt

\\\kotlin
package com.rar.academiquiz1.repositories

import com.rar.academiquiz1.models.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection(Usuario.COLLECTION)

    /**
     * Crea o actualiza un usuario en Firestore.
     * @param usuario El objeto Usuario a guardar.
     * @return Result de Unit.
     */
    suspend fun crearUsuario(usuario: Usuario): Result<Unit> {
        return try {
            if (usuario.id_usuario != null) {
                collection.document(usuario.id_usuario!!).set(usuario).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("El usuario debe tener un ID"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los datos de un usuario por su ID.
     * @param id ID del usuario.
     * @return Result con el objeto Usuario.
     */
    suspend fun obtenerUsuario(id: String): Result<Usuario?> {
        return try {
            val snapshot = collection.document(id).get().await()
            val usuario = snapshot.toObject(Usuario::class.java)
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     * @param usuario Objeto Usuario con nuevos datos.
     * @return Result de Unit.
     */
    suspend fun actualizarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            collection.document(usuario.id_usuario!!).set(usuario).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Actualiza la fecha de último acceso del usuario.
     * @param idUsuario ID del usuario.
     * @return Result de Unit.
     */
    suspend fun actualizarUltimoAcceso(idUsuario: String): Result<Unit> {
        return try {
            collection.document(idUsuario)
                .update("ultimo_acceso", Date())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un usuario y todos sus resultados asociados.
     * @param id ID del usuario a eliminar.
     * @return Result de Unit.
     */
    suspend fun eliminarUsuario(id: String): Result<Unit> {
        return try {
            // Eliminar resultados del usuario primero
            val resultadoRepo = ResultadoRepository()
            resultadoRepo.eliminarResultadosPorUsuario(id)
            
            // Eliminar el documento del usuario
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene todos los usuarios del sistema.
     * @return Result con la lista de usuarios.
     */
    suspend fun obtenerTodosUsuarios(): Result<List<Usuario>> {
        return try {
            val snapshot = collection.get().await()
            val usuarios = snapshot.toObjects(Usuario::class.java)
            Result.success(usuarios)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene usuarios filtrados por rol (ESTUDIANTE, MAESTRO, ADMIN).
     * @param rol Rol a filtrar.
     * @return Result con la lista de usuarios.
     */
    suspend fun obtenerUsuariosPorRol(rol: String): Result<List<Usuario>> {
        return try {
            val snapshot = collection.whereEqualTo("rol", rol).get().await()
            val usuarios = snapshot.toObjects(Usuario::class.java)
            Result.success(usuarios)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
\\\

#### PreguntaRepository.kt

\\\kotlin
package com.rar.academiquiz1.repositories

import com.rar.academiquiz1.models.Pregunta
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PreguntaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection(Pregunta.COLLECTION)

    /**
     * Crea una nueva pregunta en Firebase Firestore.
     * @param pregunta Objeto Pregunta a guardar.
     * @return Result con el ID de la pregunta creada.
     */
    suspend fun crearPregunta(pregunta: Pregunta): Result<String> {
        return try {
            val docRef = collection.add(pregunta).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todas las preguntas asociadas a un quiz específico.
     * @param idQuiz ID del quiz.
     * @return Result con la lista de preguntas.
     */
    suspend fun obtenerPreguntasPorQuiz(idQuiz: String): Result<List<Pregunta>> {
        return try {
            val snapshot = collection
                .whereEqualTo("id_quiz", idQuiz)
                .get()
                .await()
            val preguntas = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Pregunta::class.java)?.apply {
                    id_pregunta = doc.id
                }
            }
            Result.success(preguntas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina una pregunta específica por su ID.
     * @param id ID de la pregunta.
     * @return Result de Unit.
     */
    suspend fun eliminarPregunta(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Elimina todas las preguntas asociadas a un quiz.
     * Utiliza un batch para eliminar múltiples documentos eficientemente.
     * @param idQuiz ID del quiz.
     * @return Result de Unit.
     */
    suspend fun eliminarPreguntasPorQuiz(idQuiz: String): Result<Unit> {
        return try {
            val snapshot = collection.whereEqualTo("id_quiz", idQuiz).get().await()
            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Actualiza los datos de una pregunta existente.
     * @param pregunta Objeto Pregunta con los datos actualizados.
     * @return Result de Unit.
     */
    suspend fun actualizarPregunta(pregunta: Pregunta): Result<Unit> {
        return try {
            collection.document(pregunta.id_pregunta!!).set(pregunta).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
\\\


### Repositories (Capa de Datos)

#### QuizRepository.kt

\\\kotlin
package com.rar.academiquiz1.repositories

import com.rar.academiquiz1.models.Quiz
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class QuizRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection(Quiz.COLLECTION)

    suspend fun crearQuiz(quiz: Quiz): Result<String> {
        return try {
            val docRef = collection.add(quiz).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerQuiz(id: String): Result<Quiz?> {
        return try {
            val snapshot = collection.document(id).get().await()
            val quiz = snapshot.toObject(Quiz::class.java)
            quiz?.id_quiz = snapshot.id
            Result.success(quiz)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarQuiz(quiz: Quiz): Result<Unit> {
        return try {
            collection.document(quiz.id_quiz!!).set(quiz).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarQuiz(id: String): Result<Unit> {
        return try {
            val preguntaRepo = PreguntaRepository()
            preguntaRepo.eliminarPreguntasPorQuiz(id)
            
            val resultadoRepo = ResultadoRepository()
            resultadoRepo.eliminarResultadosPorQuiz(id)
            
            collection.document(id).delete().await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerTodosQuizzes(): Result<List<Quiz>> {
        return try {
            val snapshot = collection
                .orderBy("titulo", Query.Direction.ASCENDING)
                .get()
                .await()
            val quizzes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Quiz::class.java)?.apply {
                    id_quiz = doc.id
                }
            }
            Result.success(quizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerQuizzesActivos(): Result<List<Quiz>> {
        return try {
            val snapshot = collection
                .whereEqualTo("activo", true)
                .get()
                .await()
            val quizzes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Quiz::class.java)?.apply {
                    id_quiz = doc.id
                }
            }
            Result.success(quizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerQuizzesPorCreador(idCreador: String): Result<List<Quiz>> {
        return try {
            val snapshot = collection
                .whereEqualTo("id_creador", idCreador)
                .get()
                .await()
            val quizzes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Quiz::class.java)?.apply {
                    id_quiz = doc.id
                }
            }
            Result.success(quizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun incrementarIntentos(idQuiz: String): Result<Unit> {
        return try {
            val quizDoc = collection.document(idQuiz)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(quizDoc)
                val intentos = snapshot.getLong("intentos_totales") ?: 0
                transaction.update(quizDoc, "intentos_totales", intentos + 1)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarPromedio(idQuiz: String, nuevoPromedio: Double): Result<Unit> {
        return try {
            collection.document(idQuiz)
                .update("promedio_general", nuevoPromedio)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
\\\

#### UsuarioRepository.kt

\\\kotlin
package com.rar.academiquiz1.repositories

import com.rar.academiquiz1.models.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection(Usuario.COLLECTION)

    suspend fun crearUsuario(usuario: Usuario): Result<Unit> {
        return try {
            if (usuario.id_usuario != null) {
                collection.document(usuario.id_usuario!!).set(usuario).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("El usuario debe tener un ID"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerUsuario(id: String): Result<Usuario?> {
        return try {
            val snapshot = collection.document(id).get().await()
            val usuario = snapshot.toObject(Usuario::class.java)
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            collection.document(usuario.id_usuario!!).set(usuario).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun actualizarUltimoAcceso(idUsuario: String): Result<Unit> {
        return try {
            collection.document(idUsuario)
                .update("ultimo_acceso", Date())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarUsuario(id: String): Result<Unit> {
        return try {
            val resultadoRepo = ResultadoRepository()
            resultadoRepo.eliminarResultadosPorUsuario(id)
            
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun obtenerTodosUsuarios(): Result<List<Usuario>> {
        return try {
            val snapshot = collection.get().await()
            val usuarios = snapshot.toObjects(Usuario::class.java)
            Result.success(usuarios)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun obtenerUsuariosPorRol(rol: String): Result<List<Usuario>> {
        return try {
            val snapshot = collection.whereEqualTo("rol", rol).get().await()
            val usuarios = snapshot.toObjects(Usuario::class.java)
            Result.success(usuarios)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
\\\

#### PreguntaRepository.kt

\\\kotlin
package com.rar.academiquiz1.repositories

import com.rar.academiquiz1.models.Pregunta
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PreguntaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection(Pregunta.COLLECTION)

    suspend fun crearPregunta(pregunta: Pregunta): Result<String> {
        return try {
            val docRef = collection.add(pregunta).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerPreguntasPorQuiz(idQuiz: String): Result<List<Pregunta>> {
        return try {
            val snapshot = collection
                .whereEqualTo("id_quiz", idQuiz)
                .get()
                .await()
            val preguntas = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Pregunta::class.java)?.apply {
                    id_pregunta = doc.id
                }
            }
            Result.success(preguntas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarPregunta(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun eliminarPreguntasPorQuiz(idQuiz: String): Result<Unit> {
        return try {
            val snapshot = collection.whereEqualTo("id_quiz", idQuiz).get().await()
            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun actualizarPregunta(pregunta: Pregunta): Result<Unit> {
        return try {
            collection.document(pregunta.id_pregunta!!).set(pregunta).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
\\\


#### ResultadoRepository.kt

```kotlin
package com.rar.academiquiz1.repositories

import com.rar.academiquiz1.models.Respuesta
import com.rar.academiquiz1.models.Resultado
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ResultadoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val resultadosCollection = db.collection(Resultado.COLLECTION)
    private val respuestasCollection = db.collection(Respuesta.COLLECTION)

    /**
     * Guarda un resultado de quiz junto con todas sus respuestas detalladas.
     * Utiliza una transacción para asegurar la integridad de los datos.
     * @param resultado Objeto Resultado general.
     * @param respuestas Lista de objetos Respuesta individuales.
     * @return Result con el ID del resultado generado.
     */
    suspend fun crearResultado(resultado: Resultado, respuestas: List<Respuesta>): Result<String> {
        return try {
            val resultadoRef = resultadosCollection.document()
            resultado.id_resultado = resultadoRef.id
            
            db.runTransaction { transaction ->
                // Guardar el resultado principal
                transaction.set(resultadoRef, resultado)
                
                // Guardar cada respuesta individual
                for (respuesta in respuestas) {
                    val respuestaRef = respuestasCollection.document()
                    respuesta.id_resultado = resultado.id_resultado
                    transaction.set(respuestaRef, respuesta)
                }
            }.await()
            
            Result.success(resultado.id_resultado!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los resultados de un usuario específico, ordenados por fecha.
     * @param idUsuario ID del usuario.
     * @return Result con la lista de resultados.
     */
    suspend fun obtenerResultadosPorUsuario(idUsuario: String): Result<List<Resultado>> {
        return try {
            val snapshot = resultadosCollection
                .whereEqualTo("id_usuario", idUsuario)
                .orderBy("fecha_intento", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val resultados = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Resultado::class.java)?.apply {
                    id_resultado = doc.id
                }
            }
            Result.success(resultados)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los resultados de un quiz específico, ordenados por puntaje.
     * @param idQuiz ID del quiz.
     * @return Result con la lista de resultados.
     */
    suspend fun obtenerResultadosPorQuiz(idQuiz: String): Result<List<Resultado>> {
        return try {
            val snapshot = resultadosCollection
                .whereEqualTo("id_quiz", idQuiz)
                .orderBy("puntaje", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val resultados = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Resultado::class.java)?.apply {
                    id_resultado = doc.id
                }
            }
            Result.success(resultados)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene las respuestas detalladas asociadas a un resultado.
     * @param idResultado ID del resultado.
     * @return Result con la lista de respuestas.
     */
    suspend fun obtenerRespuestasPorResultado(idResultado: String): Result<List<Respuesta>> {
        return try {
            val snapshot = respuestasCollection
                .whereEqualTo("id_resultado", idResultado)
                .get()
                .await()
            
            val respuestas = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Respuesta::class.java)?.apply {
                    id_respuesta = doc.id
                }
            }
            Result.success(respuestas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un resultado y sus respuestas asociadas.
     * @param idResultado ID del resultado a eliminar.
     * @return Result de Unit.
     */
    suspend fun eliminarResultado(idResultado: String): Result<Unit> {
        return try {
            val respuestasSnapshot = respuestasCollection
                .whereEqualTo("id_resultado", idResultado)
                .get()
                .await()
            
            val batch = db.batch()
            for (doc in respuestasSnapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.delete(resultadosCollection.document(idResultado))
            
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina todos los resultados asociados a un quiz.
     * @param idQuiz ID del quiz.
     * @return Result de Unit.
     */
    suspend fun eliminarResultadosPorQuiz(idQuiz: String): Result<Unit> {
        return try {
            val resultadosSnapshot = resultadosCollection
                .whereEqualTo("id_quiz", idQuiz)
                .get()
                .await()
            
            val batch = db.batch()
            
            for (resultadoDoc in resultadosSnapshot.documents) {
                val idResultado = resultadoDoc.id
                
                // Eliminar respuestas asociadas
                val respuestasSnapshot = respuestasCollection
                    .whereEqualTo("id_resultado", idResultado)
                    .get()
                    .await()
                
                for (respuestaDoc in respuestasSnapshot.documents) {
                    batch.delete(respuestaDoc.reference)
                }
                
                // Eliminar resultado
                batch.delete(resultadoDoc.reference)
            }
            
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina todos los resultados asociados a un usuario.
     * @param idUsuario ID del usuario.
     * @return Result de Unit.
     */
    suspend fun eliminarResultadosPorUsuario(idUsuario: String): Result<Unit> {
        return try {
            val resultadosSnapshot = resultadosCollection
                .whereEqualTo("id_usuario", idUsuario)
                .get()
                .await()
            
            val batch = db.batch()
            
            for (resultadoDoc in resultadosSnapshot.documents) {
                val idResultado = resultadoDoc.id
                
                val respuestasSnapshot = respuestasCollection
                    .whereEqualTo("id_resultado", idResultado)
                    .get()
                    .await()
                
                for (respuestaDoc in respuestasSnapshot.documents) {
                    batch.delete(respuestaDoc.reference)
                }
                
                batch.delete(resultadoDoc.reference)
            }
            
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

#### GeminiRepository.kt

```kotlin
package com.rar.academiquiz1.repositories

import com.rar.academiquiz1.models.Opcion
import com.rar.academiquiz1.models.Pregunta
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class GeminiRepository(private val apiKey: String) {

    private val models = listOf("gemini-2.0-flash", "gemini-flash-latest", "gemini-pro-latest")

    /**
     * Genera un quiz automáticamente utilizando Google Gemini AI.
     * Intenta con varios modelos si el primero falla.
     * 
     * @param topic Tema del quiz.
     * @param numQuestions Número de preguntas a generar.
     * @param difficulty Nivel de dificultad (fácil, intermedio, difícil).
     * @return Result con la lista de preguntas generadas.
     */
    suspend fun generateQuiz(topic: String, numQuestions: Int, difficulty: String = "intermedio"): Result<List<Pregunta>> {
        var lastException: Exception? = null

        for (modelName in models) {
            try {
                // Prompt diseñado para obtener una respuesta JSON estructurada
                val prompt = """
                    Genera un examen de $numQuestions preguntas de opción múltiple sobre el tema "$topic" con dificultad $difficulty.
                    El formato de salida DEBE ser estrictamente un Array JSON válido.
                    Cada objeto del array debe tener:
                    - "enunciado": El texto de la pregunta.
                    - "opciones": Un array de objetos con "texto" (la respuesta) y "es_correcta" (booleano).
                    - "puntaje": Un valor numérico (ej. 10).
                    No incluyas markdown, ni bloques de código, solo el JSON puro.
                    Asegúrate de que haya exactamente una respuesta correcta por pregunta.
                """.trimIndent()

                val responseText = callGeminiApi(prompt, modelName)
                val cleanJson = cleanResponseText(responseText)
                val preguntas = parseJsonToPreguntas(cleanJson)
                
                if (preguntas.isNotEmpty()) {
                    return Result.success(preguntas)
                }
            } catch (e: Exception) {
                lastException = e
                android.util.Log.e("GeminiRepository", "Error con modelo $modelName: ${e.message}")
                continue
            }
        }
        
        return Result.failure(lastException ?: Exception("No se pudo generar el quiz con ningún modelo"))
    }

    /**
     * Realiza una llamada HTTP directa a la API de Gemini (en lugar de usar la SDK para menor dependencia).
     * @param prompt El texto del prompt.
     * @param model El nombre del modelo a usar.
     * @return El texto generado por la IA.
     */
    private suspend fun callGeminiApi(prompt: String, model: String): String {
        return kotlinx.coroutines.Dispatchers.IO.run {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                val url = URL("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Construcción manual del JSON request body
                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().put(JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().apply {
                            put("text", prompt)
                        }))
                    }))
                    put("safetySettings", JSONArray().put(JSONObject().apply {
                        put("category", "HARM_CATEGORY_DANGEROUS_CONTENT")
                        put("threshold", "BLOCK_ONLY_HIGH")
                    }))
                }

                try {
                    connection.outputStream.use { it.write(jsonBody.toString().toByteArray()) }

                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val reader = BufferedReader(InputStreamReader(connection.inputStream))
                        val response = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                        reader.close()
                        
                        val jsonResponse = JSONObject(response.toString())
                        val candidates = jsonResponse.getJSONArray("candidates")
                        if (candidates.length() > 0) {
                            val content = candidates.getJSONObject(0).getJSONObject("content")
                            val parts = content.getJSONArray("parts")
                            if (parts.length() > 0) {
                                return@withContext parts.getJSONObject(0).getString("text")
                            }
                        }
                        throw Exception("Respuesta vacía de Gemini")
                    } else {
                        val errorStream = connection.errorStream
                        val reader = BufferedReader(InputStreamReader(errorStream))
                        val errorResponse = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            errorResponse.append(line)
                        }
                        reader.close()
                        throw Exception("Error API ($responseCode): $errorResponse")
                    }
                } finally {
                    connection.disconnect()
                }
            }
        }
    }

    /**
     * Limpia la respuesta de texto para eliminar bloques de código markdown si existen.
     */
    private fun cleanResponseText(text: String): String {
        var cleaned = text.trim()
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7)
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3)
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length - 3)
        }
        
        val startIndex = cleaned.indexOf('[')
        val endIndex = cleaned.lastIndexOf(']')
        
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            cleaned = cleaned.substring(startIndex, endIndex + 1)
        }
        
        return cleaned.trim()
    }

    /**
     * Parsea el string JSON a una lista de objetos Pregunta.
     */
    private fun parseJsonToPreguntas(jsonString: String): List<Pregunta> {
        val listaPreguntas = mutableListOf<Pregunta>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val enunciado = obj.getString("enunciado")
                val puntaje = obj.optDouble("puntaje", 10.0)
                
                val opcionesJson = obj.getJSONArray("opciones")
                val opciones = mutableListOf<Opcion>()
                
                for (j in 0 until opcionesJson.length()) {
                    val opObj = opcionesJson.getJSONObject(j)
                    val texto = opObj.getString("texto")
                    val esCorrecta = opObj.getBoolean("es_correcta")
                    
                    opciones.add(Opcion(
                        id_opcion = UUID.randomUUID().toString(),
                        texto = texto,
                        es_correcta = esCorrecta
                    ))
                }
                
                // Asegurar que haya al menos una correcta
                if (opciones.none { it.es_correcta } && opciones.isNotEmpty()) {
                    opciones[0].es_correcta = true
                }
                
                listaPreguntas.add(Pregunta(
                    id_pregunta = null, 
                    id_quiz = null,
                    enunciado = enunciado,
                    tipo = Pregunta.TIPO_MULTIPLE,
                    puntaje = puntaje,
                    opciones = opciones
                ))
            }
        } catch (e: JSONException) {
            throw Exception("Error parseando JSON: ${e.message}")
        }
        return listaPreguntas
    }
}
```


### Activities (Actividades)

#### LoginActivity.kt

```kotlin
package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rar.academiquiz1.databinding.ActivityLoginBinding
import com.rar.academiquiz1.repositories.UsuarioRepository
import com.rar.academiquiz1.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        usuarioRepository = UsuarioRepository()
        sessionManager = SessionManager(this)

        // Verificar si ya hay sesión activa para redirigir automáticamente
        if (sessionManager.isLoggedIn() && auth.currentUser != null) {
            irAlDashboard()
            return
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Autentica al usuario con Firebase Auth.
     */
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        obtenerDatosUsuario(userId)
                    }
                } else {
                    Toast.makeText(this, "Error de autenticación: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Obtiene los datos adicionales del usuario desde Firestore y guarda la sesión.
     */
    private fun obtenerDatosUsuario(userId: String) {
        lifecycleScope.launch {
            val result = usuarioRepository.obtenerUsuario(userId)
            
            result.onSuccess { usuario ->
                if (usuario != null) {
                    sessionManager.saveUserSession(usuario)
                    usuarioRepository.actualizarUltimoAcceso(userId)
                    irAlDashboard()
                } else {
                    Toast.makeText(this@LoginActivity, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                Toast.makeText(this@LoginActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun irAlDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
```

#### RegisterActivity.kt

```kotlin
package com.rar.academiquiz1.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rar.academiquiz1.R
import com.rar.academiquiz1.databinding.ActivityRegisterBinding
import com.rar.academiquiz1.models.Usuario
import com.rar.academiquiz1.repositories.UsuarioRepository
import com.rar.academiquiz1.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var usuarioRepository: UsuarioRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        usuarioRepository = UsuarioRepository()

        setupSpinner()
        setupListeners()
    }

    private fun setupSpinner() {
        val roles = arrayOf("ESTUDIANTE", "MAESTRO")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        binding.spinnerRol.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val rol = binding.spinnerRol.selectedItem.toString()

            if (validarCampos(nombre, email, password, confirmPassword)) {
                registrarUsuario(nombre, email, password, rol)
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun validarCampos(nombre: String, email: String, pass: String, confirm: String): Boolean {
        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass.length < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass != confirm) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun registrarUsuario(nombre: String, email: String, pass: String, rol: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val nuevoUsuario = Usuario(nombre, email, rol).apply {
                        id_usuario = userId
                    }
                    
                    guardarUsuarioEnFirestore(nuevoUsuario)
                } else {
                    Toast.makeText(this, "Error en registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun guardarUsuarioEnFirestore(usuario: Usuario) {
        lifecycleScope.launch {
            val result = usuarioRepository.crearUsuario(usuario)
            
            result.onSuccess {
                Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure { e ->
                Toast.makeText(this@RegisterActivity, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

#### DashboardActivity.kt

```kotlin
package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rar.academiquiz1.databinding.ActivityDashboardBinding
import com.rar.academiquiz1.repositories.QuizRepository
import com.rar.academiquiz1.repositories.ResultadoRepository
import com.rar.academiquiz1.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var quizRepository: QuizRepository
    private lateinit var resultadoRepository: ResultadoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        quizRepository = QuizRepository()
        resultadoRepository = ResultadoRepository()

        setupUI()
        setupListeners()
        cargarEstadisticas()
    }

    /**
     * Configura la interfaz de usuario basándose en el rol del usuario (Estudiante, Maestro, Admin).
     */
    private fun setupUI() {
        val userName = sessionManager.getUserName()
        val userRole = sessionManager.getUserRol()

        binding.tvWelcome.text = "Hola, $userName"
        binding.tvRole.text = userRole

        // Configurar visibilidad de elementos según rol
        if (userRole == "ESTUDIANTE") {
            binding.fabCreateQuiz.visibility = View.GONE
            binding.cardManageUsers.visibility = View.GONE
        } else if (userRole == "MAESTRO") {
            binding.fabCreateQuiz.visibility = View.VISIBLE
            binding.cardManageUsers.visibility = View.GONE
        } else {
            // ADMIN tiene acceso total
            binding.fabCreateQuiz.visibility = View.VISIBLE
            binding.cardManageUsers.visibility = View.VISIBLE
        }
    }

    private fun setupListeners() {
        binding.cardQuizzes.setOnClickListener {
            startActivity(Intent(this, QuizListActivity::class.java))
        }

        binding.cardResults.setOnClickListener {
            startActivity(Intent(this, ResultsActivity::class.java))
        }

        binding.cardProfile.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        binding.cardManageUsers.setOnClickListener {
            startActivity(Intent(this, UsersManagementActivity::class.java))
        }

        binding.fabCreateQuiz.setOnClickListener {
            startActivity(Intent(this, CreateQuizActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            cerrarSesion()
        }
    }

    /**
     * Carga estadísticas relevantes para el dashboard.
     * Diferencia entre estudiantes (sus resultados) y maestros (sus quizzes).
     */
    private fun cargarEstadisticas() {
        val userId = sessionManager.getUserId() ?: return
        val userRole = sessionManager.getUserRol()

        lifecycleScope.launch {
            if (userRole == "ESTUDIANTE") {
                val resultados = resultadoRepository.obtenerResultadosPorUsuario(userId).getOrDefault(emptyList())
                binding.tvStat1Value.text = resultados.size.toString()
                binding.tvStat1Label.text = "Quizzes Completados"
                
                val promedio = if (resultados.isNotEmpty()) resultados.map { it.puntaje }.average() else 0.0
                binding.tvStat2Value.text = String.format("%.1f", promedio)
                binding.tvStat2Label.text = "Promedio General"
            } else {
                // Lógica para Maestro/Admin
                val quizzes = if (userRole == "ADMIN") {
                    quizRepository.obtenerTodosQuizzes().getOrDefault(emptyList())
                } else {
                    quizRepository.obtenerQuizzesPorCreador(userId).getOrDefault(emptyList())
                }
                
                binding.tvStat1Value.text = quizzes.size.toString()
                binding.tvStat1Label.text = "Quizzes Creados"
                
                val intentos = quizzes.sumOf { it.intentos_totales }
                binding.tvStat2Value.text = intentos.toString()
                binding.tvStat2Label.text = "Intentos Totales"
            }
        }
    }

    private fun cerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                sessionManager.clearSession()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
    
    override fun onResume() {
        super.onResume()
        // Recargar estadísticas al volver al dashboard
        cargarEstadisticas()
    }
}
```


#### CreateQuizActivity.kt

```kotlin
package com.rar.academiquiz1.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.R
import com.rar.academiquiz1.adapters.PreguntaAdapter
import com.rar.academiquiz1.databinding.ActivityCreateQuizBinding
import com.rar.academiquiz1.databinding.DialogAddQuestionBinding
import com.rar.academiquiz1.models.Opcion
import com.rar.academiquiz1.models.Pregunta
import com.rar.academiquiz1.models.Quiz
import com.rar.academiquiz1.repositories.GeminiRepository
import com.rar.academiquiz1.repositories.PreguntaRepository
import com.rar.academiquiz1.repositories.QuizRepository
import com.rar.academiquiz1.utils.Constants
import com.rar.academiquiz1.utils.SessionManager
import kotlinx.coroutines.launch
import java.util.UUID

class CreateQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateQuizBinding
    private lateinit var quizRepository: QuizRepository
    private lateinit var preguntaRepository: PreguntaRepository
    private lateinit var geminiRepository: GeminiRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var preguntaAdapter: PreguntaAdapter

    private var quizActual: Quiz = Quiz()
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizRepository = QuizRepository()
        preguntaRepository = PreguntaRepository()
        // Inicializar repositorio de IA con API Key
        // REEMPLAZAR CON TU API KEY REAL O USAR REMOTE CONFIG
        geminiRepository = GeminiRepository("AIzaSyBwMQK0VPmkyT7DeU92Om4Jl4s5qFUJR4w") 
        sessionManager = SessionManager(this)

        setupToolbar()
        setupRecyclerView()
        setupListeners()
        
        // Verificar si se recibió un quiz para edición
        if (intent.hasExtra(Constants.INTENT_QUIZ)) {
            val quiz = intent.getParcelableExtra<Quiz>(Constants.INTENT_QUIZ)
            if (quiz != null) {
                isEditMode = true
                quizActual = quiz
                cargarDatosQuiz()
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (isEditMode) "Editar Quiz" else "Crear Quiz"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        preguntaAdapter = PreguntaAdapter { pregunta, _ ->
            // Al hacer click en una pregunta (opcional: editar pregunta)
            mostrarDialogoEditarPregunta(pregunta)
        }
        binding.rvQuestions.layoutManager = LinearLayoutManager(this)
        binding.rvQuestions.adapter = preguntaAdapter
    }
   
   

    private fun setupListeners() {
        binding.btnAddQuestionManual.setOnClickListener {
            mostrarDialogoAgregarPregunta()
        }

        binding.btnGenerateAI.setOnClickListener {
            mostrarDialogoGenerarIA()
        }

        binding.btnSaveQuiz.setOnClickListener {
            guardarQuiz()
        }
    }

   
    /**
     * Carga los datos del quiz en la UI si estamos en modo edición.
     */
    private fun cargarDatosQuiz() {
        binding.etTitle.setText(quizActual.titulo)
        binding.etDuration.setText(quizActual.duracion_min.toString())
        binding.switchActive.isChecked = quizActual.isActivo
        
        // Cargar preguntas si no vienen en el objeto (lazy loading)
        if (quizActual.preguntas.isEmpty() && quizActual.id_quiz != null) {
            lifecycleScope.launch {
                val result = preguntaRepository.obtenerPreguntasPorQuiz(quizActual.id_quiz!!)
                result.onSuccess { preguntas ->
                    quizActual.preguntas = preguntas.toMutableList()
                    preguntaAdapter.submitList(quizActual.preguntas.toList())
                    actualizarContadorPreguntas()
                }
            }
        } else {
            preguntaAdapter.submitList(quizActual.preguntas.toList())
            actualizarContadorPreguntas()
        }
    }

    /**
     * Muestra un diálogo para agregar manualmente una nueva pregunta.
     */
    private fun mostrarDialogoAgregarPregunta() {
        val dialogBinding = DialogAddQuestionBinding.inflate(layoutInflater)
        
        AlertDialog.Builder(this)
            .setTitle("Agregar Pregunta")
            .setView(dialogBinding.root)
            .setPositiveButton("Agregar") { _, _ ->
                val enunciado = dialogBinding.etEnunciado.text.toString()
                val op1 = dialogBinding.etOption1.text.toString()
                val op2 = dialogBinding.etOption2.text.toString()
                val op3 = dialogBinding.etOption3.text.toString()
                val op4 = dialogBinding.etOption4.text.toString()
                
                if (enunciado.isNotEmpty() && op1.isNotEmpty() && op2.isNotEmpty()) {
                    val opciones = mutableListOf<Opcion>()
                    opciones.add(Opcion(UUID.randomUUID().toString(), op1, dialogBinding.radio1.isChecked))
                    opciones.add(Opcion(UUID.randomUUID().toString(), op2, dialogBinding.radio2.isChecked))
                    if (op3.isNotEmpty()) opciones.add(Opcion(UUID.randomUUID().toString(), op3, dialogBinding.radio3.isChecked))
                    if (op4.isNotEmpty()) opciones.add(Opcion(UUID.randomUUID().toString(), op4, dialogBinding.radio4.isChecked))
                    
                    val pregunta = Pregunta(
                        id_pregunta = null,
                        id_quiz = quizActual.id_quiz,
                        enunciado = enunciado,
                        tipo = Pregunta.TIPO_MULTIPLE,
                        puntaje = 10.0,
                        opciones = opciones
                    )
                    
                    quizActual.preguntas.add(pregunta)
                    preguntaAdapter.submitList(quizActual.preguntas.toList())
                    actualizarContadorPreguntas()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun mostrarDialogoEditarPregunta(pregunta: Pregunta) {
        // Implementación similar a agregar, pero pre-llenando datos
        // Por brevedad, omitido en este ejemplo completo
    }

    /**
     * Muestra el diálogo para configurar la generación de preguntas con IA.
     */
    private fun mostrarDialogoGenerarIA() {
        val view = layoutInflater.inflate(R.layout.dialog_generate_ai, null)
        val etTopic = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etTopic)
        val etCount = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etCount)
        val spinnerDiff = view.findViewById<android.widget.Spinner>(R.id.spinnerDifficulty)

        val difficulties = arrayOf("fácil", "intermedio", "difícil")
        spinnerDiff.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, difficulties)

        AlertDialog.Builder(this)
            .setTitle("Generar con IA")
            .setView(view)
            .setPositiveButton("Generar") { _, _ ->
                val topic = etTopic.text.toString()
                val countStr = etCount.text.toString()
                val difficulty = spinnerDiff.selectedItem.toString()

                if (topic.isNotEmpty() && countStr.isNotEmpty()) {
                    generarPreguntasIA(topic, countStr.toInt(), difficulty)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Llama al repositorio de Gemini para generar preguntas y las añade al quiz.
     */
    private fun generarPreguntasIA(topic: String, count: Int, difficulty: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.alpha = 0.5f
        
        lifecycleScope.launch {
            val result = geminiRepository.generateQuiz(topic, count, difficulty)
            
            binding.progressBar.visibility = View.GONE
            binding.contentLayout.alpha = 1.0f
            
            result.onSuccess { preguntas ->
                quizActual.preguntas.addAll(preguntas)
                preguntaAdapter.submitList(quizActual.preguntas.toList())
                actualizarContadorPreguntas()
                Toast.makeText(this@CreateQuizActivity, "${preguntas.size} preguntas generadas", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(this@CreateQuizActivity, "Error IA: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Guarda el quiz en Firestore, ya sea creándolo o actualizándolo.
     */
    private fun guardarQuiz() {
        val titulo = binding.etTitle.text.toString()
        val duracionStr = binding.etDuration.text.toString()
        
        if (titulo.isEmpty() || duracionStr.isEmpty()) {
            Toast.makeText(this, "Título y duración son requeridos", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (quizActual.preguntas.isEmpty()) {
            Toast.makeText(this, "Agrega al menos una pregunta", Toast.LENGTH_SHORT).show()
            return
        }

        quizActual.titulo = titulo
        quizActual.duracion_min = duracionStr.toInt()
        quizActual.isActivo = binding.switchActive.isChecked
        quizActual.num_preguntas = quizActual.preguntas.size
        
        if (!isEditMode) {
            quizActual.id_creador = sessionManager.getUserId()
        }

        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            if (isEditMode) {
                // Actualizar quiz existente
                quizRepository.actualizarQuiz(quizActual)
                
                // Guardar preguntas nuevas (sin ID)
                quizActual.preguntas.filter { it.id_pregunta == null }.forEach { pregunta ->
                    pregunta.id_quiz = quizActual.id_quiz
                    preguntaRepository.crearPregunta(pregunta)
                }
                
                Toast.makeText(this@CreateQuizActivity, "Quiz actualizado", Toast.LENGTH_SHORT).show()
            } else {
                // Crear nuevo quiz
                val result = quizRepository.crearQuiz(quizActual)
                result.onSuccess { quizId ->
                    // Guardar todas las preguntas asignándoles el nuevo ID del quiz
                    quizActual.preguntas.forEach { pregunta ->
                        pregunta.id_quiz = quizId
                        preguntaRepository.crearPregunta(pregunta)
                    }
                    Toast.makeText(this@CreateQuizActivity, "Quiz creado exitosamente", Toast.LENGTH_SHORT).show()
                }
            }
            
            binding.progressBar.visibility = View.GONE
            finish()
        }
    }

    private fun actualizarContadorPreguntas() {
        binding.tvQuestionCount.text = "Total Preguntas: ${quizActual.preguntas.size}"
    }
}
```

#### TakeQuizActivity.kt

```kotlin
package com.rar.academiquiz1.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.adapters.PreguntaAdapter
import com.rar.academiquiz1.databinding.ActivityTakeQuizBinding
import com.rar.academiquiz1.models.Opcion
import com.rar.academiquiz1.models.Pregunta
import com.rar.academiquiz1.models.Quiz
import com.rar.academiquiz1.models.Respuesta
import com.rar.academiquiz1.models.Resultado
import com.rar.academiquiz1.repositories.PreguntaRepository
import com.rar.academiquiz1.repositories.QuizRepository
import com.rar.academiquiz1.repositories.ResultadoRepository
import com.rar.academiquiz1.utils.Constants
import com.rar.academiquiz1.utils.SessionManager
import kotlinx.coroutines.launch
import java.util.Date

class TakeQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTakeQuizBinding
    private lateinit var quiz: Quiz
    private lateinit var preguntaRepository: PreguntaRepository
    private lateinit var resultadoRepository: ResultadoRepository
    private lateinit var quizRepository: QuizRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var preguntaAdapter: PreguntaAdapter
    
    private var preguntas = listOf<Pregunta>()
    private val respuestasUsuario = mutableMapOf<String, Opcion>() // Map<PreguntaID, Opcion>
    private var countDownTimer: CountDownTimer? = null
    private var tiempoRestanteMs: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakeQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar objeto Quiz del Intent
        quiz = intent.getParcelableExtra(Constants.INTENT_QUIZ) ?: run {
            finish()
            return
        }

        preguntaRepository = PreguntaRepository()
        resultadoRepository = ResultadoRepository()
        quizRepository = QuizRepository()
        sessionManager = SessionManager(this)

        setupToolbar()
        setupRecyclerView()
        cargarPreguntas()
        
        binding.btnSubmit.setOnClickListener {
            confirmarEnvio()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = quiz.titulo
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        preguntaAdapter = PreguntaAdapter { pregunta, opcion ->
            // Callback al seleccionar una opción
            // Guardar respuesta seleccionada temporalmente
            val key = pregunta.id_pregunta ?: pregunta.enunciado ?: ""
            respuestasUsuario[key] = opcion
            actualizarProgreso()
        }
        binding.rvQuestions.layoutManager = LinearLayoutManager(this)
        binding.rvQuestions.adapter = preguntaAdapter
    }

    /**
     * Carga las preguntas del quiz desde Firestore y luego inicia el temporizador.
     */
    private fun cargarPreguntas() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            val result = preguntaRepository.obtenerPreguntasPorQuiz(quiz.id_quiz!!)
            
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess { listaPreguntas ->
                preguntas = listaPreguntas
                preguntaAdapter.submitList(preguntas)
                iniciarTemporizador()
                actualizarProgreso()
            }.onFailure {
                Toast.makeText(this@TakeQuizActivity, "Error al cargar preguntas", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Inicia el temporizador de cuenta regresiva basado en la duración del quiz.
     */
    private fun iniciarTemporizador() {
        val duracionMs = quiz.duracion_min * 60 * 1000L
        
        countDownTimer = object : CountDownTimer(duracionMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestanteMs = millisUntilFinished
                val minutos = millisUntilFinished / 1000 / 60
                val segundos = (millisUntilFinished / 1000) % 60
                binding.tvTimer.text = String.format("%02d:%02d", minutos, segundos)
                
                // Cambiar color a rojo si queda menos de 1 minuto
                if (millisUntilFinished < 60000) {
                    binding.tvTimer.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            }

            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                Toast.makeText(this@TakeQuizActivity, "¡Tiempo terminado!", Toast.LENGTH_LONG).show()
                enviarRespuestas()
            }
        }.start()
    }

    private fun actualizarProgreso() {
        val respondidas = respuestasUsuario.size
        val total = preguntas.size
        binding.tvProgress.text = "Respondidas: $respondidas / $total"
        binding.linearProgress.max = total
        binding.linearProgress.progress = respondidas
    }

    private fun confirmarEnvio() {
        val noRespondidas = preguntas.size - respuestasUsuario.size
        val mensaje = if (noRespondidas > 0) {
            "Te faltan $noRespondidas preguntas por responder. ¿Estás seguro de enviar?"
        } else {
            "¿Estás seguro de enviar tus respuestas?"
        }

        AlertDialog.Builder(this)
            .setTitle("Enviar Quiz")
            .setMessage(mensaje)
            .setPositiveButton("Enviar") { _, _ ->
                enviarRespuestas()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Calcula el puntaje final, genera el objeto Resultado y lo guarda en Firestore.
     */
    private fun enviarRespuestas() {
        countDownTimer?.cancel()
        binding.btnSubmit.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        // Calcular puntaje
        var puntajeTotal = 0.0
        val respuestasParaGuardar = mutableListOf<Respuesta>()

        preguntas.forEach { pregunta ->
            val key = pregunta.id_pregunta ?: pregunta.enunciado ?: ""
            val opcionSeleccionada = respuestasUsuario[key]
            
            val esCorrecta = opcionSeleccionada?.es_correcta == true
            if (esCorrecta) {
                puntajeTotal += pregunta.puntaje
            }

            // Crear objeto Respuesta detallada
            if (opcionSeleccionada != null) {
                respuestasParaGuardar.add(Respuesta(
                    id_pregunta = pregunta.id_pregunta,
                    id_opcion = opcionSeleccionada.id_opcion,
                    correcta = esCorrecta
                ))
            }
        }

        // Normalizar puntaje a 0-100 para consistencia
        val puntajeMaximo = preguntas.sumOf { it.puntaje }
        val puntajeFinal = if (puntajeMaximo > 0) (puntajeTotal / puntajeMaximo) * 100 else 0.0

        val resultado = Resultado(
            id_usuario = sessionManager.getUserId(),
            id_quiz = quiz.id_quiz,
            puntaje = puntajeFinal,
            duracion_usada = quiz.duracion_min - (tiempoRestanteMs / 1000 / 60).toInt(),
            nombre_quiz = quiz.titulo,
            nombre_usuario = sessionManager.getUserName()
        )

        lifecycleScope.launch {
            // Guardar resultado y respuestas
            val result = resultadoRepository.crearResultado(resultado, respuestasParaGuardar)
            
            // Actualizar estadísticas globales del quiz
            quizRepository.incrementarIntentos(quiz.id_quiz!!)
            
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess {
                mostrarDialogoResultado(puntajeFinal)
            }.onFailure { e ->
                Toast.makeText(this@TakeQuizActivity, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun mostrarDialogoResultado(puntaje: Double) {
        val mensaje = if (puntaje >= 70) "¡Felicidades! Aprobaste." else "Sigue intentando."
        
        AlertDialog.Builder(this)
            .setTitle("Quiz Completado")
            .setMessage("Tu calificación: ${String.format("%.1f", puntaje)}%\n$mensaje")
            .setPositiveButton("Ver Resultados") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Salir del Quiz")
            .setMessage("Si sales ahora, perderás tu progreso. ¿Estás seguro?")
            .setPositiveButton("Salir") { _, _ ->
                countDownTimer?.cancel()
                super.onBackPressed()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
```


#### ResultsActivity.kt

```kotlin
package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.adapters.ResultAdapter
import com.rar.academiquiz1.databinding.ActivityResultsBinding
import com.rar.academiquiz1.repositories.ResultadoRepository
import com.rar.academiquiz1.utils.Constants
import com.rar.academiquiz1.utils.SessionManager
import kotlinx.coroutines.launch

class ResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding
    private lateinit var resultadoRepository: ResultadoRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var resultAdapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultadoRepository = ResultadoRepository()
        sessionManager = SessionManager(this)

        setupToolbar()
        setupRecyclerView()
        cargarResultados()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        resultAdapter = ResultAdapter { resultado ->
            val intent = Intent(this, ResultDetailsActivity::class.java)
            intent.putExtra(Constants.INTENT_RESULT_ID, resultado.id_resultado)
            intent.putExtra(Constants.INTENT_QUIZ_ID, resultado.id_quiz)
            startActivity(intent)
        }
        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.rvResults.adapter = resultAdapter
    }

    /**
     * Carga el historial de resultados del usuario.
     */
    private fun cargarResultados() {
        val userId = sessionManager.getUserId() ?: return
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = resultadoRepository.obtenerResultadosPorUsuario(userId)
            
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess { resultados ->
                if (resultados.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvResults.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvResults.visibility = View.VISIBLE
                    resultAdapter.submitList(resultados)
                    
                    // Calcular promedio simple para mostrar al usuario
                    val promedio = resultados.map { it.puntaje }.average()
                    binding.tvAverage.text = "Promedio General: ${String.format("%.1f%%", promedio)}"
                }
            }.onFailure { e ->
                Toast.makeText(this@ResultsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

#### ResultDetailsActivity.kt

```kotlin
package com.rar.academiquiz1.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.adapters.ResultDetailAdapter
import com.rar.academiquiz1.databinding.ActivityResultDetailsBinding
import com.rar.academiquiz1.models.Pregunta
import com.rar.academiquiz1.models.Respuesta
import com.rar.academiquiz1.repositories.PreguntaRepository
import com.rar.academiquiz1.repositories.ResultadoRepository
import com.rar.academiquiz1.utils.Constants
import kotlinx.coroutines.launch

class ResultDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultDetailsBinding
    private lateinit var preguntaRepository: PreguntaRepository
    private lateinit var resultadoRepository: ResultadoRepository
    private lateinit var adapter: ResultDetailAdapter

    private var resultId: String? = null
    private var quizId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resultId = intent.getStringExtra(Constants.INTENT_RESULT_ID)
        quizId = intent.getStringExtra(Constants.INTENT_QUIZ_ID)

        if (resultId == null || quizId == null) {
            finish()
            return
        }

        preguntaRepository = PreguntaRepository()
        resultadoRepository = ResultadoRepository()

        setupToolbar()
        setupRecyclerView()
        cargarDetalles()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = ResultDetailAdapter()
        binding.rvDetails.layoutManager = LinearLayoutManager(this)
        binding.rvDetails.adapter = adapter
    }

    /**
     * Carga las preguntas del quiz y las respuestas del usuario para mostrarlas combinadas.
     */
    private fun cargarDetalles() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            // Cargar preguntas y respuestas en paralelo
            val preguntasResult = preguntaRepository.obtenerPreguntasPorQuiz(quizId!!)
            val respuestasResult = resultadoRepository.obtenerRespuestasPorResultado(resultId!!)

            binding.progressBar.visibility = View.GONE

            if (preguntasResult.isSuccess && respuestasResult.isSuccess) {
                val preguntas = preguntasResult.getOrDefault(emptyList())
                val respuestas = respuestasResult.getOrDefault(emptyList())
                
                combinarDatos(preguntas, respuestas)
            } else {
                Toast.makeText(this@ResultDetailsActivity, "Error al cargar detalles", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Combina la lista de preguntas con las respuestas dadas para la visualización.
     */
    private fun combinarDatos(preguntas: List<Pregunta>, respuestas: List<Respuesta>) {
        val detailItems = preguntas.map { pregunta ->
            val respuesta = respuestas.find { it.id_pregunta == pregunta.id_pregunta }
            
            // Encontrar texto de opción seleccionada
            val opcionSeleccionada = pregunta.opciones.find { it.id_opcion == respuesta?.id_opcion }?.texto
            
            // Encontrar texto de opción correcta
            val opcionCorrecta = pregunta.getOpcionCorrecta()?.texto
            
            ResultDetailAdapter.DetailItem(
                pregunta = pregunta,
                respuesta = respuesta,
                opcionSeleccionada = opcionSeleccionada,
                opcionCorrecta = opcionCorrecta
            )
        }
        
        adapter.submitList(detailItems)
    }
}
```

#### UsersManagementActivity.kt

```kotlin
package com.rar.academiquiz1.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.R
import com.rar.academiquiz1.adapters.UsuarioAdapter
import com.rar.academiquiz1.databinding.ActivityUsersManagementBinding
import com.rar.academiquiz1.databinding.DialogAddUserBinding
import com.rar.academiquiz1.models.Usuario
import com.rar.academiquiz1.repositories.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class UsersManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersManagementBinding
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var usuarioAdapter: UsuarioAdapter
    
    // Listas para manejar el filtrado local
    private var allUsuarios = listOf<Usuario>()
    private var filteredUsuarios = listOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioRepository = UsuarioRepository()

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupListeners()
        cargarUsuarios()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        usuarioAdapter = UsuarioAdapter(
            onEditClick = { usuario -> mostrarDialogoEditarUsuario(usuario) },
            onDeleteClick = { usuario -> confirmarEliminacion(usuario) }
        )
        binding.rvUsuarios.layoutManager = LinearLayoutManager(this)
        binding.rvUsuarios.adapter = usuarioAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarUsuarios(newText ?: "")
                return true
            }
        })
    }

    private fun setupListeners() {
        binding.fabAddUser.setOnClickListener {
            mostrarDialogoAgregarUsuario()
        }
    }

    /**
     * Carga todos los usuarios desde Firestore.
     */
    private fun cargarUsuarios() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = usuarioRepository.obtenerTodosUsuarios()
            binding.progressBar.visibility = View.GONE
            
            result.onSuccess { usuarios ->
                allUsuarios = usuarios
                filteredUsuarios = usuarios
                actualizarUI()
            }.onFailure { e ->
                Toast.makeText(this@UsersManagementActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Filtra la lista de usuarios localmente por nombre o email.
     */
    private fun filtrarUsuarios(query: String) {
        filteredUsuarios = if (query.isEmpty()) {
            allUsuarios
        } else {
            allUsuarios.filter { 
                it.nombre?.contains(query, ignoreCase = true) == true || 
                it.email?.contains(query, ignoreCase = true) == true 
            }
        }
        actualizarUI()
    }

    private fun actualizarUI() {
        usuarioAdapter.submitList(filteredUsuarios)
        binding.tvTotalUsers.text = "Total: ${filteredUsuarios.size} usuarios"
        
        if (filteredUsuarios.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvUsuarios.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvUsuarios.visibility = View.VISIBLE
        }
    }

    private fun mostrarDialogoAgregarUsuario() {
        val dialogBinding = DialogAddUserBinding.inflate(layoutInflater)
        val roles = arrayOf("ESTUDIANTE", "MAESTRO", "ADMIN")
        dialogBinding.spinnerRol.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

        AlertDialog.Builder(this)
            .setTitle("Agregar Usuario")
            .setView(dialogBinding.root)
            .setPositiveButton("Crear") { _, _ ->
                val nombre = dialogBinding.etNombre.text.toString()
                val email = dialogBinding.etEmail.text.toString()
                val password = dialogBinding.etPassword.text.toString()
                val rol = dialogBinding.spinnerRol.selectedItem.toString()

                if (nombre.isNotEmpty() && email.isNotEmpty() && password.length >= 6) {
                    crearUsuarioEnFirebase(nombre, email, password, rol)
                } else {
                    Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Crea un usuario en Firestore. 
     * Nota: La creación en Auth idealmente debería ser desde backend para no cerrar sesión actual.
     */
    private fun crearUsuarioEnFirebase(nombre: String, email: String, pass: String, rol: String) {
        // En una app real, esto debería hacerse desde un Cloud Function (Backend)
        // Aquí simulamos el proceso o advertimos al admin
        
        Toast.makeText(this, "Funcionalidad limitada en cliente: Se requiere Backend para crear usuarios sin cerrar sesión", Toast.LENGTH_LONG).show()
        
        // Implementación demostrativa que guarda solo en Firestore
        // En producción usar Firebase Admin SDK
        val nuevoUsuario = Usuario(nombre, email, rol).apply {
            id_usuario = java.util.UUID.randomUUID().toString() // ID temporal simulado
        }
        
        lifecycleScope.launch {
            usuarioRepository.crearUsuario(nuevoUsuario)
            cargarUsuarios()
        }
    }

    private fun mostrarDialogoEditarUsuario(usuario: Usuario) {
        val dialogBinding = DialogAddUserBinding.inflate(layoutInflater)
        dialogBinding.etNombre.setText(usuario.nombre)
        dialogBinding.etEmail.setText(usuario.email)
        dialogBinding.etEmail.isEnabled = false // No editar email
        dialogBinding.etPassword.visibility = View.GONE // No editar password aquí
        dialogBinding.tilPassword.visibility = View.GONE
        
        val roles = arrayOf("ESTUDIANTE", "MAESTRO", "ADMIN")
        dialogBinding.spinnerRol.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        val roleIndex = roles.indexOf(usuario.rol)
        if (roleIndex >= 0) dialogBinding.spinnerRol.setSelection(roleIndex)

        AlertDialog.Builder(this)
            .setTitle("Editar Usuario")
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = dialogBinding.etNombre.text.toString()
                val nuevoRol = dialogBinding.spinnerRol.selectedItem.toString()
                
                if (nuevoNombre.isNotEmpty()) {
                    usuario.nombre = nuevoNombre
                    usuario.rol = nuevoRol
                    actualizarUsuario(usuario)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarUsuario(usuario: Usuario) {
        lifecycleScope.launch {
            usuarioRepository.actualizarUsuario(usuario)
            cargarUsuarios()
            Toast.makeText(this@UsersManagementActivity, "Usuario actualizado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmarEliminacion(usuario: Usuario) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de eliminar a ${usuario.nombre}? Esta acción borrará todos sus resultados.")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    usuarioRepository.eliminarUsuario(usuario.id_usuario!!)
                    cargarUsuarios()
                    Toast.makeText(this@UsersManagementActivity, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
```


#### QuizListActivity.kt

```kotlin
package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rar.academiquiz1.adapters.QuizAdapter
import com.rar.academiquiz1.databinding.ActivityQuizListBinding
import com.rar.academiquiz1.models.Quiz
import com.rar.academiquiz1.repositories.QuizRepository
import com.rar.academiquiz1.utils.Constants
import com.rar.academiquiz1.utils.SessionManager
import kotlinx.coroutines.launch

class QuizListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizListBinding
    private lateinit var quizRepository: QuizRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var quizAdapter: QuizAdapter
    
    private var allQuizzes = listOf<Quiz>()
    private var filteredQuizzes = listOf<Quiz>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizRepository = QuizRepository()
        sessionManager = SessionManager(this)

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        cargarQuizzes()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        val userRole = sessionManager.getUserRol()
        val isTeacher = userRole != "ESTUDIANTE"

        quizAdapter = QuizAdapter(
            onQuizClick = { quiz ->
                if (userRole == "ESTUDIANTE") {
                    // Estudiante: Tomar el quiz
                    val intent = Intent(this, TakeQuizActivity::class.java)
                    intent.putExtra(Constants.INTENT_QUIZ, quiz)
                    startActivity(intent)
                } else {
                    // Profesor/Admin: Ver detalles o editar
                    val intent = Intent(this, CreateQuizActivity::class.java)
                    intent.putExtra(Constants.INTENT_QUIZ, quiz)
                    startActivity(intent)
                }
            },
            onEditClick = if (isTeacher) {
                { quiz ->
                    val intent = Intent(this, CreateQuizActivity::class.java)
                    intent.putExtra(Constants.INTENT_QUIZ, quiz)
                    startActivity(intent)
                }
            } else null,
            onDeleteClick = if (isTeacher) {
                { quiz ->
                    eliminarQuiz(quiz)
                }
            } else null,
            isTeacher = isTeacher
        )

        binding.rvQuizzes.apply {
            layoutManager = LinearLayoutManager(this@QuizListActivity)
            adapter = quizAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarQuizzes(newText ?: "")
                return true
            }
        })
    }

    /**
     * Carga la lista de quizzes disponibles basándose en el rol del usuario.
     */
    private fun cargarQuizzes() {
        mostrarCargando(true)
        
        lifecycleScope.launch {
            val userRole = sessionManager.getUserRol()
            
            val result = when (userRole) {
                "ESTUDIANTE" -> quizRepository.obtenerQuizzesActivos()
                "ADMIN" -> quizRepository.obtenerTodosQuizzes()
                else -> { // MAESTRO
                    val userId = sessionManager.getUserId()
                    if (userId != null) {
                        quizRepository.obtenerQuizzesPorCreador(userId)
                    } else {
                        Result.failure(Exception("Usuario no encontrado"))
                    }
                }
            }

            mostrarCargando(false)

            result.onSuccess { quizzes ->
                allQuizzes = quizzes
                filteredQuizzes = quizzes
                actualizarUI()
            }.onFailure { e ->
                Toast.makeText(
                    this@QuizListActivity,
                    "Error al cargar quizzes: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun filtrarQuizzes(query: String) {
        filteredQuizzes = if (query.isEmpty()) {
            allQuizzes
        } else {
            allQuizzes.filter { quiz ->
                quiz.titulo?.contains(query, ignoreCase = true) == true
            }
        }
        actualizarUI()
    }

    private fun actualizarUI() {
        binding.tvTotalQuizzes.text = "${filteredQuizzes.size} quizzes disponibles"
        
        if (filteredQuizzes.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvQuizzes.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvQuizzes.visibility = View.VISIBLE
            quizAdapter.submitList(filteredQuizzes)
        }
    }

    private fun eliminarQuiz(quiz: Quiz) {
        // Validar que el quiz tenga un ID válido
        if (quiz.id_quiz.isNullOrEmpty()) {
            android.util.Log.e("QuizListActivity", "Intento de eliminar quiz con ID nulo o vacío")
            Toast.makeText(this, "Error: Quiz sin ID válido", Toast.LENGTH_SHORT).show()
            return
        }
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Eliminar Quiz")
            .setMessage("¿Estás seguro de eliminar '${quiz.titulo}'?")
            .setPositiveButton("Eliminar") { dialog, _ ->
                lifecycleScope.launch {
                    try {
                        val result = quizRepository.eliminarQuiz(quiz.id_quiz!!)
                        
                        result.onSuccess {
                            Toast.makeText(this@QuizListActivity, "Quiz eliminado", Toast.LENGTH_SHORT).show()
                            cargarQuizzes()
                        }.onFailure { e ->
                            Toast.makeText(
                                this@QuizListActivity, 
                                "Error al eliminar: ${e.message}", 
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@QuizListActivity, 
                            "Error inesperado: ${e.message}", 
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun mostrarCargando(mostrar: Boolean) {
        binding.progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        binding.rvQuizzes.visibility = if (mostrar) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        cargarQuizzes()
    }
}
```

#### SettingsActivity.kt

```kotlin
package com.rar.academiquiz1.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.rar.academiquiz1.R
import com.rar.academiquiz1.databinding.ActivitySettingsBinding
import com.rar.academiquiz1.repositories.ResultadoRepository
import com.rar.academiquiz1.repositories.UsuarioRepository
import com.rar.academiquiz1.utils.SessionManager
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var auth: FirebaseAuth
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var resultadoRepository: ResultadoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        auth = FirebaseAuth.getInstance()
        usuarioRepository = UsuarioRepository()
        resultadoRepository = ResultadoRepository()

        setupToolbar()
        setupUI()
        setupListeners()
        cargarEstadisticas()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * Configura la información visual del perfil y oculta estadísticas si el usuario no es estudiante.
     */
    private fun setupUI() {
        val userName = sessionManager.getUserName() ?: "Usuario"
        val userEmail = sessionManager.getUserEmail() ?: "email@example.com"
        val userRole = sessionManager.getUserRol() ?: "ESTUDIANTE"

        binding.tvUserName.text = userName
        binding.tvUserEmail.text = userEmail
        binding.tvUserRole.text = "Rol: ${getRoleDisplayName(userRole)}"

        // Mostrar estadísticas solo para estudiantes
        if (userRole != "ESTUDIANTE") {
            binding.cardStats.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            mostrarDialogoEditarPerfil()
        }

        binding.btnChangePassword.setOnClickListener {
            mostrarDialogoCambiarPassword()
        }

        binding.btnLogout.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cargarEstadisticas() {
        val userRole = sessionManager.getUserRol()
        if (userRole != "ESTUDIANTE") return

        val userId = sessionManager.getUserId() ?: return

        lifecycleScope.launch {
            val result = resultadoRepository.obtenerResultadosPorUsuario(userId)
            
            result.onSuccess { resultados ->
                val totalQuizzes = resultados.size
                val promedio = if (resultados.isNotEmpty()) {
                    resultados.map { it.puntaje }.average()
                } else 0.0

                binding.tvTotalQuizzes.text = totalQuizzes.toString()
                binding.tvAvgScore.text = String.format("%.1f%%", promedio)
            }
        }
    }

    private fun mostrarDialogoEditarPerfil() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.etNombre)
        
        etNombre.setText(sessionManager.getUserName())

        AlertDialog.Builder(this)
            .setTitle("Editar Perfil")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = etNombre.text.toString().trim()
                if (nuevoNombre.isNotEmpty()) {
                    actualizarPerfil(nuevoNombre)
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoCambiarPassword() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val etCurrentPassword = dialogView.findViewById<TextInputEditText>(R.id.etCurrentPassword)
        val etNewPassword = dialogView.findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<TextInputEditText>(R.id.etConfirmPassword)

        AlertDialog.Builder(this)
            .setTitle("Cambiar Contraseña")
            .setView(dialogView)
            .setPositiveButton("Cambiar") { _, _ ->
                val currentPassword = etCurrentPassword.text.toString()
                val newPassword = etNewPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

                if (validarCambioPassword(currentPassword, newPassword, confirmPassword)) {
                    cambiarPassword(currentPassword, newPassword)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun validarCambioPassword(current: String, new: String, confirm: String): Boolean {
        if (current.isEmpty() || new.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        if (new.length < 6) {
            Toast.makeText(this, "La nueva contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }

        if (new != confirm) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun actualizarPerfil(nuevoNombre: String) {
        val userId = sessionManager.getUserId() ?: return

        lifecycleScope.launch {
            val result = usuarioRepository.obtenerUsuario(userId)
            
            result.onSuccess { usuario ->
                if (usuario != null) {
                    usuario.nombre = nuevoNombre
                    
                    usuarioRepository.actualizarUsuario(usuario).onSuccess {
                        sessionManager.saveUser(usuario)
                        binding.tvUserName.text = nuevoNombre
                        Toast.makeText(this@SettingsActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    }.onFailure { e ->
                        Toast.makeText(this@SettingsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun cambiarPassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser
        val email = user?.email ?: return

        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                auth.signOut()
                sessionManager.clearSession()
                
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun getRoleDisplayName(role: String): String {
        return when (role) {
            "ESTUDIANTE" -> "Estudiante"
            "MAESTRO" -> "Profesor"
            "ADMIN" -> "Administrador"
            else -> role
        }
    }
}
```


### Adapters (Adaptadores)

#### QuizAdapter.kt

```kotlin
package com.rar.academiquiz1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rar.academiquiz1.databinding.ItemQuizBinding
import com.rar.academiquiz1.models.Quiz

class QuizAdapter(
    private val onQuizClick: (Quiz) -> Unit,
    private val onEditClick: ((Quiz) -> Unit)? = null,
    private val onDeleteClick: ((Quiz) -> Unit)? = null,
    private val isTeacher: Boolean = false
) : ListAdapter<Quiz, QuizAdapter.QuizViewHolder>(QuizDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val binding = ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class QuizViewHolder(private val binding: ItemQuizBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(quiz: Quiz) {
            binding.tvTitle.text = quiz.titulo
            binding.tvDuration.text = "${quiz.duracion_min} min"
            binding.tvQuestions.text = "${quiz.num_preguntas} preguntas"
            
            if (isTeacher) {
                binding.layoutTeacherStats.visibility = View.VISIBLE
                binding.tvAttempts.text = "Intentos: ${quiz.intentos_totales}"
                binding.tvAverage.text = "Promedio: ${quiz.getPromedioFormatted()}"
                
                binding.btnEdit.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE
                
                binding.btnEdit.setOnClickListener { onEditClick?.invoke(quiz) }
                binding.btnDelete.setOnClickListener { onDeleteClick?.invoke(quiz) }
            } else {
                binding.layoutTeacherStats.visibility = View.GONE
                binding.btnEdit.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                onQuizClick(quiz)
            }
        }
    }

    class QuizDiffCallback : DiffUtil.ItemCallback<Quiz>() {
        override fun areItemsTheSame(oldItem: Quiz, newItem: Quiz): Boolean {
            return oldItem.id_quiz == newItem.id_quiz
        }

        override fun areContentsTheSame(oldItem: Quiz, newItem: Quiz): Boolean {
            return oldItem == newItem
        }
    }
}
```

#### PreguntaAdapter.kt

```kotlin
package com.rar.academiquiz1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rar.academiquiz1.databinding.ItemQuestionBinding
import com.rar.academiquiz1.models.Opcion
import com.rar.academiquiz1.models.Pregunta

class PreguntaAdapter(
    private val onOptionSelected: ((Pregunta, Opcion) -> Unit)? = null
) : ListAdapter<Pregunta, PreguntaAdapter.PreguntaViewHolder>(PreguntaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreguntaViewHolder {
        val binding = ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PreguntaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PreguntaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PreguntaViewHolder(private val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pregunta: Pregunta) {
            binding.tvEnunciado.text = pregunta.enunciado
            binding.radioGroup.removeAllViews()
            binding.radioGroup.clearCheck()

            pregunta.opciones.forEach { opcion ->
                val radioButton = RadioButton(binding.root.context)
                radioButton.text = opcion.texto
                radioButton.id = View.generateViewId()
                
                radioButton.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        onOptionSelected?.invoke(pregunta, opcion)
                    }
                }
                
                binding.radioGroup.addView(radioButton)
            }
        }
    }

    class PreguntaDiffCallback : DiffUtil.ItemCallback<Pregunta>() {
        override fun areItemsTheSame(oldItem: Pregunta, newItem: Pregunta): Boolean {
            // Usar enunciado como fallback si id es nulo (preguntas nuevas)
            return (oldItem.id_pregunta ?: oldItem.enunciado) == (newItem.id_pregunta ?: newItem.enunciado)
        }

        override fun areContentsTheSame(oldItem: Pregunta, newItem: Pregunta): Boolean {
            return oldItem == newItem
        }
    }
}
```

#### ResultAdapter.kt

```kotlin
package com.rar.academiquiz1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rar.academiquiz1.databinding.ItemResultBinding
import com.rar.academiquiz1.models.Resultado
import java.text.SimpleDateFormat
import java.util.Locale

class ResultAdapter(
    private val onResultClick: (Resultado) -> Unit
) : ListAdapter<Resultado, ResultAdapter.ResultViewHolder>(ResultDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ResultViewHolder(
        private val binding: ItemResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        fun bind(resultado: Resultado) {
            binding.apply {
                tvQuizName.text = resultado.nombre_quiz
                tvScore.text = resultado.getPuntajeFormatted()
                tvDate.text = dateFormat.format(resultado.fecha_intento)
                tvDuration.text = "${resultado.duracion_usada} min"
                tvStatus.text = resultado.getEstado()

                // Color según calificación
                val color = when {
                    resultado.puntaje >= 90 -> com.rar.academiquiz1.R.color.success
                    resultado.puntaje >= 70 -> com.rar.academiquiz1.R.color.warning
                    else -> com.rar.academiquiz1.R.color.error
                }

                tvScore.setTextColor(root.context.getColor(color))
                tvStatus.setTextColor(root.context.getColor(color))

                root.setOnClickListener { onResultClick(resultado) }
            }
        }
    }

    class ResultDiffCallback : DiffUtil.ItemCallback<Resultado>() {
        override fun areItemsTheSame(oldItem: Resultado, newItem: Resultado): Boolean {
            return oldItem.id_resultado == newItem.id_resultado
        }

        override fun areContentsTheSame(oldItem: Resultado, newItem: Resultado): Boolean {
            return oldItem == newItem
        }
    }
}
```

#### ResultDetailAdapter.kt

```kotlin
package com.rar.academiquiz1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rar.academiquiz1.databinding.ItemResultDetailBinding
import com.rar.academiquiz1.models.Pregunta
import com.rar.academiquiz1.models.Respuesta

class ResultDetailAdapter(
    private var items: List<DetailItem> = emptyList()
) : RecyclerView.Adapter<ResultDetailAdapter.DetailViewHolder>() {

    data class DetailItem(
        val pregunta: Pregunta,
        val respuesta: Respuesta?,
        val opcionSeleccionada: String?,
        val opcionCorrecta: String?
    )

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

    inner class DetailViewHolder(private val binding: ItemResultDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

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
```

#### UsuarioAdapter.kt

```kotlin
package com.rar.academiquiz1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rar.academiquiz1.databinding.ItemUsuarioBinding
import com.rar.academiquiz1.models.Usuario

class UsuarioAdapter(
    private val onEditClick: (Usuario) -> Unit,
    private val onDeleteClick: (Usuario) -> Unit
) : ListAdapter<Usuario, UsuarioAdapter.UsuarioViewHolder>(UsuarioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val binding = ItemUsuarioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UsuarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UsuarioViewHolder(
        private val binding: ItemUsuarioBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(usuario: Usuario) {
            binding.apply {
                tvUserName.text = usuario.nombre
                tvUserEmail.text = usuario.email
                tvUserRole.text = getRoleDisplayName(usuario.rol ?: "ESTUDIANTE")

                // Color según rol
                val roleColor = when (usuario.rol) {
                    "ADMIN" -> com.rar.academiquiz1.R.color.error
                    "MAESTRO" -> com.rar.academiquiz1.R.color.accent
                    else -> com.rar.academiquiz1.R.color.primary
                }
                tvUserRole.setTextColor(root.context.getColor(roleColor))

                btnEdit.setOnClickListener {
                    onEditClick(usuario)
                }

                btnDelete.setOnClickListener {
                    onDeleteClick(usuario)
                }
            }
        }

        private fun getRoleDisplayName(role: String): String {
            return when (role) {
                "ESTUDIANTE" -> "Estudiante"
                "MAESTRO" -> "Profesor"
                "ADMIN" -> "Administrador"
                else -> role
            }
        }
    }

    class UsuarioDiffCallback : DiffUtil.ItemCallback<Usuario>() {
        override fun areItemsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem.id_usuario == newItem.id_usuario
        }

        override fun areContentsTheSame(oldItem: Usuario, newItem: Usuario): Boolean {
            return oldItem == newItem
        }
    }
}
```


### Utils (Utilidades)

#### SessionManager.kt

```kotlin
package com.rar.academiquiz1.utils

import android.content.Context
import android.content.SharedPreferences
import com.rar.academiquiz1.models.Usuario
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()
    private val gson = Gson()

    fun saveUserSession(usuario: Usuario) {
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true)
        editor.putString(Constants.KEY_USER_ID, usuario.id_usuario)
        editor.putString(Constants.KEY_USER_NAME, usuario.nombre)
        editor.putString(Constants.KEY_USER_EMAIL, usuario.email)
        editor.putString(Constants.KEY_USER_ROLE, usuario.rol)
        editor.apply()
    }
    
    fun saveUser(usuario: Usuario) {
        // Actualizar datos sin cambiar estado de login
        editor.putString(Constants.KEY_USER_NAME, usuario.nombre)
        editor.putString(Constants.KEY_USER_ROLE, usuario.rol)
        editor.apply()
    }

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

    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}
```

#### Constants.kt

```kotlin
package com.rar.academiquiz1.utils

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
```

### Configuración

#### build.gradle.kts (Module: app)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.rar.academiquiz1"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rar.academiquiz1"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    
    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

#### AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.VIBRATE" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.AcademiQuiz1"
        tools:targetApi="31">
        
        <activity
            android:name=".activities.LoginActivity"
            android:exported="true"
            android:theme="@style/Theme.AcademiQuiz1">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <activity android:name=".activities.RegisterActivity" />
        <activity android:name=".activities.DashboardActivity" />
        <activity android:name=".activities.CreateQuizActivity" />
        <activity android:name=".activities.TakeQuizActivity" />
        <activity android:name=".activities.ResultsActivity" />
        <activity android:name=".activities.ResultDetailsActivity" />
        <activity android:name=".activities.QuizListActivity" />
        <activity android:name=".activities.SettingsActivity" />
        <activity android:name=".activities.UsersManagementActivity" />
        
    </application>

</manifest>
```

---


### Main & UI Theme

#### MainActivity.kt

```kotlin
package com.rar.academiquiz1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rar.academiquiz1.ui.theme.AcademiQuiz1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcademiQuiz1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AcademiQuiz1Theme {
        Greeting("Android")
    }
}
```

#### Color.kt

```kotlin
package com.rar.academiquiz1.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
```

#### Theme.kt

```kotlin
package com.rar.academiquiz1.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun AcademiQuiz1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

#### Type.kt

```kotlin
package com.rar.academiquiz1.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
```
## Contribución

### Cómo Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Guías de Estilo

- **Kotlin**: Seguir [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- **Git Commits**: Usar [Conventional Commits](https://www.conventionalcommits.org/)
- **Documentación**: KDoc para todas las clases y funciones públicas

---

## Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

```
MIT License

Copyright (c) 2024 AcademiQuiz

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## Autores

- **Ramón Arechar** - *Desarrollo Inicial* - [RamonArechar7](https://github.com/RamonArechar7)

---

## Contacto

**Proyecto**: [https://github.com/RamonArechar7/AcademiQuiz](https://github.com/RamonArechar7/AcademiQuiz)

**Email**: [ramon.arechar@example.com](mailto:ramon.arechar@example.com)

---

<div align="center">

**Hecho con pasión para la educación**

Si este proyecto te fue útil, considera darle una estrella en GitHub

[Volver arriba](#academiquiz---sistema-de-cuestionarios-educativos)

</div>

