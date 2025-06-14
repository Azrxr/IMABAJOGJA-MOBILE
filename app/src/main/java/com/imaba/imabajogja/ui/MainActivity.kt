package com.imaba.imabajogja.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.imaba.imabajogja.R
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Deteksi mode terang/gelap
        val isDarkMode =
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> true
                else -> false
            }

// Ambil warna sesuai mode
        val resolvedColor = ContextCompat.getColor(
            this,
            if (isDarkMode) R.color.maroon_primary_dark else R.color.maroon_primary
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                // Bersihkan flag transparan
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                // Aktifkan menggambar sistem bar
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                statusBarColor = resolvedColor
                navigationBarColor = resolvedColor
            }
        }

// Untuk Android M+ (ikon status bar terang/gelap)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            WindowCompat.getInsetsController(
                window,
                window.decorView
            )?.isAppearanceLightStatusBars = !isDarkMode
        }

//        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (savedInstanceState == null) {
            bottomNavMenu()
        }
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
                    if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) == null) {
                        loadFragment(AdmHomeFragment())
                    }
                    bottomNavigationViewAdmin.inflateMenu(R.menu.adm_bottom_nav_menu)
                    bottomNavigationViewAdmin.visibility = View.VISIBLE
                } else {
                    if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) == null) {
                        loadFragment(HomeFragment())
                    }
                    bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu)
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