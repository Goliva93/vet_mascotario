package pe.goliva.vet_mascotario.ui.main.pets

object AddPetDraftStore {
    var petName: String = ""
    var speciesId: Long? = null
    var speciesName: String = ""
    var sex: String? = null

    var breedId: Long? = null
    var breedName: String = ""
    var birthDate: String = ""
    var color: String = ""

    var notes: String = ""
    var photoUrl: String = ""

    fun clear() {
        petName = ""
        speciesId = null
        speciesName = ""
        sex = null

        breedId = null
        breedName = ""
        birthDate = ""
        color = ""

        notes = ""
        photoUrl = ""
    }
}