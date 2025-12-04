package com.rar.academiquiz1.repositories

import com.rar.academiquiz1.models.Resultado
import com.rar.academiquiz1.models.Respuesta
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ResultadoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collectionResultados = db.collection(Resultado.COLLECTION)
    private val collectionRespuestas = db.collection(Respuesta.COLLECTION)

    suspend fun crearResultado(resultado: Resultado, respuestas: List<Respuesta>): Result<String> {
        return try {
            val resultadoRef = collectionResultados.add(resultado).await()
            val idResultado = resultadoRef.id

            // Guardar respuestas en batch
            db.runBatch { batch ->
                respuestas.forEach { respuesta ->
                    respuesta.id_resultado = idResultado
                    val respuestaRef = collectionRespuestas.document()
                    batch.set(respuestaRef, respuesta)
                }
            }.await()

            Result.success(idResultado)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerResultado(id: String): Result<Resultado?> {
        return try {
            val snapshot = collectionResultados.document(id).get().await()
            val resultado = snapshot.toObject(Resultado::class.java)
            Result.success(resultado)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerResultadosPorUsuario(idUsuario: String): Result<List<Resultado>> {
        return try {
            val snapshot = collectionResultados
                .whereEqualTo("id_usuario", idUsuario)
                .get()
                .await()
            val resultados = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Resultado::class.java)?.apply {
                    id_resultado = doc.id
                }
            }.sortedByDescending { it.fecha_intento }
            Result.success(resultados)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerResultadosPorQuiz(idQuiz: String): Result<List<Resultado>> {
        return try {
            val snapshot = collectionResultados
                .whereEqualTo("id_quiz", idQuiz)
                .get()
                .await()
            val resultados = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Resultado::class.java)?.apply {
                    id_resultado = doc.id
                }
            }.sortedByDescending { it.fecha_intento }
            Result.success(resultados)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerRespuestasPorResultado(idResultado: String): Result<List<Respuesta>> {
        return try {
            val snapshot = collectionRespuestas
                .whereEqualTo("id_resultado", idResultado)
                .get()
                .await()
            val respuestas = snapshot.toObjects(Respuesta::class.java)
            Result.success(respuestas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarResultado(id: String): Result<Unit> {
        return try {
            // Eliminar respuestas asociadas
            val respuestas = collectionRespuestas
                .whereEqualTo("id_resultado", id)
                .get()
                .await()

            db.runBatch { batch ->
                respuestas.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
                batch.delete(collectionResultados.document(id))
            }.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun calcularPromedioQuiz(idQuiz: String): Result<Double> {
        return try {
            val snapshot = collectionResultados
                .whereEqualTo("id_quiz", idQuiz)
                .get()
                .await()

            val resultados = snapshot.toObjects(Resultado::class.java)
            val promedio = if (resultados.isNotEmpty()) {
                resultados.map { it.puntaje }.average()
            } else {
                0.0
            }

            Result.success(promedio)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarResultadosPorQuiz(idQuiz: String): Result<Unit> {
        return try {
            // Get all results for this quiz
            val resultados = collectionResultados
                .whereEqualTo("id_quiz", idQuiz)
                .get()
                .await()
            
            // For each result, delete its responses and the result itself
            resultados.documents.forEach { resultadoDoc ->
                // Get responses for this result
                val respuestas = collectionRespuestas
                    .whereEqualTo("id_resultado", resultadoDoc.id)
                    .get()
                    .await()
                
                // Delete in batch
                db.runBatch { batch ->
                    // Delete all responses
                    respuestas.documents.forEach { respuestaDoc ->
                        batch.delete(respuestaDoc.reference)
                    }
                    // Delete the result
                    batch.delete(resultadoDoc.reference)
                }.await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarResultadosPorUsuario(idUsuario: String): Result<Unit> {
        return try {
            // Get all results for this user
            val resultados = collectionResultados
                .whereEqualTo("id_usuario", idUsuario)
                .get()
                .await()
            
            // For each result, delete its responses and the result itself
            resultados.documents.forEach { resultadoDoc ->
                // Get responses for this result
                val respuestas = collectionRespuestas
                    .whereEqualTo("id_resultado", resultadoDoc.id)
                    .get()
                    .await()
                
                // Delete in batch
                db.runBatch { batch ->
                    // Delete all responses
                    respuestas.documents.forEach { respuestaDoc ->
                        batch.delete(respuestaDoc.reference)
                    }
                    // Delete the result
                    batch.delete(resultadoDoc.reference)
                }.await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}