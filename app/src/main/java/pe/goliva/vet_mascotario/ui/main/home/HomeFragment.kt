package pe.goliva.vet_mascotario.ui.main.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.R
import pe.goliva.vet_mascotario.data.dao.UserProfileDao
import pe.goliva.vet_mascotario.databinding.FragmentHomeBinding
import pe.goliva.vet_mascotario.utils.SessionManager

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var userProfileDao: UserProfileDao

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeBinding.bind(view)

        sessionManager = SessionManager(requireContext())
        userProfileDao = UserProfileDao(requireContext())

        loadUserData()

        binding.btnQuickAppointments.setOnClickListener {
            Toast.makeText(requireContext(), "Aquí se abriran las citas", Toast.LENGTH_SHORT).show()
        }
        binding.btnQuickPets.setOnClickListener{
            Toast.makeText(requireContext(),"Aquí estarán tu listado de mascotas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData(){
        val userId = sessionManager.getUserId()

        if(userId == -1L){
            binding.tvHomeGreeting.text = "Hello"
            binding.tvHomeBranch.text = "_binding"
            return
        }

        val userProfile = userProfileDao.getUserProfileById(userId)

        if (userProfile != null) {
            binding.tvHomeGreeting.text = "Hola, ${userProfile.fullName}"
            binding.tvHomeBranch.text = "Sede preferida: ${userProfile.homeBranchName ?: "No asignada"}"
        } else {
            binding.tvHomeGreeting.text = "Hello"
            binding.tvHomeBranch.text = "No se pudo cargar la sede"
        }
    }

    override fun onDestroyView () {
        super.onDestroyView()
        _binding = null
    }

}