package pe.goliva.vet_mascotario.ui.main.profile


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.databinding.FragmentProfileBinding
import pe.goliva.vet_mascotario.ui.login.LoginActivity
import pe.goliva.vet_mascotario.utils.SessionManager

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProfileBinding.bind(view)
        sessionManager = SessionManager(requireContext())

        val email = sessionManager.getUserEmail() ?: "Sin correo"
        binding.tvProfileEmail.text = "Correo: $email"

        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }
}