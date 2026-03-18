package pe.goliva.vet_mascotario.data.model

data class TimeSlotOption(
    val startTime: String,
    val endTime: String,
    val startAtDb: String,
    val endAtDb: String,
    val isAvailable: Boolean
)