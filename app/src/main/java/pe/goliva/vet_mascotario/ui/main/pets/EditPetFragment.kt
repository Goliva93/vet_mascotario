package pe.goliva.vet_mascotario.ui.main.pets

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.PetDao
import pe.goliva.vet_mascotario.data.model.BreedOption
import pe.goliva.vet_mascotario.data.model.PetDetail
import pe.goliva.vet_mascotario.data.model.SpeciesOption
import pe.goliva.vet_mascotario.databinding.FragmentEditPetBinding
import pe.goliva.vet_mascotario.utils.SessionManager
import java.util.Calendar
import java.util.Locale

class EditPetFragment : Fragment(R.layout.fragment_edit_pet) {

    private var _binding: FragmentEditPetBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var petDao: PetDao

    private var currentUserId: Long = -1L
    private var petId: Long = -1L

    private var speciesOptions: List<SpeciesOption> = emptyList()
    private var breedOptions: List<BreedOption> = emptyList()

    private var selectedSpecies: SpeciesOption? = null
    private var selectedBreed: BreedOption? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentEditPetBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        petDao = PetDao(requireContext())

        currentUserId = sessionManager.getUserId()
        petId = arguments?.getLong(ARG_PET_ID, -1L) ?: -1L

        setBottomNavVisible(false)

        setupClicks()
        loadSpecies()
        loadPet()
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.actvSpecies.setOnItemClickListener { _, _, position, _ ->
            selectedSpecies = speciesOptions.getOrNull(position)
            binding.tilSpecies.error = null

            selectedBreed = null
            binding.actvBreed.setText("", false)
            loadBreedsForSelectedSpecies()
        }

        binding.actvBreed.setOnItemClickListener { _, _, position, _ ->
            selectedBreed = breedOptions.getOrNull(position)
            binding.tilBreed.error = null
        }

        binding.etBirthDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSavePetChanges.setOnClickListener {
            saveChanges()
        }
    }

    private fun loadSpecies() {
        speciesOptions = petDao.getActiveSpecies()

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            speciesOptions.map { it.name }
        )

        binding.actvSpecies.setAdapter(adapter)
    }

    private fun loadPet() {
        if (currentUserId == -1L || petId == -1L) {
            Toast.makeText(requireContext(), "No se pudo cargar la mascota", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        val detail = petDao.getPetDetailByIdForUser(currentUserId, petId)

        if (detail == null) {
            Toast.makeText(requireContext(), "Mascota no encontrada", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        bindPet(detail)
    }

    private fun bindPet(detail: PetDetail) {
        binding.etPetName.setText(detail.name)
        binding.etBirthDate.setText(detail.birthDate ?: "")
        binding.etColor.setText(detail.color ?: "")
        binding.etNotes.setText(detail.notes ?: "")

        when (detail.sex?.uppercase()) {
            "M" -> binding.chipMale.isChecked = true
            "F" -> binding.chipFemale.isChecked = true
        }

        selectedSpecies = speciesOptions.firstOrNull { it.speciesId == detail.speciesId }
        binding.actvSpecies.setText(selectedSpecies?.name ?: detail.speciesName.orEmpty(), false)

        loadBreedsForSelectedSpecies()

        selectedBreed = breedOptions.firstOrNull { it.breedId == detail.breedId }
        binding.actvBreed.setText(selectedBreed?.name ?: detail.breedName.orEmpty(), false)
    }

    private fun loadBreedsForSelectedSpecies() {
        val speciesId = selectedSpecies?.speciesId
        if (speciesId == null) {
            breedOptions = emptyList()
            binding.actvBreed.setAdapter(null)
            return
        }

        breedOptions = petDao.getBreedsBySpecies(speciesId)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            breedOptions.map { it.name }
        )

        binding.actvBreed.setAdapter(adapter)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format(
                    Locale.US,
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    dayOfMonth
                )
                binding.etBirthDate.setText(formattedDate)
                binding.tilBirthDate.error = null
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.show()
    }

    private fun saveChanges() {
        clearErrors()

        if (currentUserId == -1L || petId == -1L) {
            Toast.makeText(requireContext(), "Sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        val petName = binding.etPetName.text?.toString()?.trim().orEmpty()
        val species = selectedSpecies
        val breed = selectedBreed
        val birthDate = binding.etBirthDate.text?.toString()?.trim().orEmpty()
        val color = binding.etColor.text?.toString()?.trim().orEmpty()
        val notes = binding.etNotes.text?.toString()?.trim().orEmpty()

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

        if (petDao.existsActivePetDuplicateExcludingPet(
                userId = currentUserId,
                petId = petId,
                petName = petName,
                speciesId = species.speciesId
            )
        ) {
            binding.tilPetName.error = "Ya tienes otra mascota activa con ese nombre y especie"
            return
        }

        binding.btnSavePetChanges.isEnabled = false

        val updated = petDao.updatePetForUser(
            userId = currentUserId,
            petId = petId,
            petName = petName,
            speciesId = species.speciesId,
            breedId = breed?.breedId,
            sex = sex,
            birthDate = birthDate.takeIf { it.isNotBlank() },
            color = color.takeIf { it.isNotBlank() },
            notes = notes.takeIf { it.isNotBlank() }
        )

        binding.btnSavePetChanges.isEnabled = true

        if (updated) {
            Toast.makeText(
                requireContext(),
                "Mascota actualizada correctamente",
                Toast.LENGTH_SHORT
            ).show()
            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(
                requireContext(),
                "No se pudo actualizar la mascota",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun clearErrors() {
        binding.tilPetName.error = null
        binding.tilSpecies.error = null
        binding.tilBreed.error = null
        binding.tilBirthDate.error = null
        binding.tilColor.error = null
        binding.tilNotes.error = null
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

    companion object {
        private const val ARG_PET_ID = "arg_pet_id"

        fun newInstance(petId: Long): EditPetFragment {
            return EditPetFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PET_ID, petId)
                }
            }
        }
    }
}