package com.example.finalproject.auth.register.data
// com.example.finalproject.auth.register.data/RegisterRepository.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalproject.auth.register.model.RegistrationData
import com.example.finalproject.auth.register.viewmodel.RegisterViewModel
import retrofit2.Retrofit
import kotlin.runCatching

interface RegisterApi {
    // backend endpoint example
    // @POST("auth/register")
    // suspend fun register(@Body body: RegisterRequest): RegisterResponse
}

class RegisterRepository(private val api: RegisterApi) {
    suspend fun register(data: RegistrationData): Result<Unit> {
        return runCatching {
            // map RegistrationData -> RegisterRequest and call api.register(...)
            // For demo, pretend success:
            // val resp = api.register(...)
            // TODO: actual implementation
        }
    }
}


class RegisterViewModelFactory(
    private val repo: RegisterRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


// Fake API để test, không gọi mạng thật
object FakeRegisterApi : RegisterApi {
    // sau này có thể thêm hàm register(...)
}

