package com.example.finalproject.study.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.core.DataStore.TokenManager
import com.example.finalproject.core.network.api.ApiClient
import com.example.finalproject.core.network.api.auth.LoginApi
import com.example.finalproject.study.data.StudyRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StudyViewModelFactory(
    private val repo: StudyRepository = StudyRepository(),
    private val tokenManager: TokenManager? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val loginApi = if (tokenManager != null) {
            // Use authenticated API client
            ApiClient.create(tokenManager).loginApi
        } else {
            // Create a simple retrofit instance for LoginApi (without authentication)
            val retrofit = Retrofit.Builder()
                .baseUrl("https://studymate.beerpsi.cc/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(LoginApi::class.java)
        }
        
        return StudyViewModel(repo, loginApi) as T
    }
}
