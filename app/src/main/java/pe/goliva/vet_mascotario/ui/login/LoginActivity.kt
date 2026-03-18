package pe.goliva.vet_mascotario.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pe.goliva.vet_mascotario.MainActivity
import pe.goliva.vet_mascotario.data.dao.AuthDao

import pe.goliva.vet_mascotario.databinding.ActivityLoginBinding
import pe.goliva.vet_mascotario.utils.LocalAuthManager
import pe.goliva.vet_mascotario.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var authDao: AuthDao
    //private lateinit var localAuthManager: LocalAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        //localAuthManager = LocalAuthManager(this)
        authDao = AuthDao(this)

        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }

        binding.btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

    }

    private fun attemptLogin() {
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        binding.tilEmail.error = null
        binding.tilPassword.error = null

        var hasError = false

        if(email.isBlank()){
            binding.tilEmail.error = "Ingresa tu correo"
            hasError = true
        }
        if(password.isBlank()){
            binding.tilPassword.error = "Ingresa tu contraseña"
            hasError = true
        }

        if(hasError) return

        //val isValid = localAuthManager.authenticate(email, password)
        val user = authDao.authenticate(email, password)

        if (user != null){

            //val userId = localAuthManager.getUserIdByEmail(email)
            sessionManager.saveLoginSession(user.userId,email)
            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
        }
    }
}