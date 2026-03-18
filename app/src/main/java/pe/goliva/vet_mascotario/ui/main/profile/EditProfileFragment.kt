package pe.goliva.vet_mascotario.ui.main.profile

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.UserProfileDao
import pe.goliva.vet_mascotario.databinding.FragmentEditProfileBinding
import pe.goliva.vet_mascotario.utils.SessionManager

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var userProfileDao: UserProfileDao

    private var currentUserId: Long = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentEditProfileBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        userProfileDao = UserProfileDao(requireContext())

        setBottomNavVisible(false)

        currentUserId = sessionManager.getUserId()

        setupClicks()
        loadProfileData()
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.layoutChangePhoto.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "La carga de foto se agregará en una siguiente parte",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnSaveProfileChanges.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadProfileData() {
        if (currentUserId == -1L) {
            Toast.makeText(requireContext(), "Sesión no válida", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        val profile = userProfileDao.getUserProfileById(currentUserId)

        if (profile == null) {
            Toast.makeText(requireContext(), "No se pudo cargar el perfil", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        binding.etFullName.setText(profile.fullName)
        binding.etEmail.setText(profile.email)
        binding.etPhone.setText(profile.phone ?: "")
    }

    private fun saveProfile() {
        clearErrors()

        val fullName = binding.etFullName.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val phone = binding.etPhone.text?.toString()?.trim().orEmpty()

        if (fullName.isBlank()) {
            binding.tilFullName.error = "Ingresa tu nombre completo"
            return
        }

        if (email.isBlank()) {
            binding.tilEmail.error = "Ingresa tu correo electrónico"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Ingresa un correo válido"
            return
        }

        val digitsOnlyPhone = phone.filter { it.isDigit() }
        if (phone.isBlank()) {
            binding.tilPhone.error = "Ingresa tu teléfono"
            return
        }

        if (digitsOnlyPhone.length < 9) {
            binding.tilPhone.error = "Ingresa un teléfono válido"
            return
        }

        if (userProfileDao.emailExistsForAnotherUser(email, currentUserId)) {
            binding.tilEmail.error = "Ese correo ya está registrado"
            return
        }

        binding.btnSaveProfileChanges.isEnabled = false

        val updated = userProfileDao.updateUserProfile(
            userId = currentUserId,
            fullName = fullName,
            email = email,
            phone = phone
        )

        binding.btnSaveProfileChanges.isEnabled = true

        if (updated) {
            sessionManager.saveLoginSession(currentUserId, email)

            Toast.makeText(
                requireContext(),
                "Perfil actualizado correctamente",
                Toast.LENGTH_SHORT
            ).show()

            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(
                requireContext(),
                "No se pudo actualizar el perfil",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun clearErrors() {
        binding.tilFullName.error = null
        binding.tilEmail.error = null
        binding.tilPhone.error = null
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