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
            // Cascade delete: first delete all related entities
            
            // 1. Delete all preguntas for this quiz
            val preguntaRepo = PreguntaRepository()
            preguntaRepo.eliminarPreguntasPorQuiz(id)
            
            // 2. Delete all resultados and respuestas for this quiz
            val resultadoRepo = ResultadoRepository()
            resultadoRepo.eliminarResultadosPorQuiz(id)
            
            // 3. Finally, delete the quiz itself
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