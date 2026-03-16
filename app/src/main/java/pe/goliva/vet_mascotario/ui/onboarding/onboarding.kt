package pe.goliva.vet_mascotario.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.databinding.ActivityOnboardingBinding
import pe.goliva.vet_mascotario.databinding.LayoutOnboardingStep1Binding
import pe.goliva.vet_mascotario.databinding.LayoutOnboardingStep2Binding
import pe.goliva.vet_mascotario.databinding.LayoutOnboardingStep3Binding
import pe.goliva.vet_mascotario.ui.login.LoginActivity
import pe.goliva.vet_mascotario.utils.SessionManager


class  OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var sessionManager: SessionManager

    private var currentStep = 0

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        showStep(currentStep)

        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }
        binding.btnNext.setOnClickListener {
            if (currentStep < 2) {
                currentStep++
                showStep(currentStep)
            } else {
                finishOnboarding()
            }
        }

    }
    private fun showStep(step: Int){
        binding.contentContainer.removeAllViews()
        val inflater = LayoutInflater.from(this)
        when (step){
            0 -> {
                val stepBinding = LayoutOnboardingStep1Binding.inflate(inflater, binding.contentContainer, false)
                binding.contentContainer.addView(stepBinding.root)
                updateIndicators(0)
                binding.btnNext.text = "Siguiente"
            }
            1 -> {
                val stepBinding = LayoutOnboardingStep2Binding.inflate(inflater, binding.contentContainer, false)
                binding.contentContainer.addView(stepBinding.root)
                updateIndicators(1)
                binding.btnNext.text = "Siguiente"
            }
            2 -> {
                val stepBinding = LayoutOnboardingStep3Binding.inflate(inflater, binding.contentContainer,false)
                binding.contentContainer.addView(stepBinding.root)
                updateIndicators(2)
                binding.btnNext.text = "Finalizar"
            }
        }
    }

    private fun updateIndicators(activeStep: Int){
        val active = getColor(R.color.green_primary)
        val inactive = getColor(R.color.gray_card_bg)

        binding.dot1.setBackgroundColor(if (activeStep == 0) active else inactive)
        binding.dot2.setBackgroundColor(if (activeStep == 1) active else inactive)
        binding.dot3.setBackgroundColor(if (activeStep == 2) active else inactive)

    }

    private fun finishOnboarding() {
        sessionManager.setOnboardingSeen(true)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

}