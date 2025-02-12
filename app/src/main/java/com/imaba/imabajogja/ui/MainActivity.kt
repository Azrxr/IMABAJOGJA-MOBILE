package com.imaba.imabajogja.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.ViewModelFactory
import com.imaba.imabajogja.data.model.UserPreference
import com.imaba.imabajogja.databinding.ActivityMainBinding
import com.imaba.imabajogja.ui.admin.campuse.AdmCampuseFragment
import com.imaba.imabajogja.ui.admin.home.AdmHomeFragment
import com.imaba.imabajogja.ui.admin.member.AdmMemberFragment
import com.imaba.imabajogja.ui.admin.profile.AdmProfileFragment
import com.imaba.imabajogja.ui.campus.CampuseFragment
import com.imaba.imabajogja.ui.home.HomeFragment
import com.imaba.imabajogja.ui.member.MemberFragment
import com.imaba.imabajogja.ui.profile.ProfileFragment
import com.imaba.imabajogja.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bottomNavMenu()
    }

    private fun bottomNavMenu() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {

                val bottomNavigationView: BottomNavigationView = binding.bottomNav
                val bottomNavigationViewAdmin: BottomNavigationView = binding.admBottomNav
                bottomNavigationView.menu.clear()
                bottomNavigationViewAdmin.menu.clear()
                // Load fragment sesuai role
                if (user.role == "admin") {
                    loadFragment(AdmHomeFragment()) // Fragment admin pertama
                    bottomNavigationViewAdmin.inflateMenu(R.menu.adm_bottom_nav_menu) // Gunakan menu admin
                    bottomNavigationViewAdmin.visibility = View.VISIBLE
                } else {
                    loadFragment(HomeFragment()) // Fragment member pertama
                    bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu) // Gunakan menu member
                    bottomNavigationView.visibility = View.VISIBLE
                }

                bottomNavigationViewAdmin.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.nav_home -> loadFragment(AdmHomeFragment())
                        R.id.nav_member -> loadFragment(AdmMemberFragment())
                        R.id.nav_study -> loadFragment(AdmCampuseFragment())
                        R.id.nav_profile -> loadFragment(AdmProfileFragment())
                    }
                    true
                }

                bottomNavigationView.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.nav_home -> loadFragment(HomeFragment())
                        R.id.nav_member -> loadFragment(MemberFragment())
                        R.id.nav_study -> loadFragment(CampuseFragment())
                        R.id.nav_profile -> loadFragment(ProfileFragment())
                    }
                    true
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}