package pe.goliva.vet_mascotario.ui.main.appointments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.AppointmentDao
import pe.goliva.vet_mascotario.data.model.AppointmentDetail
import pe.goliva.vet_mascotario.databinding.FragmentAppointmentDetailBinding
import pe.goliva.vet_mascotario.utils.SessionManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AppointmentDetailFragment : Fragment(R.layout.fragment_appointment_detail) {

    private var _binding: FragmentAppointmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var appointmentDao: AppointmentDao

    private var currentUserId: Long = -1L
    private var appointmentId: Long = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAppointmentDetailBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        appointmentDao = AppointmentDao(requireContext())

        currentUserId = sessionManager.getUserId()
        appointmentId = arguments?.getLong(ARG_APPOINTMENT_ID, -1L) ?: -1L

        setBottomNavVisible(false)
        setupClicks()
        loadAppointmentDetail()
    }

    override fun onResume() {
        super.onResume()
        _binding?.let {
            loadAppointmentDetail()
        }
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnRescheduleAppointment.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Reprogramar cita se implementará en la siguiente parte",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnCancelAppointment.setOnClickListener {
            showCancelAppointmentDialog()
        }
    }

    private fun loadAppointmentDetail() {
        if (currentUserId == -1L || appointmentId == -1L) {
            Toast.makeText(requireContext(), "No se pudo cargar la cita", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        val detail = appointmentDao.getAppointmentDetailByIdForUser(currentUserId, appointmentId)

        if (detail == null) {
            Toast.makeText(requireContext(), "Cita no encontrada", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        bindDetail(detail)
    }

    private fun bindDetail(detail: AppointmentDetail) {
        binding.tvDetailPetName.text = detail.petName
        binding.tvDetailType.text = detail.appointmentTypeName
        binding.tvDetailBranch.text = detail.branchName
        binding.tvDetailDateTime.text = formatDateTime(detail.startAt, detail.endAt)

        val statusLabel = mapStatusLabel(detail.status)
        binding.tvDetailStatus.text = statusLabel
        binding.tvDetailStatus.setTextColor(
            ContextCompat.getColor(requireContext(), mapStatusTextColor(detail.status))
        )

        binding.tvDetailNotes.text =
            detail.notes?.takeIf { it.isNotBlank() } ?: "Sin observaciones registradas"

        val isCancelled = detail.status.equals("CANCELLED", ignoreCase = true)
        val isFinished = detail.status.equals("FINISHED", ignoreCase = true)

        binding.cardCancelReason.visibility =
            if (isCancelled && !detail.cancelReason.isNullOrBlank()) View.VISIBLE else View.GONE

        if (binding.cardCancelReason.visibility == View.VISIBLE) {
            binding.tvDetailCancelReason.text = detail.cancelReason
        }

        binding.layoutDetailActions.visibility =
            if (!isCancelled && !isFinished) View.VISIBLE else View.GONE
    }

    private fun showCancelAppointmentDialog() {
        if (currentUserId == -1L || appointmentId == -1L) {
            Toast.makeText(requireContext(), "No se pudo cancelar la cita", Toast.LENGTH_SHORT).show()
            return
        }

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_cancel_appointment, null, false)

        val etCancelReason = dialogView.findViewById<TextInputEditText>(R.id.et_cancel_reason)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setNegativeButton("Volver", null)
            .setPositiveButton("Cancelar cita", null)
            .create()
            .also { dialog ->
                dialog.setOnShowListener {
                    val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    positiveButton.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.status_error)
                    )

                    positiveButton.setOnClickListener {
                        val cancelReason = etCancelReason.text?.toString()?.trim().orEmpty()
                        cancelAppointment(dialog, cancelReason.takeIf { it.isNotBlank() })
                    }
                }
                dialog.show()
            }
    }

    private fun cancelAppointment(
        dialog: AlertDialog,
        cancelReason: String?
    ) {
        val cancelled = appointmentDao.cancelAppointmentForUser(
            userId = currentUserId,
            appointmentId = appointmentId,
            cancelReason = cancelReason
        )

        if (cancelled) {
            dialog.dismiss()
            Toast.makeText(
                requireContext(),
                "Cita cancelada correctamente",
                Toast.LENGTH_SHORT
            ).show()

            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(
                requireContext(),
                "No se pudo cancelar la cita",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun mapStatusLabel(status: String): String {
        return when (status.uppercase()) {
            "PENDING" -> "Pendiente"
            "CONFIRMED" -> "Confirmada"
            "FINISHED" -> "Finalizada"
            "CANCELLED" -> "Cancelada"
            else -> status
        }
    }

    private fun mapStatusTextColor(status: String): Int {
        return when (status.uppercase()) {
            "PENDING" -> R.color.yellow_accent
            "CONFIRMED" -> R.color.green_primary
            "FINISHED" -> R.color.text_gray
            "CANCELLED" -> R.color.status_error
            else -> R.color.text_gray
        }
    }

    private fun formatDateTime(startAt: String, endAt: String): String {
        return try {
            val input = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
            val dateOutput = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "PE"))
            val timeOutput = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

            val start = LocalDateTime.parse(startAt, input)
            val end = LocalDateTime.parse(endAt, input)

            "${start.format(dateOutput)} · ${start.format(timeOutput)} - ${end.format(timeOutput)}"
        } catch (_: Exception) {
            "$startAt - $endAt"
        }
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

    companion object {
        private const val ARG_APPOINTMENT_ID = "arg_appointment_id"

        fun newInstance(appointmentId: Long): AppointmentDetailFragment {
            return AppointmentDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_APPOINTMENT_ID, appointmentId)
                }
            }
        }
    }
}