package com.hiskytechs.muhallinewuserapp.supplier.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.databinding.FragmentSupplierHomeBinding
import com.hiskytechs.muhallinewuserapp.supplier.Adapters.SupplierQuickActionAdapter
import com.hiskytechs.muhallinewuserapp.supplier.Adapters.SupplierRecentOrderAdapter
import com.hiskytechs.muhallinewuserapp.supplier.Data.SupplierData
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierHomeAction
import com.hiskytechs.muhallinewuserapp.supplier.Ui.SupplierAddProductActivity
import com.hiskytechs.muhallinewuserapp.supplier.Ui.SupplierInventoryManagementActivity
import com.hiskytechs.muhallinewuserapp.supplier.Ui.SupplierMainActivity
import com.hiskytechs.muhallinewuserapp.supplier.Ui.SupplierMessagesActivity
import com.hiskytechs.muhallinewuserapp.supplier.Ui.SupplierOrderStatusActivity
import com.hiskytechs.muhallinewuserapp.supplier.Utill.formatPkr

class SupplierHomeFragment : Fragment() {

    private var _binding: FragmentSupplierHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupplierHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadHome()
    }

    override fun onResume() {
        super.onResume()
        loadHome()
    }

    private fun loadHome() {
        SupplierData.refreshDashboard(
            onSuccess = {
                if (_binding == null) return@refreshDashboard
                bindHome()
            },
            onError = { message ->
                if (_binding == null) return@refreshDashboard
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun bindHome() {
        val profile = SupplierData.getProfile()
        val stats = SupplierData.getDashboardStats()
        binding.tvBusinessName.text = profile.businessName
        binding.tvBusinessSubtitle.text = getString(R.string.supplier_verified_chip)
        binding.tvTodayOrdersValue.text = stats.todayOrders.toString()
        binding.tvPendingOrdersValue.text = stats.pendingOrders.toString()
        binding.tvRevenueValue.text = formatPkr(stats.thisMonthRevenuePkr)
        binding.tvProductsValue.text = stats.totalProducts.toString()
        binding.tvLowStockMessage.text = SupplierData.getLowStockAlert()
        binding.tvNotificationBadge.text = SupplierData.getConversations().sumOf { it.unreadCount }.toString()

        binding.rvQuickActions.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvQuickActions.adapter = SupplierQuickActionAdapter(SupplierData.getQuickActions()) { action ->
            when (action.action) {
                SupplierHomeAction.ADD_PRODUCT -> startActivity(Intent(requireContext(), SupplierAddProductActivity::class.java))
                SupplierHomeAction.VIEW_ORDERS -> (activity as? SupplierMainActivity)?.openTab(R.id.nav_supplier_orders)
                SupplierHomeAction.OPEN_MESSAGES -> startActivity(Intent(requireContext(), SupplierMessagesActivity::class.java))
                SupplierHomeAction.OPEN_INVENTORY -> startActivity(Intent(requireContext(), SupplierInventoryManagementActivity::class.java))
            }
        }

        binding.rvRecentOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentOrders.adapter = SupplierRecentOrderAdapter(SupplierData.getRecentOrders()) {
            SupplierOrderStatusActivity.open(requireContext(), it.id)
        }

        binding.tvViewAllOrders.setOnClickListener {
            (activity as? SupplierMainActivity)?.openTab(R.id.nav_supplier_orders)
        }

        binding.layoutNotifications.setOnClickListener {
            startActivity(Intent(requireContext(), SupplierMessagesActivity::class.java))
        }

        binding.layoutLowStock.setOnClickListener {
            startActivity(Intent(requireContext(), SupplierInventoryManagementActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
