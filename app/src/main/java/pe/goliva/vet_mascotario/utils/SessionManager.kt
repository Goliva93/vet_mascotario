package pe.goliva.vet_mascotario.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context : Context){

    private val prefs: SharedPreferences =
        context.getSharedPreferences("el_mascotario_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ONBOARDING_SEEN = "onboarding_seen"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
    }
    fun setOnboardingSeen(seen: Boolean) {
        prefs.edit { putBoolean(KEY_ONBOARDING_SEEN, seen) }
    }
    fun hasSeenOnboarding(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_SEEN, false)
    }
    fun saveLoginSession(userId: Long, email: String){
        prefs.edit {
            putBoolean(KEY_IS_LOGGED_IN, true)
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_USER_EMAIL, email)
            .apply()
        }
    }
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1L)
    }
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }
    fun clearSession() {
        prefs.edit {
            putBoolean(KEY_IS_LOGGED_IN, false)
                .remove(KEY_USER_ID)
                .remove(KEY_USER_EMAIL)
        }
    }

}
