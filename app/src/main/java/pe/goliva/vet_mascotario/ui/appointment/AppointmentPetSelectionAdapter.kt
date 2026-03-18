package pe.goliva.vet_mascotario.ui.appointment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.model.PetListItem
import pe.goliva.vet_mascotario.databinding.ItemAppointmentPetBinding

class AppointmentPetSelectionAdapter(
    private var items: List<PetListItem>,
    private var selectedPetId: Long?,
    private val onPetSelected: (PetListItem) -> Unit
) : RecyclerView.Adapter<AppointmentPetSelectionAdapter.PetOptionViewHolder>() {

    inner class PetOptionViewHolder(
        private val binding: ItemAppointmentPetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PetListItem) {
            binding.tvPetInitial.text = item.name.firstOrNull()?.uppercase() ?: "M"
            binding.tvPetName.text = item.name

            val info = listOfNotNull(
                item.speciesName?.takeIf { it.isNotBlank() },
                item.breedName?.takeIf { it.isNotBlank() }
            ).joinToString(" · ")

            binding.tvPetInfo.text =
                if (info.isBlank()) "Información no disponible" else info

            val isSelected = item.petId == selectedPetId

            binding.layoutSelectedIndicator.visibility =
                if (isSelected) View.VISIBLE else View.GONE

            binding.cardPetOption.strokeColor = ContextCompat.getColor(
                binding.root.context,
                if (isSelected) R.color.green_primary else R.color.profile_card_stroke
            )

            binding.root.setOnClickListener {
                selectedPetId = item.petId
                notifyDataSetChanged()
                onPetSelected(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetOptionViewHolder {
        val binding = ItemAppointmentPetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PetOptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PetOptionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<PetListItem>, newSelectedPetId: Long?) {
        items = newItems
        selectedPetId = newSelectedPetId
        notifyDataSetChanged()
    }
}