package pe.goliva.vet_mascotario.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.UserProfileDao
import pe.goliva.vet_mascotario.databinding.FragmentHomeBinding
import pe.goliva.vet_mascotario.ui.appointment.AppointmentCreateActivity
import pe.goliva.vet_mascotario.ui.main.profile.NotificationsFragment
import pe.goliva.vet_mascotario.ui.main.profile.PreferredBranchFragment
import pe.goliva.vet_mascotario.utils.SessionManager

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var userProfileDao: UserProfileDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        userProfileDao = UserProfileDao(requireContext())

        setupClicks()
        loadUserData()
    }

    override fun onResume() {
        super.onResume()
        _binding?.let {
            loadUserData()
        }
    }

    private fun setupClicks() {
        binding.btnHomeNotifications.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotificationsFragment())
                .addToBackStack(NotificationsFragment::class.java.simpleName)
                .commit()
        }

        binding.layoutHomeBranch.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PreferredBranchFragment())
                .addToBackStack(PreferredBranchFragment::class.java.simpleName)
                .commit()
        }

        binding.btnViewUpcoming.setOnClickListener {
            navigateToBottomTab(R.id.nav_citas)
        }

        binding.btnScheduleAppointment.setOnClickListener {
            startActivity(Intent(requireContext(), AppointmentCreateActivity::class.java))
        }

        binding.cardQuickAppointments.setOnClickListener {
            navigateToBottomTab(R.id.nav_citas)
        }

        binding.cardQuickPets.setOnClickListener {
            navigateToBottomTab(R.id.nav_mascotas)
        }
    }

    private fun loadUserData() {
        val userId = sessionManager.getUserId()

        if (userId == -1L) {
            showFallbackHome(
                userName = "Usuario",
                branchName = "Sede no asignada"
            )
            return
        }

        val userProfile = userProfileDao.getUserProfileById(userId)

        if (userProfile != null) {
            val firstName = extractFirstName(userProfile.fullName)
            val branchName = userProfile.homeBranchName?.takeIf { it.isNotBlank() } ?: "Sede no asignada"

            binding.tvHomeGreeting.text = firstName
            binding.tvHomeBranch.text = branchName
            binding.tvHomeUpcomingMeta.text = "Agenda una nueva atención en $branchName"
        } else {
            showFallbackHome(
                userName = "Usuario",
                branchName = "Sede no asignada"
            )
        }
    }

    private fun showFallbackHome(userName: String, branchName: String) {
        binding.tvHomeGreeting.text = userName
        binding.tvHomeBranch.text = branchName
        binding.tvHomeUpcomingMeta.text = "Agenda una nueva atención en $branchName"
    }

    private fun extractFirstName(fullName: String): String {
        return fullName.trim()
            .split("\\s+".toRegex())
            .firstOrNull()
            ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            ?: "Usuario"
    }

    private fun navigateToBottomTab(menuItemId: Int) {
        requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottom_nav)
            ?.selectedItemId = menuItemId
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}