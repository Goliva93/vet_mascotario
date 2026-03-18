package pe.goliva.vet_mascotario.ui.main.appointments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.AppointmentDao
import pe.goliva.vet_mascotario.data.model.AppointmentListItem
import pe.goliva.vet_mascotario.databinding.FragmentAppointmentsBinding
import pe.goliva.vet_mascotario.ui.appointment.AppointmentCreateActivity
import pe.goliva.vet_mascotario.utils.SessionManager

class AppointmentsFragment : Fragment(R.layout.fragment_appointments) {

    private var _binding: FragmentAppointmentsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var appointmentDao: AppointmentDao
    private lateinit var appointmentsAdapter: AppointmentsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAppointmentsBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        appointmentDao = AppointmentDao(requireContext())

        setupRecycler()
        setupClicks()
        loadAppointments()
    }

    override fun onResume() {
        super.onResume()
        _binding?.let {
            loadAppointments()
        }
    }

    private fun setupRecycler() {
        appointmentsAdapter = AppointmentsAdapter(emptyList()) { appointment ->
            onAppointmentSelected(appointment)
        }
        binding.rvAppointments.adapter = appointmentsAdapter
    }

    private fun setupClicks() {
        binding.btnAddAppointment.setOnClickListener {
            startActivity(Intent(requireContext(), AppointmentCreateActivity::class.java))
        }
    }

    private fun loadAppointments() {
        val userId = sessionManager.getUserId()

        if (userId == -1L) {
            showEmptyState()
            return
        }

        val appointments = appointmentDao.getAppointmentsByUserId(userId)

        if (appointments.isEmpty()) {
            showEmptyState()
        } else {
            showAppointmentsList(appointments)
        }
    }

    private fun showAppointmentsList(appointments: List<AppointmentListItem>) {
        binding.layoutEmptyAppointments.visibility = View.GONE
        binding.rvAppointments.visibility = View.VISIBLE
        appointmentsAdapter.updateData(appointments)
    }

    private fun showEmptyState() {
        binding.rvAppointments.visibility = View.GONE
        binding.layoutEmptyAppointments.visibility = View.VISIBLE
        appointmentsAdapter.updateData(emptyList())
    }

    private fun onAppointmentSelected(appointment: AppointmentListItem) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                AppointmentDetailFragment.newInstance(appointment.appointmentId)
            )
            .addToBackStack(AppointmentDetailFragment::class.java.simpleName)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}