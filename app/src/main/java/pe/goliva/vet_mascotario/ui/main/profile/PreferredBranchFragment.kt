package pe.goliva.vet_mascotario.ui.main.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.UserProfileDao
import pe.goliva.vet_mascotario.data.model.BranchOption
import pe.goliva.vet_mascotario.databinding.FragmentPreferredBranchBinding
import pe.goliva.vet_mascotario.databinding.ItemPreferredBranchBinding
import pe.goliva.vet_mascotario.utils.SessionManager

class PreferredBranchFragment : Fragment(R.layout.fragment_preferred_branch) {

    private var _binding: FragmentPreferredBranchBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var userProfileDao: UserProfileDao

    private var currentUserId: Long = -1L
    private var currentBranchId: Long? = null
    private var selectedBranchId: Long? = null

    private var branches: List<BranchOption> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPreferredBranchBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        userProfileDao = UserProfileDao(requireContext())

        setBottomNavVisible(false)

        currentUserId = sessionManager.getUserId()

        setupClicks()
        loadBranchData()
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnConfirmBranch.setOnClickListener {
            savePreferredBranch()
        }
    }

    private fun loadBranchData() {
        if (currentUserId == -1L) {
            Toast.makeText(requireContext(), "Sesión no válida", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        val profile = userProfileDao.getUserProfileById(currentUserId)
        currentBranchId = profile?.homeBranchId
        selectedBranchId = currentBranchId

        branches = userProfileDao.getActiveBranches()

        if (branches.isEmpty()) {
            Toast.makeText(requireContext(), "No hay sedes disponibles", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        renderBranches()
    }

    private fun renderBranches() {
        binding.layoutBranchOptions.removeAllViews()

        branches.forEach { branch ->
            val itemBinding = ItemPreferredBranchBinding.inflate(
                layoutInflater,
                binding.layoutBranchOptions,
                false
            )

            bindBranchItem(itemBinding, branch, branch.branchId == selectedBranchId)

            itemBinding.cardBranchItem.setOnClickListener {
                selectedBranchId = branch.branchId
                renderBranches()
            }

            binding.layoutBranchOptions.addView(itemBinding.root)
        }
    }

    private fun bindBranchItem(
        itemBinding: ItemPreferredBranchBinding,
        branch: BranchOption,
        isSelected: Boolean
    ) {
        itemBinding.tvBranchName.text = "Sede ${branch.name}"
        itemBinding.tvBranchAddress.text =
            branch.address?.takeIf { it.isNotBlank() } ?: "Dirección no disponible"
        itemBinding.tvBranchPhone.text =
            branch.phone?.takeIf { it.isNotBlank() } ?: "Teléfono no disponible"

        itemBinding.layoutSelectedIndicator.visibility =
            if (isSelected) View.VISIBLE else View.GONE

        itemBinding.cardBranchItem.strokeColor = ContextCompat.getColor(
            requireContext(),
            if (isSelected) R.color.green_primary else R.color.profile_card_stroke
        )
    }

    private fun savePreferredBranch() {
        val branchId = selectedBranchId

        if (currentUserId == -1L || branchId == null) {
            Toast.makeText(requireContext(), "No se pudo confirmar la sede", Toast.LENGTH_SHORT).show()
            return
        }

        if (branchId == currentBranchId) {
            Toast.makeText(requireContext(), "La sede preferida ya está seleccionada", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        binding.btnConfirmBranch.isEnabled = false

        val updated = userProfileDao.updatePreferredBranch(currentUserId, branchId)

        binding.btnConfirmBranch.isEnabled = true

        if (updated) {
            Toast.makeText(
                requireContext(),
                "Sede preferida actualizada correctamente",
                Toast.LENGTH_SHORT
            ).show()
            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(
                requireContext(),
                "No se pudo actualizar la sede preferida",
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