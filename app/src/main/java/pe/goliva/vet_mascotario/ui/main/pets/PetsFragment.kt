package pe.goliva.vet_mascotario.ui.main.pets

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.PetDao
import pe.goliva.vet_mascotario.data.model.PetListItem
import pe.goliva.vet_mascotario.databinding.FragmentPetsBinding
import pe.goliva.vet_mascotario.utils.SessionManager

class PetsFragment : Fragment(R.layout.fragment_pets) {

    private var _binding: FragmentPetsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var petDao: PetDao
    private lateinit var petsAdapter: PetsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPetsBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        petDao = PetDao(requireContext())

        setupRecycler()
        setupClicks()
        loadPets()
    }

    override fun onResume() {
        super.onResume()
        _binding?.let {
            loadPets()
        }
    }

    private fun setupRecycler() {
        petsAdapter = PetsAdapter(emptyList()) { pet ->
            onPetSelected(pet)
        }
        binding.rvPets.adapter = petsAdapter
    }

    private fun setupClicks() {
        binding.btnAddPet.setOnClickListener {
            AddPetDraftStore.clear()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddPetStepOneFragment())
                .addToBackStack(AddPetStepOneFragment::class.java.simpleName)
                .commit()
        }
    }

    private fun loadPets() {
        val userId = sessionManager.getUserId()

        if (userId == -1L) {
            showEmptyState()
            return
        }

        val pets = petDao.getPetsByUserId(userId)

        if (pets.isEmpty()) {
            showEmptyState()
        } else {
            showPetsList(pets)
        }
    }

    private fun showPetsList(pets: List<PetListItem>) {
        binding.layoutEmptyPets.visibility = View.GONE
        binding.rvPets.visibility = View.VISIBLE
        petsAdapter.updateData(pets)
    }

    private fun showEmptyState() {
        binding.rvPets.visibility = View.GONE
        binding.layoutEmptyPets.visibility = View.VISIBLE
        petsAdapter.updateData(emptyList())
    }

    private fun onPetSelected(pet: PetListItem) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PetDetailFragment.newInstance(pet.petId))
            .addToBackStack(PetDetailFragment::class.java.simpleName)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}