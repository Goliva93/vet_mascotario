package pe.goliva.vet_mascotario.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.AppointmentDao
import pe.goliva.vet_mascotario.data.dao.UserProfileDao
import pe.goliva.vet_mascotario.data.model.HomeUpcomingAppointment
import pe.goliva.vet_mascotario.databinding.FragmentHomeBinding
import pe.goliva.vet_mascotario.ui.appointment.AppointmentCreateActivity
import pe.goliva.vet_mascotario.ui.main.profile.NotificationsFragment
import pe.goliva.vet_mascotario.ui.main.profile.PreferredBranchFragment
import pe.goliva.vet_mascotario.utils.SessionManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var userProfileDao: UserProfileDao
    private lateinit var appointmentDao: AppointmentDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        userProfileDao = UserProfileDao(requireContext())
        appointmentDao = AppointmentDao(requireContext())

        setupClicks()
        loadHomeData()
    }

    override fun onResume() {
        super.onResume()
        _binding?.let {
            loadHomeData()
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

    private fun loadHomeData() {
        val userId = sessionManager.getUserId()

        if (userId == -1L) {
            showFallbackHome(
                userName = "Usuario",
                branchName = "Sede no asignada"
            )
            showNoUpcomingAppointment(branchName = "Sede no asignada")
            return
        }

        val userProfile = userProfileDao.getUserProfileById(userId)

        if (userProfile != null) {
            val firstName = extractFirstName(userProfile.fullName)
            val branchName = userProfile.homeBranchName?.takeIf { it.isNotBlank() } ?: "Sede no asignada"

            binding.tvHomeGreeting.text = firstName
            binding.tvHomeBranch.text = branchName

            loadUpcomingAppointment(userId, branchName)
        } else {
            showFallbackHome(
                userName = "Usuario",
                branchName = "Sede no asignada"
            )
            showNoUpcomingAppointment(branchName = "Sede no asignada")
        }
    }

    private fun loadUpcomingAppointment(userId: Long, branchName: String) {
        val upcoming = appointmentDao.getUpcomingAppointmentByUserId(userId)

        if (upcoming == null) {
            showNoUpcomingAppointment(branchName)
        } else {
            showUpcomingAppointment(upcoming)
        }
    }

    private fun showUpcomingAppointment(upcoming: HomeUpcomingAppointment) {
        binding.tvHomeUpcomingTitle.text =
            "${upcoming.petName} · ${upcoming.appointmentTypeName}"

        binding.tvHomeUpcomingMeta.text =
            "${formatHomeDateTime(upcoming.startAt)} · ${upcoming.branchName}"
    }

    private fun showNoUpcomingAppointment(branchName: String) {
        binding.tvHomeUpcomingTitle.text = "Aún no tienes próximas citas"
        binding.tvHomeUpcomingMeta.text = "Agenda una nueva atención en $branchName"
    }

    private fun showFallbackHome(userName: String, branchName: String) {
        binding.tvHomeGreeting.text = userName
        binding.tvHomeBranch.text = branchName
    }

    private fun extractFirstName(fullName: String): String {
        return fullName.trim()
            .split("\\s+".toRegex())
            .firstOrNull()
            ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            ?: "Usuario"
    }

    private fun formatHomeDateTime(startAt: String): String {
        return try {
            val input = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
            val dateOutput = DateTimeFormatter.ofPattern("dd MMM", Locale("es", "PE"))
            val timeOutput = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

            val start = LocalDateTime.parse(startAt, input)
            "${start.format(dateOutput)} · ${start.format(timeOutput)}"
        } catch (_: Exception) {
            startAt
        }
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