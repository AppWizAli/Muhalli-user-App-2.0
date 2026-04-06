package com.hiskytechs.muhallinewuserapp.supplier.Ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySupplierMainBinding
import com.hiskytechs.muhallinewuserapp.supplier.Fragments.SupplierEarningsFragment
import com.hiskytechs.muhallinewuserapp.supplier.Fragments.SupplierHomeFragment
import com.hiskytechs.muhallinewuserapp.supplier.Fragments.SupplierOrdersFragment
import com.hiskytechs.muhallinewuserapp.supplier.Fragments.SupplierProductsFragment
import com.hiskytechs.muhallinewuserapp.supplier.Fragments.SupplierProfileFragment

class SupplierMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            openTab(item.itemId)
            true
        }

        if (savedInstanceState == null) {
            val destination = intent.getIntExtra(EXTRA_TAB_ID, R.id.nav_supplier_home)
            binding.bottomNavigation.selectedItemId = destination
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val destination = intent.getIntExtra(EXTRA_TAB_ID, R.id.nav_supplier_home)
        binding.bottomNavigation.selectedItemId = destination
    }

    fun openTab(itemId: Int) {
        val fragment: Fragment = when (itemId) {
            R.id.nav_supplier_products -> SupplierProductsFragment()
            R.id.nav_supplier_orders -> SupplierOrdersFragment()
            R.id.nav_supplier_earnings -> SupplierEarningsFragment()
            R.id.nav_supplier_profile -> SupplierProfileFragment()
            else -> SupplierHomeFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    companion object {
        private const val EXTRA_TAB_ID = "extra_tab_id"

        fun open(context: Context, tabId: Int) {
            context.startActivity(
                Intent(context, SupplierMainActivity::class.java).putExtra(EXTRA_TAB_ID, tabId)
            )
        }
    }
}
