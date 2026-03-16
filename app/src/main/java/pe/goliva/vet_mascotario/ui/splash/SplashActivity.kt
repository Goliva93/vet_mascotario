package pe.goliva.vet_mascotario.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import pe.goliva.vet_mascotario.MainActivity
import pe.goliva.vet_mascotario.databinding.ActivitySplashBinding
import pe.goliva.vet_mascotario.ui.login.LoginActivity
import pe.goliva.vet_mascotario.ui.onboarding.OnboardingActivity
import pe.goliva.vet_mascotario.utils.SessionManager
import kotlin.jvm.java

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)



        Handler(Looper.getMainLooper()).postDelayed({
            val nextIntent = when {
                sessionManager.isLoggedIn() -> Intent(this, MainActivity::class.java)
                !sessionManager.hasSeenOnboarding() -> Intent(this, OnboardingActivity::class.java)
                else -> Intent(this, LoginActivity::class.java)

            }
            startActivity(nextIntent)
            finish()
        }, 1200)
    }
}