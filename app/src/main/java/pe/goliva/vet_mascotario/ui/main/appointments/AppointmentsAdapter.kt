package pe.goliva.vet_mascotario.ui.main.appointments

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.model.AppointmentListItem
import pe.goliva.vet_mascotario.databinding.ItemAppointmentBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AppointmentsAdapter(
    private var items: List<AppointmentListItem>,
    private val onAppointmentClick: (AppointmentListItem) -> Unit
) : RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(
        private val binding: ItemAppointmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AppointmentListItem) {
            binding.tvAppointmentPetName.text = item.petName
            binding.tvAppointmentType.text = item.appointmentTypeName
            binding.tvAppointmentBranch.text = item.branchName
            binding.tvAppointmentDateTime.text = formatDateTime(item.startAt, item.endAt)

            val notes = item.notes?.takeIf { it.isNotBlank() }
            binding.tvAppointmentNotes.text = notes ?: "Sin observaciones"

            binding.tvAppointmentStatus.text = mapStatusLabel(item.status)
            binding.tvAppointmentStatus.setTextColor(
                ContextCompat.getColor(binding.root.context, mapStatusTextColor(item.status))
            )

            binding.root.setOnClickListener {
                onAppointmentClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemAppointmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<AppointmentListItem>) {
        items = newItems
        notifyDataSetChanged()
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
}