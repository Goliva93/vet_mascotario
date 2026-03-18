package pe.goliva.vet_mascotario.ui.appointment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.model.TimeSlotOption
import pe.goliva.vet_mascotario.databinding.ItemTimeSlotBinding

class AppointmentTimeSlotAdapter(
    private var items: List<TimeSlotOption>,
    private var selectedStartAtDb: String,
    private val onSlotSelected: (TimeSlotOption) -> Unit
) : RecyclerView.Adapter<AppointmentTimeSlotAdapter.TimeSlotViewHolder>() {

    inner class TimeSlotViewHolder(
        private val binding: ItemTimeSlotBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TimeSlotOption) {
            binding.tvSlotTime.text = "${item.startTime} - ${item.endTime}"

            if (item.isAvailable) {
                binding.tvSlotStatus.text = "Disponible"
                binding.tvSlotStatus.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.green_primary)
                )
                binding.root.isEnabled = true
                binding.cardTimeSlot.alpha = 1f
            } else {
                binding.tvSlotStatus.text = "No disponible"
                binding.tvSlotStatus.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.status_error)
                )
                binding.root.isEnabled = false
                binding.cardTimeSlot.alpha = 0.55f
            }

            val isSelected = item.startAtDb == selectedStartAtDb

            binding.layoutSelectedIndicator.visibility =
                if (isSelected && item.isAvailable) View.VISIBLE else View.GONE

            binding.cardTimeSlot.strokeColor = ContextCompat.getColor(
                binding.root.context,
                if (isSelected && item.isAvailable) R.color.green_primary else R.color.profile_card_stroke
            )

            binding.root.setOnClickListener {
                if (!item.isAvailable) return@setOnClickListener
                selectedStartAtDb = item.startAtDb
                notifyDataSetChanged()
                onSlotSelected(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val binding = ItemTimeSlotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TimeSlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<TimeSlotOption>, newSelectedStartAtDb: String) {
        items = newItems
        selectedStartAtDb = newSelectedStartAtDb
        notifyDataSetChanged()
    }
}