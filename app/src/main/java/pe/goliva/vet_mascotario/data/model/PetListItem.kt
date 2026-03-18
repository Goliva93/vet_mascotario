package pe.goliva.vet_mascotario.data.model

data class PetListItem(
    val petId: Long,
    val name: String,
    val speciesName: String?,
    val breedName: String?,
    val sex: String?,
    val birthDate: String?,
    val color: String?,
    val photoUrl: String?
)
