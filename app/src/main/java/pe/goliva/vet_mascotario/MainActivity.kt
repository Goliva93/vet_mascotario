package pe.goliva.vet_mascotario

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import pe.goliva.vet_mascotario.databinding.ActivityMainBinding
import pe.goliva.vet_mascotario.ui.main.appointments.AppointmentsFragment
import pe.goliva.vet_mascotario.ui.main.home.HomeFragment
import pe.goliva.vet_mascotario.ui.main.pets.PetsFragment
import pe.goliva.vet_mascotario.ui.main.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_citas -> replaceFragment(AppointmentsFragment())
                R.id.nav_mascotas -> replaceFragment(PetsFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}