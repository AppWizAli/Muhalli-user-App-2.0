package com.hiskytechs.muhallinewuserapp.supplier.Ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.ActivitySupplierInventoryManagementBinding
import com.hiskytechs.muhallinewuserapp.supplier.Adapters.SupplierInventoryAdapter
import com.hiskytechs.muhallinewuserapp.supplier.Data.SupplierData
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierProductFilter
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierStockState

class SupplierInventoryManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierInventoryManagementBinding
    private lateinit var inventoryAdapter: SupplierInventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierInventoryManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inventoryAdapter = SupplierInventoryAdapter(
            items = emptyList(),
            onAdjustStock = { product, delta ->
                SupplierData.adjustStock(product.id, delta)
                loadInventory()
            },
            onUpdate = {
                Toast.makeText(this, getString(R.string.supplier_stock_updated), Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvInventory.layoutManager = LinearLayoutManager(this)
        binding.rvInventory.adapter = inventoryAdapter
        binding.ivBack.setOnClickListener { finish() }
        loadInventory()
    }

    private fun loadInventory() {
        val products = SupplierData.getProducts(SupplierProductFilter.ALL)
        binding.tvInventoryTotalProducts.text = products.size.toString()
        binding.tvInventoryLowStock.text = products.count { it.stockState == SupplierStockState.LOW_STOCK }.toString()
        binding.tvInventoryOutOfStock.text = products.count { it.stockState == SupplierStockState.OUT_OF_STOCK }.toString()
        inventoryAdapter.updateItems(products)
    }
}
