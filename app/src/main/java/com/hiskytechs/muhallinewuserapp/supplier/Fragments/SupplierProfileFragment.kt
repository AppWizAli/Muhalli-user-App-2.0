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
import com.hiskytechs.muhallinewuserapp.databinding.FragmentSupplierProfileBinding
import com.hiskytechs.muhallinewuserapp.supplier.Adapters.SupplierProfileOptionAdapter
import com.hiskytechs.muhallinewuserapp.supplier.Data.SupplierData
import com.hiskytechs.muhallinewuserapp.supplier.Models.SupplierProfileAction
import com.hiskytechs.muhallinewuserapp.supplier.Ui.SupplierEditProfileActivity
import com.hiskytechs.muhallinewuserapp.supplier.Ui.SupplierLoginActivity
import com.hiskytechs.muhallinewuserapp.supplier.Ui.SupplierMainActivity
import com.hiskytechs.muhallinewuserapp.supplier.Utill.initials
import com.hiskytechs.muhallinewuserapp.supplier.Utill.formatPkr

class SupplierProfileFragment : Fragment() {

    private var _binding: FragmentSupplierProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupplierProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvProfileOptions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProfileOptions.adapter = SupplierProfileOptionAdapter(SupplierData.getProfileOptions()) { option ->
            when (option.action) {
                SupplierProfileAction.PRODUCTS -> (activity as? SupplierMainActivity)?.openTab(R.id.nav_supplier_products)
                SupplierProfileAction.ORDERS -> (activity as? SupplierMainActivity)?.openTab(R.id.nav_supplier_orders)
                SupplierProfileAction.EARNINGS -> (activity as? SupplierMainActivity)?.openTab(R.id.nav_supplier_earnings)
                SupplierProfileAction.LOGOUT -> {
                    startActivity(Intent(requireContext(), SupplierLoginActivity::class.java))
                    activity?.finish()
                }
                else -> Toast.makeText(requireContext(), getString(R.string.supplier_coming_soon), Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), SupplierEditProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val profile = SupplierData.getProfile()
        binding.tvAvatarInitial.text = initials(profile.businessName)
        binding.tvProfileBusinessName.text = profile.businessName
        binding.tvProfileOwnerName.text = profile.ownerName
        binding.tvProfilePhone.text = profile.phoneNumber
        binding.tvProfileCity.text = profile.city
        binding.tvProfileEmail.text = profile.emailAddress
        binding.tvProfileMinAmount.text = formatPkr(profile.minimumOrderAmountPkr)
        binding.tvProfileMinQuantity.text = getString(R.string.supplier_min_order_qty) + " ${profile.minimumOrderQuantity}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
