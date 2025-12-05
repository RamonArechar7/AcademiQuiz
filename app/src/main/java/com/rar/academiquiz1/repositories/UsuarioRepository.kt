package com.rar.academiquiz1.repositories

import com.rar.academiquiz1.models.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection(Usuario.COLLECTION)

    suspend fun crearUsuario(usuario: Usuario): Result<String> {
        return try {
            val docRef = collection.document(usuario.id_usuario!!)
            docRef.set(usuario).await()
            Result.success(docRef.id)
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

    suspend fun actualizarUltimoAcceso(id: String): Result<Unit> {
        return try {
            collection.document(id)
                .update("ultimo_acceso", Date())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarUsuario(id: String): Result<Unit> {
        return try {
            // Cascade delete: first delete all related entities
            
            // 1. Delete all resultados and respuestas for this user
            val resultadoRepo = ResultadoRepository()
            resultadoRepo.eliminarResultadosPorUsuario(id)
            
            // 2. Finally, delete the user itself
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