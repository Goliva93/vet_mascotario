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
import pe.goliva.vet_mascotario.databinding.FragmentAddPetStepTwoBinding
import java.util.Calendar
import java.util.Locale

class AddPetStepTwoFragment : Fragment(R.layout.fragment_add_pet_step_two) {

    private var _binding: FragmentAddPetStepTwoBinding? = null
    private val binding get() = _binding!!

    private lateinit var petDao: PetDao
    private var breedOptions: List<BreedOption> = emptyList()
    private var selectedBreed: BreedOption? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddPetStepTwoBinding.bind(view)
        petDao = PetDao(requireContext())

        setBottomNavVisible(false)

        if (AddPetDraftStore.speciesId == null) {
            Toast.makeText(requireContext(), "Primero completa el paso 1", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        setupClicks()
        loadContextData()
        loadBreeds()
        restoreDraft()
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.actvBreed.setOnItemClickListener { _, _, position, _ ->
            selectedBreed = breedOptions.getOrNull(position)
            binding.tilBreed.error = null
        }

        binding.etBirthDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnContinueStepTwo.setOnClickListener {
            saveAndContinue()
        }
    }

    private fun loadContextData() {
        binding.tvSpeciesContext.text = "Especie seleccionada: ${AddPetDraftStore.speciesName}"
    }

    private fun loadBreeds() {
        val speciesId = AddPetDraftStore.speciesId ?: return

        breedOptions = petDao.getBreedsBySpecies(speciesId)

        val breedNames = breedOptions.map { it.name }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            breedNames
        )

        binding.actvBreed.setAdapter(adapter)

        if (breedOptions.isEmpty()) {
            binding.actvBreed.setText("", false)
            binding.actvBreed.hint = "No hay razas registradas"
        }
    }

    private fun restoreDraft() {
        if (AddPetDraftStore.breedId != null && AddPetDraftStore.breedName.isNotBlank()) {
            binding.actvBreed.setText(AddPetDraftStore.breedName, false)
            selectedBreed = breedOptions.firstOrNull { it.breedId == AddPetDraftStore.breedId }
        }

        binding.etBirthDate.setText(AddPetDraftStore.birthDate)
        binding.etColor.setText(AddPetDraftStore.color)
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

    private fun saveAndContinue() {
        clearErrors()

        val birthDate = binding.etBirthDate.text?.toString()?.trim().orEmpty()
        val color = binding.etColor.text?.toString()?.trim().orEmpty()

        AddPetDraftStore.breedId = selectedBreed?.breedId
        AddPetDraftStore.breedName = selectedBreed?.name.orEmpty()
        AddPetDraftStore.birthDate = birthDate
        AddPetDraftStore.color = color

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AddPetStepThreeFragment())
            .addToBackStack(AddPetStepThreeFragment::class.java.simpleName)
            .commit()
    }

    private fun clearErrors() {
        binding.tilBreed.error = null
        binding.tilBirthDate.error = null
        binding.tilColor.error = null
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