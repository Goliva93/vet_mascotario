package pe.goliva.vet_mascotario.ui.main.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.AuthDao
import pe.goliva.vet_mascotario.databinding.FragmentChangePasswordBinding
import pe.goliva.vet_mascotario.utils.SessionManager

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var authDao: AuthDao

    private var currentUserId: Long = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentChangePasswordBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        authDao = AuthDao(requireContext())

        setBottomNavVisible(false)

        currentUserId = sessionManager.getUserId()

        setupClicks()
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnSavePassword.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        clearErrors()

        if (currentUserId == -1L) {
            Toast.makeText(requireContext(), "Sesión no válida", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        val currentPassword = binding.etCurrentPassword.text?.toString()?.trim().orEmpty()
        val newPassword = binding.etNewPassword.text?.toString()?.trim().orEmpty()
        val confirmNewPassword = binding.etConfirmNewPassword.text?.toString()?.trim().orEmpty()

        if (currentPassword.isBlank()) {
            binding.tilCurrentPassword.error = "Ingresa tu contraseña actual"
            return
        }

        if (newPassword.isBlank()) {
            binding.tilNewPassword.error = "Ingresa la nueva contraseña"
            return
        }

        if (newPassword.length < 6) {
            binding.tilNewPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        if (confirmNewPassword.isBlank()) {
            binding.tilConfirmNewPassword.error = "Confirma la nueva contraseña"
            return
        }

        if (newPassword != confirmNewPassword) {
            binding.tilConfirmNewPassword.error = "Las contraseñas no coinciden"
            return
        }

        if (currentPassword == newPassword) {
            binding.tilNewPassword.error = "La nueva contraseña debe ser diferente"
            return
        }

        if (!authDao.verifyCurrentPassword(currentUserId, currentPassword)) {
            binding.tilCurrentPassword.error = "La contraseña actual no es correcta"
            return
        }

        binding.btnSavePassword.isEnabled = false

        val updated = authDao.updatePassword(currentUserId, newPassword)

        binding.btnSavePassword.isEnabled = true

        if (updated) {
            Toast.makeText(
                requireContext(),
                "Contraseña actualizada correctamente",
                Toast.LENGTH_SHORT
            ).show()
            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(
                requireContext(),
                "No se pudo actualizar la contraseña",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun clearErrors() {
        binding.tilCurrentPassword.error = null
        binding.tilNewPassword.error = null
        binding.tilConfirmNewPassword.error = null
    }

    private fun setBottomNavVisible(visible: Boolean) {
        requireActivity().findViewById<View>(R.id.bottom_nav)?.visibility =
            if (visible) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        setBottomNavVisible(true)
        super.onDestroyView()
        _binding = null
    }
}