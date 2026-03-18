package pe.goliva.vet_mascotario.data.model

data class AppointmentTypeOption(
    val appointmentTypeId: Long,
    val name: String,
    val defaultDurationMin: Int
)