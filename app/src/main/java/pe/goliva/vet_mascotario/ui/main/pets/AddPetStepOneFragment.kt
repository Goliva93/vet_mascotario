package pe.goliva.vet_mascotario.ui.main.pets

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.PetDao
import pe.goliva.vet_mascotario.data.model.SpeciesOption
import pe.goliva.vet_mascotario.databinding.FragmentAddPetStepOneBinding
import pe.goliva.vet_mascotario.utils.SessionManager

class AddPetStepOneFragment : Fragment(R.layout.fragment_add_pet_step_one) {

    private var _binding: FragmentAddPetStepOneBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var petDao: PetDao

    private var currentUserId: Long = -1L
    private var speciesOptions: List<SpeciesOption> = emptyList()
    private var selectedSpecies: SpeciesOption? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddPetStepOneBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        petDao = PetDao(requireContext())

        currentUserId = sessionManager.getUserId()
        setBottomNavVisible(false)

        setupClicks()
        loadSpecies()
        restoreDraft()
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.actvSpecies.setOnItemClickListener { _, _, position, _ ->
            selectedSpecies = speciesOptions.getOrNull(position)
            binding.tilSpecies.error = null
        }

        binding.btnContinueStepOne.setOnClickListener {
            validateAndContinue()
        }
    }

    private fun loadSpecies() {
        speciesOptions = petDao.getActiveSpecies()

        val speciesNames = speciesOptions.map { it.name }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            speciesNames
        )

        binding.actvSpecies.setAdapter(adapter)
    }

    private fun restoreDraft() {
        binding.etPetName.setText(AddPetDraftStore.petName)

        if (AddPetDraftStore.speciesId != null && AddPetDraftStore.speciesName.isNotBlank()) {
            binding.actvSpecies.setText(AddPetDraftStore.speciesName, false)
            selectedSpecies = speciesOptions.firstOrNull { it.speciesId == AddPetDraftStore.speciesId }
        }

        when (AddPetDraftStore.sex) {
            "M" -> binding.chipMale.isChecked = true
            "F" -> binding.chipFemale.isChecked = true
        }
    }

    private fun validateAndContinue() {
        clearErrors()

        if (currentUserId == -1L) {
            Toast.makeText(requireContext(), "Sesión no válida", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        val petName = binding.etPetName.text?.toString()?.trim().orEmpty()
        val species = selectedSpecies

        val sex = when {
            binding.chipMale.isChecked -> "M"
            binding.chipFemale.isChecked -> "F"
            else -> null
        }

        if (petName.isBlank()) {
            binding.tilPetName.error = "Ingresa el nombre de la mascota"
            return
        }

        if (species == null) {
            binding.tilSpecies.error = "Selecciona una especie"
            return
        }

        // Validación simple para evitar duplicados del mismo dueño:
        // mismo nombre + misma especie + mascota activa.
        if (petDao.existsActivePetDuplicate(currentUserId, petName, species.speciesId)) {
            binding.tilPetName.error = "Ya tienes una mascota activa con ese nombre y especie"
            return
        }

        // Guardamos el avance del paso 1.
        AddPetDraftStore.petName = petName
        AddPetDraftStore.speciesId = species.speciesId
        AddPetDraftStore.speciesName = species.name
        AddPetDraftStore.sex = sex

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AddPetStepTwoFragment())
            .addToBackStack(AddPetStepTwoFragment::class.java.simpleName)
            .commit()
    }

    private fun clearErrors() {
        binding.tilPetName.error = null
        binding.tilSpecies.error = null
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