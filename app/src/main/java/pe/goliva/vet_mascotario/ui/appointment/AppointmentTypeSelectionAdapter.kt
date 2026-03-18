package pe.goliva.vet_mascotario.ui.appointment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.model.AppointmentTypeOption
import pe.goliva.vet_mascotario.databinding.ItemAppointmentTypeBinding

class AppointmentTypeSelectionAdapter(
    private var items: List<AppointmentTypeOption>,
    private var selectedTypeId: Long?,
    private val onTypeSelected: (AppointmentTypeOption) -> Unit
) : RecyclerView.Adapter<AppointmentTypeSelectionAdapter.TypeViewHolder>() {

    inner class TypeViewHolder(
        private val binding: ItemAppointmentTypeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AppointmentTypeOption) {
            binding.tvTypeName.text = item.name
            binding.tvTypeDuration.text = "Duración estimada: ${item.defaultDurationMin} min"

            val isSelected = item.appointmentTypeId == selectedTypeId

            binding.layoutSelectedIndicator.visibility =
                if (isSelected) View.VISIBLE else View.GONE

            binding.cardTypeOption.strokeColor = ContextCompat.getColor(
                binding.root.context,
                if (isSelected) R.color.green_primary else R.color.profile_card_stroke
            )

            binding.root.setOnClickListener {
                selectedTypeId = item.appointmentTypeId
                notifyDataSetChanged()
                onTypeSelected(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val binding = ItemAppointmentTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<AppointmentTypeOption>, newSelectedTypeId: Long?) {
        items = newItems
        selectedTypeId = newSelectedTypeId
        notifyDataSetChanged()
    }
}