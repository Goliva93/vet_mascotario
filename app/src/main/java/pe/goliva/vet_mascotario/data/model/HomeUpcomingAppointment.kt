package pe.goliva.vet_mascotario.data.model

data class HomeUpcomingAppointment(
    val appointmentId: Long,
    val petName: String,
    val appointmentTypeName: String,
    val branchName: String,
    val startAt: String,
    val endAt: String,
    val status: String
)