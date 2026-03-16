package pe.goliva.vet_mascotario.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pe.goliva.vet_mascotario.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSendLink.setOnClickListener {
            binding.tvMessage.text = "Se Envió un enlace de recuperación a tu correo."
        }
    }
}