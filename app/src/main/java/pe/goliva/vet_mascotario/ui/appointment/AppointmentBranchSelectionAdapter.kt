package pe.goliva.vet_mascotario.ui.appointment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.model.BranchOption
import pe.goliva.vet_mascotario.databinding.ItemAppointmentBranchBinding

class AppointmentBranchSelectionAdapter(
    private var items: List<BranchOption>,
    private var selectedBranchId: Long?,
    private val onBranchSelected: (BranchOption) -> Unit
) : RecyclerView.Adapter<AppointmentBranchSelectionAdapter.BranchViewHolder>() {

    inner class BranchViewHolder(
        private val binding: ItemAppointmentBranchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BranchOption) {
            binding.tvBranchName.text = item.name

            val info = listOfNotNull(
                item.address?.takeIf { it.isNotBlank() },
                item.phone?.takeIf { it.isNotBlank() }
            ).joinToString(" · ")

            binding.tvBranchInfo.text =
                if (info.isBlank()) "Información no disponible" else info

            val isSelected = item.branchId == selectedBranchId

            binding.layoutSelectedIndicator.visibility =
                if (isSelected) View.VISIBLE else View.GONE

            binding.cardBranchOption.strokeColor = ContextCompat.getColor(
                binding.root.context,
                if (isSelected) R.color.green_primary else R.color.profile_card_stroke
            )

            binding.root.setOnClickListener {
                selectedBranchId = item.branchId
                notifyDataSetChanged()
                onBranchSelected(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder {
        val binding = ItemAppointmentBranchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BranchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<BranchOption>, newSelectedBranchId: Long?) {
        items = newItems
        selectedBranchId = newSelectedBranchId
        notifyDataSetChanged()
    }
}