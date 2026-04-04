package com.hiskytechs.muhallinewuserapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hiskytechs.muhallinewuserapp.Fragments.CartFragment
import com.hiskytechs.muhallinewuserapp.Fragments.CategoriesFragment
import com.hiskytechs.muhallinewuserapp.Fragments.HomeFragment
import com.hiskytechs.muhallinewuserapp.Fragments.OrdersFragment
import com.hiskytechs.muhallinewuserapp.Fragments.ProfileFragment
import com.hiskytechs.muhallinewuserapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            handleNavigation(intent)
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_categories -> {
                     loadFragment(CategoriesFragment())
                    true
                }
                R.id.nav_cart -> {
                     loadFragment(CartFragment())
                    true
                }
                R.id.nav_orders -> {
                     loadFragment(OrdersFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNavigation(intent)
    }

    private fun handleNavigation(intent: Intent?) {
        val navigateTo = intent?.getStringExtra("navigate_to")
        if (navigateTo == "cart") {
            binding.bottomNavigation.selectedItemId = R.id.nav_cart
            loadFragment(CartFragment())
        } else {
            loadFragment(HomeFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
