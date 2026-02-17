package com.ramsalapps.flashcards

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStreamReader

class CSVParser(private val context: Context) {

    fun parseCSV(uri: Uri): Result<List<Flashcard>> {
        return try {
            val flashcards = mutableListOf<Flashcard>()
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.use { bufferedReader ->
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    line?.let {
                        val flashcard = parseCSVLine(it)
                        if (flashcard != null) {
                            flashcards.add(flashcard)
                        }
                    }
                }
            }

            if (flashcards.isEmpty()) {
                Result.failure(Exception("El archivo CSV no contiene tarjetas válidas"))
            } else {
                Result.success(flashcards)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun parseTestCSV(uri: Uri): Result<List<TestQuestion>> {
        return try {
            val questions = mutableListOf<TestQuestion>()
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.use { bufferedReader ->
                var line: String?
                // Skip header if needed, but assuming no header for simplicity or handle it
                while (bufferedReader.readLine().also { line = it } != null) {
                    line?.let {
                        val question = parseTestCSVLine(it)
                        if (question != null) {
                            questions.add(question)
                        }
                    }
                }
            }

            if (questions.isEmpty()) {
                Result.failure(Exception("El archivo CSV no contiene preguntas de test válidas"))
            } else {
                Result.success(questions)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseCSVLine(line: String): Flashcard? {
        // Parsear línea CSV en formato: "Pregunta", "Respuesta"
        return try {
            val parts = parseCSVFields(line)
            if (parts.size >= 2) {
                Flashcard(
                    question = parts[0].trim().removeSurrounding("\""),
                    answer = parts[1].trim().removeSurrounding("\""),
                    category = "Importado"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseTestCSVLine(line: String): TestQuestion? {
        // Formato esperado: "Pregunta", "Opción A", "Opción B", "Opción C", "Opción D", "Índice Correcta (0-3)", "Explicación"
        return try {
            val parts = parseCSVFields(line).map { it.trim().removeSurrounding("\"") }
            if (parts.size >= 6) {
                val question = parts[0]
                val options = parts.subList(1, 5)
                val correctIndex = parts[5].toIntOrNull() ?: 0
                val explanation = if (parts.size > 6) parts[6] else ""
                
                TestQuestion(
                    question = question,
                    options = options,
                    correctAnswerIndex = correctIndex,
                    explanation = explanation
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseCSVFields(line: String): List<String> {
        val fields = mutableListOf<String>()
        var currentField = StringBuilder()
        var insideQuotes = false
        var i = 0

        while (i < line.length) {
            val char = line[i]

            when {
                char == '"' -> {
                    insideQuotes = !insideQuotes
                }
                char == ',' && !insideQuotes -> {
                    fields.add(currentField.toString())
                    currentField = StringBuilder()
                }
                else -> {
                    currentField.append(char)
                }
            }
            i++
        }

        fields.add(currentField.toString())
        return fields
    }
}
