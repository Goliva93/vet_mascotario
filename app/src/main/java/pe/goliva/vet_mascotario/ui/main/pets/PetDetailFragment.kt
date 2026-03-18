package pe.goliva.vet_mascotario.ui.main.pets

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.PetDao
import pe.goliva.vet_mascotario.data.model.PetDetail
import pe.goliva.vet_mascotario.databinding.FragmentPetDetailBinding
import pe.goliva.vet_mascotario.utils.SessionManager

class PetDetailFragment : Fragment(R.layout.fragment_pet_detail) {

    private var _binding: FragmentPetDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var petDao: PetDao

    private var currentUserId: Long = -1L
    private var petId: Long = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPetDetailBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        petDao = PetDao(requireContext())

        currentUserId = sessionManager.getUserId()
        petId = arguments?.getLong(ARG_PET_ID, -1L) ?: -1L

        setBottomNavVisible(false)
        setupClicks()
        loadPetDetail()
    }

    override fun onResume() {
        super.onResume()
        _binding?.let {
            loadPetDetail()
        }
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnEditPet.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditPetFragment.newInstance(petId))
                .addToBackStack(EditPetFragment::class.java.simpleName)
                .commit()
        }
    }

    private fun loadPetDetail() {
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

        bindDetail(detail)
    }

    private fun bindDetail(detail: PetDetail) {
        binding.tvPetInitial.text = detail.name.firstOrNull()?.uppercase() ?: "M"
        binding.tvPetName.text = detail.name

        val speciesBreed = listOfNotNull(
            detail.speciesName?.takeIf { it.isNotBlank() },
            detail.breedName?.takeIf { it.isNotBlank() }
        ).joinToString(" · ")

        binding.tvPetSpeciesBreed.text =
            if (speciesBreed.isBlank()) "Información no disponible" else speciesBreed

        val sexLabel = when (detail.sex?.uppercase()) {
            "M" -> "Macho"
            "F" -> "Hembra"
            else -> "No registrado"
        }

        binding.tvPetSex.text = "Sexo: $sexLabel"
        binding.tvPetBirthDate.text =
            "Fecha de nacimiento: ${detail.birthDate?.takeIf { it.isNotBlank() } ?: "No registrada"}"
        binding.tvPetColor.text =
            "Color: ${detail.color?.takeIf { it.isNotBlank() } ?: "No registrado"}"

        binding.tvPetNotes.text =
            detail.notes?.takeIf { it.isNotBlank() } ?: "Sin notas registradas"
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

        fun newInstance(petId: Long): PetDetailFragment {
            return PetDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PET_ID, petId)
                }
            }
        }
    }
}