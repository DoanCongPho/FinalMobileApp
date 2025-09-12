package com.example.finalproject.createquiz.data

import android.content.ContentResolver
import android.net.Uri
import com.example.finalproject.createquiz.model.Difficulty
import com.example.finalproject.createquiz.model.QuizCreateResponse
import com.example.finalproject.createquiz.model.QuizSource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.InputStream
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.core.network.api.quiz.QuizApi
import com.example.finalproject.createquiz.viewmodel.CreateQuizViewModel


class CreateQuizViewModelFactory(
    private val repository: CreateQuizRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateQuizViewModel::class.java)) {
            return CreateQuizViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class CreateQuizRepository(
    private val api: QuizApi
) {
    suspend fun generateQuiz(
        resolver: ContentResolver,
        sources: List<QuizSource>,
        prompt: String,
        difficulty: Difficulty,
        numQuestions: Int
    ): QuizCreateResponse {
        val parts = sources.mapIndexed { idx, src ->
            val uri = Uri.parse(src.uriString)
            val tmp = resolver.openInputStream(uri)!!.use { inS ->
                streamToTemp(inS, src.displayName)
            }
            val body = tmp.asRequestBody(src.mime.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("files", src.displayName, body)
        }

        val promptRB = prompt.toRequestBody("text/plain".toMediaTypeOrNull())
        val diffRB   = difficulty.name.lowercase().toRequestBody("text/plain".toMediaTypeOrNull())
        val numRB    = numQuestions.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        return api.generateQuiz(parts, promptRB, diffRB, numRB)
    }

    private fun streamToTemp(input: InputStream, name: String): File {
        val f = File.createTempFile(name.substringBeforeLast('.'), name.substringAfterLast('.', "tmp"))
        f.outputStream().use { out -> input.copyTo(out) }
        return f
    }
}
