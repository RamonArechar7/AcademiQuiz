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

    suspend fun obtenerPregunta(id: String): Result<Pregunta?> {
        return try {
            val snapshot = collection.document(id).get().await()
            val pregunta = snapshot.toObject(Pregunta::class.java)?.apply {
                id_pregunta = snapshot.id
            }
            Result.success(pregunta)
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

    suspend fun eliminarPregunta(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
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

    suspend fun eliminarPreguntasPorQuiz(idQuiz: String): Result<Unit> {
        return try {
            val snapshot = collection.whereEqualTo("id_quiz", idQuiz).get().await()
            db.runBatch { batch ->
                snapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}