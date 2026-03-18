package pe.goliva.vet_mascotario.ui.main.pets

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.PetDao
import pe.goliva.vet_mascotario.databinding.FragmentAddPetStepThreeBinding
import pe.goliva.vet_mascotario.utils.SessionManager

class AddPetStepThreeFragment : Fragment(R.layout.fragment_add_pet_step_three) {

    private var _binding: FragmentAddPetStepThreeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var petDao: PetDao

    private var currentUserId: Long = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddPetStepThreeBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        petDao = PetDao(requireContext())
        currentUserId = sessionManager.getUserId()

        setBottomNavVisible(false)

        if (!hasRequiredDraftData()) {
            Toast.makeText(requireContext(), "Completa los pasos anteriores", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        setupSummary()
        setupClicks()
        restoreDraft()
    }

    private fun hasRequiredDraftData(): Boolean {
        return currentUserId != -1L &&
                AddPetDraftStore.petName.isNotBlank() &&
                AddPetDraftStore.speciesId != null
    }

    private fun setupSummary() {
        binding.tvPetSummaryName.text = AddPetDraftStore.petName

        val speciesAndBreed = listOfNotNull(
            AddPetDraftStore.speciesName.takeIf { it.isNotBlank() },
            AddPetDraftStore.breedName.takeIf { it.isNotBlank() }
        ).joinToString(" · ")

        binding.tvPetSummarySpecies.text =
            if (speciesAndBreed.isBlank()) "Información básica"
            else speciesAndBreed

        val sexLabel = when (AddPetDraftStore.sex) {
            "M" -> "Macho"
            "F" -> "Hembra"
            else -> null
        }

        val meta = listOfNotNull(
            sexLabel,
            AddPetDraftStore.birthDate.takeIf { it.isNotBlank() },
            AddPetDraftStore.color.takeIf { it.isNotBlank() }
        ).joinToString(" · ")

        binding.tvPetSummaryMeta.text =
            if (meta.isBlank()) "Sin información adicional"
            else meta
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.cardOptionalPhoto.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "La carga de foto se implementará en una siguiente parte",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnSavePet.setOnClickListener {
            savePet()
        }
    }

    private fun restoreDraft() {
        binding.etNotes.setText(AddPetDraftStore.notes)
    }

    private fun savePet() {
        if (currentUserId == -1L) {
            Toast.makeText(requireContext(), "Sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        val notes = binding.etNotes.text?.toString()?.trim().orEmpty()
        AddPetDraftStore.notes = notes

        binding.btnSavePet.isEnabled = false

        val inserted = petDao.insertPetForUser(
            userId = currentUserId,
            petName = AddPetDraftStore.petName,
            speciesId = AddPetDraftStore.speciesId!!,
            breedId = AddPetDraftStore.breedId,
            sex = AddPetDraftStore.sex,
            birthDate = AddPetDraftStore.birthDate.takeIf { it.isNotBlank() },
            color = AddPetDraftStore.color.takeIf { it.isNotBlank() },
            notes = AddPetDraftStore.notes.takeIf { it.isNotBlank() },
            photoUrl = AddPetDraftStore.photoUrl.takeIf { it.isNotBlank() }
        )

        binding.btnSavePet.isEnabled = true

        if (inserted) {
            Toast.makeText(
                requireContext(),
                "Mascota registrada correctamente",
                Toast.LENGTH_SHORT
            ).show()

            AddPetDraftStore.clear()

            parentFragmentManager.popBackStack(
                AddPetStepOneFragment::class.java.simpleName,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        } else {
            Toast.makeText(
                requireContext(),
                "No se pudo registrar la mascota",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setBottomNavVisible(visible: Boolean) {
        requireActivity().findViewById<View>(R.id.bottom_nav)?.visibility =
            if (visible) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        setBottomNavVisible(true)
        super.onDestroyView()
        _binding = null
    }
}