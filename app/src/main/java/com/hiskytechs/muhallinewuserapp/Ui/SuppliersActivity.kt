package com.hiskytechs.muhallinewuserapp.Ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.SupplierAdapter
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySuppliersBinding

class SuppliersActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuppliersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuppliersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedCategory = intent.getStringExtra(EXTRA_CATEGORY_NAME).orEmpty()
        val suppliers = if (selectedCategory.isBlank()) {
            AppData.suppliers
        } else {
            AppData.suppliersForCategory(selectedCategory)
        }

        binding.tvSelectedCategory.text = if (selectedCategory.isBlank()) {
            getString(R.string.showing_all_verified_suppliers)
        } else {
            selectedCategory
        }
        binding.tvResultsCount.text = getString(R.string.suppliers_found_count, suppliers.size)

        binding.rvSuppliers.layoutManager = LinearLayoutManager(this)
        binding.rvSuppliers.adapter = SupplierAdapter(suppliers, selectedCategory)

        binding.ivBack.setOnClickListener { finish() }
        binding.layoutFilter.setOnClickListener {
            Toast.makeText(this, getString(R.string.filter_options_coming_soon), Toast.LENGTH_SHORT)
                .show()
        }
        binding.layoutSort.setOnClickListener {
            Toast.makeText(this, getString(R.string.sort_options_coming_soon), Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        const val EXTRA_CATEGORY_NAME = "category_name"
    }
}
