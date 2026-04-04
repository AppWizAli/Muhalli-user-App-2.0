package com.hiskytechs.muhallinewuserapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hiskytechs.muhallinewuserapp.Fragments.CategoriesFragment
import com.hiskytechs.muhallinewuserapp.Fragments.ChatsFragment
import com.hiskytechs.muhallinewuserapp.Fragments.HomeFragment
import com.hiskytechs.muhallinewuserapp.Fragments.OrdersFragment
import com.hiskytechs.muhallinewuserapp.Fragments.ProfileFragment
import com.hiskytechs.muhallinewuserapp.Ui.CartActivity
import com.hiskytechs.muhallinewuserapp.Utill.CartManager
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

        binding.layoutCartEntry.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
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
                R.id.nav_chats -> {
                     loadFragment(ChatsFragment())
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

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNavigation(intent)
    }

    private fun handleNavigation(intent: Intent?) {
        val navigateTo = intent?.getStringExtra("navigate_to")
        when (navigateTo) {
            "home" -> navigateToTab(R.id.nav_home)
            "cart" -> {
                startActivity(Intent(this, CartActivity::class.java))
                navigateToTab(R.id.nav_home)
            }
            "chats" -> navigateToTab(R.id.nav_chats)
            "orders" -> navigateToTab(R.id.nav_orders)
            "profile" -> navigateToTab(R.id.nav_profile)
            "categories" -> navigateToTab(R.id.nav_categories)
            else -> navigateToTab(R.id.nav_home)
        }
    }

    fun navigateToTab(itemId: Int) {
        binding.bottomNavigation.selectedItemId = itemId
        val fragment = when (itemId) {
            R.id.nav_categories -> CategoriesFragment()
            R.id.nav_chats -> ChatsFragment()
            R.id.nav_orders -> OrdersFragment()
            R.id.nav_profile -> ProfileFragment()
            else -> HomeFragment()
        }
        loadFragment(fragment)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateCartBadge() {
        val count = CartManager.getCartCount()
        binding.tvCartBadge.visibility = if (count > 0) View.VISIBLE else View.GONE
        binding.tvCartBadge.text = count.toString()
    }
}
