package com.hiskytechs.muhallinewuserapp.Ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hiskytechs.muhallinewuserapp.Fragments.SupplierInfoFragment
import com.hiskytechs.muhallinewuserapp.Fragments.SupplierProductsFragment
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySupplierDetailsBinding
import com.google.android.material.tabs.TabLayout

class SupplierDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierDetailsBinding
    private var supplierName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supplierName = intent.getStringExtra("supplier_name")
        val location = intent.getStringExtra("location")

        binding.tvSupplierName.text = supplierName
        binding.tvLocation.text = location ?: binding.tvLocation.text

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupTabs()
        
        // Load products by default
        loadFragment(SupplierProductsFragment.newInstance(supplierName ?: ""))
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadFragment(SupplierProductsFragment.newInstance(supplierName ?: ""))
                    1 -> loadFragment(SupplierInfoFragment.newInstance(supplierName ?: ""))
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}
