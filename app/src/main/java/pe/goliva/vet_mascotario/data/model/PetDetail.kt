package pe.goliva.vet_mascotario.data.model

data class PetDetail(
    val petId: Long,
    val name: String,
    val speciesId: Long?,
    val speciesName: String?,
    val breedId: Long?,
    val breedName: String?,
    val sex: String?,
    val birthDate: String?,
    val color: String?,
    val notes: String?,
    val photoUrl: String?
)