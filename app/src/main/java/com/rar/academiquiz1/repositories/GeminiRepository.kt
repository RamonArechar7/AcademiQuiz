package com.rar.academiquiz1.repositories

import com.rar.academiquiz1.models.Opcion
import com.rar.academiquiz1.models.Pregunta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GeminiRepository(private val apiKey: String) {

    suspend fun generateQuiz(topic: String, numQuestions: Int, difficulty: String = "intermedio"): Result<List<Pregunta>> {
        val models = listOf("gemini-2.0-flash", "gemini-flash-latest", "gemini-pro-latest")
        var lastException: Exception? = null

        for (modelName in models) {
            try {
                android.util.Log.d("GeminiRepository", "Intentando generar quiz con modelo: $modelName (HTTP Directo)")
                
                // Descripción de dificultad para el prompt
                val difficultyDescription = when (difficulty.lowercase()) {
                    "fácil" -> "Las preguntas deben ser básicas, sobre conceptos fundamentales y definiciones simples. Nivel principiante."
                    "intermedio" -> "Las preguntas deben requerir comprensión y aplicación de conceptos. Nivel medio."
                    "difícil" -> "Las preguntas deben ser complejas, requiriendo análisis profundo, síntesis y evaluación. Nivel avanzado."
                    else -> "Las preguntas deben requerir comprensión y aplicación de conceptos. Nivel medio."
                }
                
                val prompt = """
                    Genera exactamente $numQuestions preguntas de opción múltiple sobre el tema: "$topic".
                    
                    NIVEL DE DIFICULTAD: $difficulty
                    $difficultyDescription
                    
                    IMPORTANTE: Responde ÚNICAMENTE con un array JSON válido, sin texto adicional, sin markdown, sin explicaciones.
                    
                    Formato requerido:
                    [
                      {
                        "enunciado": "¿Pregunta aquí?",
                        "opciones": [
                          {"texto": "Opción A", "es_correcta": false},
                          {"texto": "Opción B", "es_correcta": true},
                          {"texto": "Opción C", "es_correcta": false},
                          {"texto": "Opción D", "es_correcta": false}
                        ]
                      }
                    ]
                    
                    Reglas:
                    - Cada pregunta debe tener exactamente 4 opciones
                    - Solo UNA opción debe ser correcta (es_correcta: true)
                    - Las preguntas deben ser claras y educativas
                    - Ajusta la complejidad según el nivel de dificultad especificado
                    - Responde SOLO con el JSON, nada más
                """.trimIndent()

                val responseText = callGeminiApi(prompt, modelName)
                
                if (responseText.isBlank()) {
                    throw Exception("Respuesta vacía de Gemini API")
                }

                // Log para debugging
                android.util.Log.d("GeminiRepository", "Respuesta original ($modelName): $responseText")

                // Limpiar la respuesta
                val cleanJson = cleanResponseText(responseText)
                
                // Log del JSON limpio
                android.util.Log.d("GeminiRepository", "JSON limpio: $cleanJson")
                
                // Validar que tenemos un JSON válido
                if (!cleanJson.startsWith("[") || !cleanJson.endsWith("]")) {
                    throw Exception("Respuesta no es un JSON válido")
                }
                
                // Intentar parsear
                val preguntas = parseJsonToPreguntas(cleanJson)
                
                if (preguntas.isEmpty()) {
                    throw Exception("No se generaron preguntas válidas")
                }
                
                return Result.success(preguntas)

            } catch (e: Exception) {
                android.util.Log.w("GeminiRepository", "Fallo con modelo $modelName: ${e.message}")
                lastException = e
                // Continuar con el siguiente modelo
            }
        }

        android.util.Log.e("GeminiRepository", "Todos los modelos fallaron", lastException)
        
        // Intentar listar modelos disponibles para debugging
        logAvailableModels()
        
        return Result.failure(Exception("Error al generar Quiz (todos los modelos fallaron). Ver logs para modelos disponibles. Último error: ${lastException?.message}"))
    }

    private suspend fun logAvailableModels() {
        try {
            withContext(Dispatchers.IO) {
                val url = URL("https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                
                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    android.util.Log.d("GeminiRepository", "Modelos disponibles para esta API Key: $response")
                } else {
                    val error = connection.errorStream?.bufferedReader()?.use { it.readText() }
                    android.util.Log.e("GeminiRepository", "Error al listar modelos ($responseCode): $error")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("GeminiRepository", "Error al intentar listar modelos", e)
        }
    }

    private suspend fun callGeminiApi(prompt: String, model: String): String {
        return withContext(Dispatchers.IO) {
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            
            try {
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                
                // Construir JSON body
                val jsonBody = JSONObject()
                val contentsArray = JSONArray()
                val contentObject = JSONObject()
                val partsArray = JSONArray()
                val partObject = JSONObject()
                partObject.put("text", prompt)
                partsArray.put(partObject)
                contentObject.put("parts", partsArray)
                contentsArray.put(contentObject)
                jsonBody.put("contents", contentsArray)
                
                // Configuración de seguridad (opcional, pero recomendada)
                val safetySettings = JSONArray()
                val categories = listOf(
                    "HARM_CATEGORY_HARASSMENT",
                    "HARM_CATEGORY_HATE_SPEECH",
                    "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                    "HARM_CATEGORY_DANGEROUS_CONTENT"
                )
                
                for (category in categories) {
                    val setting = JSONObject()
                    setting.put("category", category)
                    setting.put("threshold", "BLOCK_ONLY_HIGH")
                    safetySettings.put(setting)
                }
                jsonBody.put("safetySettings", safetySettings)

                // Enviar request
                connection.outputStream.use { it.write(jsonBody.toString().toByteArray()) }
                
                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    
                    if (!jsonResponse.has("candidates")) {
                        return@withContext "" // Respuesta vacía o bloqueada
                    }
                    
                    val candidates = jsonResponse.getJSONArray("candidates")
                    if (candidates.length() == 0) return@withContext ""
                    
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    parts.getJSONObject(0).getString("text")
                } else {
                    val error = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Error desconocido"
                    throw Exception("Error API ($responseCode): $error")
                }
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun cleanResponseText(text: String): String {
        var cleaned = text.trim()
        
        // Remover markdown code blocks (incluyendo variantes)
        cleaned = cleaned.replace(Regex("```json\\s*"), "")
        cleaned = cleaned.replace(Regex("```\\s*"), "")
        cleaned = cleaned.replace(Regex("`"), "")
        
        // Remover posibles etiquetas o texto introductorio
        cleaned = cleaned.replace(Regex("^[^\\[]*"), "")
        
        // Remover texto después del último ]
        val endIndex = cleaned.lastIndexOf(']')
        if (endIndex > 0 && endIndex < cleaned.length - 1) {
            cleaned = cleaned.substring(0, endIndex + 1)
        }
        
        // Asegurar que empieza con [
        val startIndex = cleaned.indexOf('[')
        if (startIndex > 0) {
            cleaned = cleaned.substring(startIndex)
        }
        
        // Limpiar espacios en blanco excesivos
        cleaned = cleaned.trim()
        
        return cleaned
    }

    private fun parseJsonToPreguntas(jsonString: String): List<Pregunta> {
        val listaPreguntas = mutableListOf<Pregunta>()
        
        try {
            val jsonArray = JSONArray(jsonString)
            
            if (jsonArray.length() == 0) {
                throw Exception("El array JSON está vacío")
            }

            for (i in 0 until jsonArray.length()) {
                try {
                    val objPregunta = jsonArray.getJSONObject(i)
                    
                    // Validar que existan los campos requeridos
                    if (!objPregunta.has("enunciado")) {
                        android.util.Log.w("GeminiRepository", "Pregunta $i no tiene enunciado, saltando...")
                        continue
                    }
                    
                    if (!objPregunta.has("opciones")) {
                        android.util.Log.w("GeminiRepository", "Pregunta $i no tiene opciones, saltando...")
                        continue
                    }
                    
                    val enunciado = objPregunta.getString("enunciado")
                    val jsonOpciones = objPregunta.getJSONArray("opciones")
                    
                    if (jsonOpciones.length() < 2) {
                        android.util.Log.w("GeminiRepository", "Pregunta $i tiene menos de 2 opciones, saltando...")
                        continue
                    }
                    
                    val opciones = mutableListOf<Opcion>()
                    for (j in 0 until jsonOpciones.length()) {
                        try {
                            val objOpcion = jsonOpciones.getJSONObject(j)
                            opciones.add(
                                Opcion(
                                    id_opcion = java.util.UUID.randomUUID().toString(),
                                    texto = objOpcion.getString("texto"),
                                    es_correcta = objOpcion.optBoolean("es_correcta", false)
                                )
                            )
                        } catch (e: Exception) {
                            android.util.Log.w("GeminiRepository", "Error en opción $j de pregunta $i: ${e.message}")
                        }
                    }
                    
                    // Validar que haya al menos una opción correcta
                    if (opciones.none { it.es_correcta }) {
                        android.util.Log.w("GeminiRepository", "Pregunta $i no tiene respuesta correcta, marcando la primera")
                        opciones.firstOrNull()?.es_correcta = true
                    }
                    
                    if (opciones.isNotEmpty()) {
                        listaPreguntas.add(
                            Pregunta(
                                enunciado = enunciado,
                                tipo = Pregunta.TIPO_MULTIPLE,
                                puntaje = 1.0,
                                opciones = opciones
                            )
                        )
                    }
                } catch (e: Exception) {
                    android.util.Log.e("GeminiRepository", "Error procesando pregunta $i: ${e.message}")
                    // Continuar con la siguiente pregunta
                }
            }
        } catch (e: JSONException) {
            throw Exception("Error parseando JSON: ${e.message}. JSON recibido: ${jsonString.take(200)}...")
        }
        
        return listaPreguntas
    }
}
