package pe.goliva.vet_mascotario.data.model

data class UserProfile(
    val userId: Long,
    val ownerId: Long?,
    val fullName: String,
    val email: String,
    val phone: String?,
    val homeBranchId: Long?,
    val homeBranchName: String?
)