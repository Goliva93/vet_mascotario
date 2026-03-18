package pe.goliva.vet_mascotario.ui.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.UserProfileDao
import pe.goliva.vet_mascotario.databinding.FragmentProfileBinding
import pe.goliva.vet_mascotario.ui.login.LoginActivity
import pe.goliva.vet_mascotario.utils.SessionManager

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var userProfileDao: UserProfileDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProfileBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        userProfileDao = UserProfileDao(requireContext())

        setupClicks()
        loadProfileDao()
    }

    override fun onResume() {
        super.onResume()
        _binding?.let {
            loadProfileDao()
        }
    }

    private fun setupClicks() {
        binding.cardEditProfile.setOnClickListener {
            //showComingSoon("Cambiar perfil")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditProfileFragment())
                .addToBackStack(EditProfileFragment::class.java.simpleName)
                .commit()
        }

        binding.cardChangePassword.setOnClickListener {
            //showComingSoon("Cambiar contraseña")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ChangePasswordFragment())
                .addToBackStack(ChangePasswordFragment::class.java.simpleName)
                .commit()
        }

        binding.cardPreferredBranch.setOnClickListener {
            //showComingSoon("Cambiar sede preferida")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PreferredBranchFragment())
                .addToBackStack(PreferredBranchFragment::class.java.simpleName)
                .commit()
        }

        binding.cardNotifications.setOnClickListener {
            //showComingSoon("Notificaciones")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotificationsFragment())
                .addToBackStack(NotificationsFragment::class.java.simpleName)
                .commit()
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadProfileDao() {
        val userId = sessionManager.getUserId()

        if (userId == -1L) {
            showFallbackProfile(
                name = "Usuario no disponible",
                email = "Correo no disponible",
                phone = "Sin teléfono",
                branch = "Sede no asignada"
            )
            return
        }

        val userProfile = userProfileDao.getUserProfileById(userId)

        if (userProfile != null) {
            binding.tvProfileName.text = userProfile.fullName.ifBlank { "Usuario" }
            binding.tvProfileEmail.text = userProfile.email.ifBlank { "Correo no disponible" }
            binding.tvProfilePhone.text = userProfile.phone?.takeIf { it.isNotBlank() } ?: "Sin teléfono"
            binding.tvProfileBranchChip.text =
                userProfile.homeBranchName?.takeIf { it.isNotBlank() } ?: "Sede no asignada"
        } else {
            showFallbackProfile(
                name = "Usuario no encontrado",
                email = "Correo no disponible",
                phone = "Sin teléfono",
                branch = "Sede no asignada"
            )
        }
    }

    private fun showFallbackProfile(
        name: String,
        email: String,
        phone: String,
        branch: String
    ) {
        binding.tvProfileName.text = name
        binding.tvProfileEmail.text = email
        binding.tvProfilePhone.text = phone
        binding.tvProfileBranchChip.text = branch
    }

    private fun showComingSoon(sectionName: String) {
        Toast.makeText(
            requireContext(),
            "$sectionName estará disponible en la siguiente parte",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}