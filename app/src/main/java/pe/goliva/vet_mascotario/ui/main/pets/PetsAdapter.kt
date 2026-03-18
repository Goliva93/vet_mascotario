package pe.goliva.vet_mascotario.ui.main.pets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pe.goliva.vet_mascotario.data.model.PetListItem
import pe.goliva.vet_mascotario.databinding.ItemPetBinding

class PetsAdapter(
    private var items: List<PetListItem>,
    private val onPetClick: (PetListItem) -> Unit
) : RecyclerView.Adapter<PetsAdapter.PetViewHolder>() {

    inner class PetViewHolder(
        private val binding: ItemPetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PetListItem) {
            binding.tvPetInitial.text = item.name.firstOrNull()?.uppercase() ?: "M"
            binding.tvPetName.text = item.name
            binding.tvPetSpecies.text = item.speciesName ?: "Especie no disponible"

            binding.tvPetBreed.text =
                item.breedName?.takeIf { it.isNotBlank() } ?: "Raza no disponible"

            binding.tvPetColor.text =
                item.color?.takeIf { it.isNotBlank() } ?: "Color no registrado"

            val sexLabel = when (item.sex?.uppercase()) {
                "M" -> "Macho"
                "F" -> "Hembra"
                else -> ""
            }

            if (sexLabel.isBlank()) {
                binding.tvPetSex.visibility = View.GONE
            } else {
                binding.tvPetSex.visibility = View.VISIBLE
                binding.tvPetSex.text = sexLabel
            }

            binding.tvPetBirthDate.text =
                item.birthDate?.takeIf { it.isNotBlank() } ?: "Fecha no registrada"

            binding.root.setOnClickListener {
                onPetClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val binding = ItemPetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<PetListItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}