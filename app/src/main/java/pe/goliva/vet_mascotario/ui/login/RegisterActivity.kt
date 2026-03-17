package pe.goliva.vet_mascotario.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.databinding.ActivityRegisterBinding
import pe.goliva.vet_mascotario.utils.LocalAuthManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var localAuthManager: LocalAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        localAuthManager = LocalAuthManager(this)

        binding.tvBack.setOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val phone = binding.etPhone.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()
        val confirmPassword = binding.etConfirmPassword.text?.toString()?.trim().orEmpty()

        binding.tilName.error = null
        binding.tilEmail.error = null
        binding.tilPhone.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        var hasError = false

        if(name.isBlank()) {
            binding.tilName.error = "Ingresa tu nombre"
            hasError = true
        }
        if(email.isBlank()) {
            binding.tilEmail.error = "Ingresa tu correo electrónico"
            hasError = true
        }
        if(phone.isBlank()) {
            binding.tilPhone.error = "Ingresa tu teléfono"
            hasError = true
        }
        if(password.isBlank()) {
            binding.tilPassword.error = "Ingresa tu contraseña"
            hasError = true
        }
        if(confirmPassword.isBlank()) {
            binding.tilConfirmPassword.error = "Confirma tu contraseña"
            hasError = true
        }

        if(hasError) return

        localAuthManager.registerUser(name, email, phone, password)
        Toast.makeText(this,"Cuenta registrada correctamente", Toast.LENGTH_SHORT).show()
        finish()
    }
}