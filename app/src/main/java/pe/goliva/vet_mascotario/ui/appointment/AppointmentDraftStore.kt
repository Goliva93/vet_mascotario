package pe.goliva.vet_mascotario.ui.appointment

object AppointmentDraftStore {
    var petId: Long? = null
    var petName: String = ""

    var appointmentTypeId: Long? = null
    var appointmentTypeName: String = ""
    var appointmentDurationMin: Int? = null

    var branchId: Long? = null
    var branchName: String = ""

    var selectedDate: String = ""
    var selectedTime: String = ""
    var selectedEndTime: String = ""
    var selectedStartAtDb: String = ""
    var selectedEndAtDb: String = ""

    var notes: String = ""

    fun clear() {
        petId = null
        petName = ""

        appointmentTypeId = null
        appointmentTypeName = ""
        appointmentDurationMin = null

        branchId = null
        branchName = ""

        selectedDate = ""
        selectedTime = ""
        selectedEndTime = ""
        selectedStartAtDb = ""
        selectedEndAtDb = ""

        notes = ""
    }
}