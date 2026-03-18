package pe.goliva.vet_mascotario.data.model

/**
 * en esta clase crearemos el modelo a usar para inicio de sesión de usuario
 */

data class AuthUser(
    val userId:Long,
    val ownerId: Long?,
    val fullName: String,
    val email: String,
    val phone: String?,
    val homeBranchId: Long?
)