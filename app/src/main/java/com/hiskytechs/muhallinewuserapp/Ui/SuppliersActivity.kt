package com.hiskytechs.muhallinewuserapp.Ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.SupplierAdapter
import com.hiskytechs.muhallinewuserapp.Data.AppData
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
            "Showing all verified suppliers"
        } else {
            selectedCategory
        }
        binding.tvResultsCount.text = "${suppliers.size} suppliers found"

        binding.rvSuppliers.layoutManager = LinearLayoutManager(this)
        binding.rvSuppliers.adapter = SupplierAdapter(suppliers, selectedCategory)

        binding.ivBack.setOnClickListener { finish() }
        binding.layoutFilter.setOnClickListener {
            Toast.makeText(this, "Filter options coming soon", Toast.LENGTH_SHORT).show()
        }
        binding.layoutSort.setOnClickListener {
            Toast.makeText(this, "Sort options coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_CATEGORY_NAME = "category_name"
    }
}
