package pe.goliva.vet_mascotario.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class LocalAuthManager (context : Context) {

    private val prefs : SharedPreferences =
        context.getSharedPreferences("local_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_REGISTERED_NAME = "registered_name"
        private const val KEY_REGISTERED_EMAIL = "registered_email"
        private const val KEY_REGISTERED_PASSWORD = "registered_phone"
        private const val KEY_REGISTERED_PHONE = "registered_password"

        private const val DEMO_EMAIL = "demo@mascotario.com"
        private const val DEMO_PASSWORD = "123456"
    }

    fun registerUser (name: String, email: String, password: String, phone: String) {
        prefs.edit {
            putString(KEY_REGISTERED_NAME, name)
                .putString(KEY_REGISTERED_EMAIL, email)
                .putString(KEY_REGISTERED_PASSWORD, password)
                .putString(KEY_REGISTERED_PHONE, phone)
            .apply()
        }
    }

    fun authenticate(email: String, password: String): Boolean {
        val savedEmail = prefs.getString(KEY_REGISTERED_EMAIL, null)
        val savedPassword = prefs.getString(KEY_REGISTERED_PASSWORD, null)

        val isDemoUser = email == DEMO_EMAIL && password == DEMO_PASSWORD
        val isRegisteredUser = email == savedEmail && password == savedPassword

        return isDemoUser || isRegisteredUser
    }

    fun getUserIdByEmail(email: String): Long {
        return if (email == DEMO_EMAIL) 1L else 2L
    }

    fun getRegisterName() : String? = prefs.getString(KEY_REGISTERED_NAME, null)
    fun getRegisteredEmail(): String? = prefs.getString(KEY_REGISTERED_EMAIL, null)
    //fun getRegisteredPassword(): String? = prefs.getString(KEY_REGISTERED_PASSWORD, null)
    fun getRegisteredPhone(): String? = prefs.getString(KEY_REGISTERED_PHONE, null)




}