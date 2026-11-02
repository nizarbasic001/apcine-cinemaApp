package com.example.data.repository

import android.content.Context
import com.example.data.model.UserProfile
import com.example.data.remote.SupabaseApi
import com.example.data.remote.SupabaseClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AuthRepository(
    private val context: Context,
    private val api: SupabaseApi = SupabaseClient.api
) {
    private val sharedPrefs = context.getSharedPreferences("cinestream_auth", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    init {
        val savedToken = sharedPrefs.getString("access_token", null)
        val savedId = sharedPrefs.getString("user_id", null)
        val savedEmail = sharedPrefs.getString("user_email", null)
        val savedName = sharedPrefs.getString("user_name", null)

        if (!savedToken.isNullOrBlank() && !savedId.isNullOrBlank()) {
            _accessToken.value = savedToken
            _currentUser.value = UserProfile(
                id = savedId,
                email = savedEmail,
                displayName = savedName
            )
        }
    }

    suspend fun login(email: String, pass: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val response = api.loginWithPassword(
                mapOf(
                    "email" to email.trim(),
                    "password" to pass
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val token = body.accessToken ?: ""
                val userId = body.user?.id ?: ""
                val userEmail = body.user?.email ?: email
                val userName = body.user?.userMetadata?.get("display_name") ?: userEmail.substringBefore("@")

                val profile = UserProfile(
                    id = userId,
                    email = userEmail,
                    displayName = userName
                )

                _accessToken.value = token
                _currentUser.value = profile

                sharedPrefs.edit()
                    .putString("access_token", token)
                    .putString("user_id", userId)
                    .putString("user_email", userEmail)
                    .putString("user_name", userName)
                    .apply()

                Result.success(profile)
            } else {
                val err = response.errorBody()?.string() ?: "Login failed. Please check your credentials."
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, pass: String, displayName: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        try {
            val response = api.signUpWithPassword(
                mapOf(
                    "email" to email.trim(),
                    "password" to pass,
                    "data" to mapOf("display_name" to displayName.trim())
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val token = body.accessToken ?: ""
                val userId = body.user?.id ?: "guest-${System.currentTimeMillis()}"
                val userEmail = body.user?.email ?: email
                val name = displayName.ifBlank { userEmail.substringBefore("@") }

                val profile = UserProfile(
                    id = userId,
                    email = userEmail,
                    displayName = name
                )

                _accessToken.value = token
                _currentUser.value = profile

                sharedPrefs.edit()
                    .putString("access_token", token)
                    .putString("user_id", userId)
                    .putString("user_email", userEmail)
                    .putString("user_name", name)
                    .apply()

                Result.success(profile)
            } else {
                val err = response.errorBody()?.string() ?: "Registration failed."
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun logout() {
        _accessToken.value = null
        _currentUser.value = null
        sharedPrefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = _currentUser.value != null
}
