package com.hiskytechs.muhallinewuserapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.MainActivity
import com.hiskytechs.muhallinewuserapp.R
import com.hiskytechs.muhallinewuserapp.Ui.AboutAppActivity
import com.hiskytechs.muhallinewuserapp.Ui.AccountDetailsActivity
import com.hiskytechs.muhallinewuserapp.Ui.CheckoutAddressActivity
import com.hiskytechs.muhallinewuserapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindProfile()
        setupClicks()
    }

    override fun onResume() {
        super.onResume()
        bindProfile()
    }

    private fun bindProfile() {
        val profile = AppData.buyerProfile
        binding.tvStoreName.text = profile.storeName
        binding.tvBuyerMeta.text = getString(
            R.string.profile_member_since_format,
            profile.buyerName,
            profile.memberSince
        )
        binding.tvPhone.text = profile.phoneNumber
        binding.tvEmail.text = profile.email
    }

    private fun setupClicks() {
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), AccountDetailsActivity::class.java))
        }
        binding.rowAccountDetails.setOnClickListener {
            startActivity(Intent(requireContext(), AccountDetailsActivity::class.java))
        }
        binding.rowSavedAddresses.setOnClickListener {
            startActivity(Intent(requireContext(), CheckoutAddressActivity::class.java).apply {
                putExtra(CheckoutAddressActivity.EXTRA_CONTINUE_TO_REVIEW, false)
            })
        }
        binding.rowChats.setOnClickListener {
            (activity as? MainActivity)?.navigateToTab(R.id.nav_chats)
        }
        binding.rowAboutApp.setOnClickListener {
            startActivity(Intent(requireContext(), AboutAppActivity::class.java))
        }
        binding.rowMyOrders.setOnClickListener {
            (activity as? MainActivity)?.navigateToTab(R.id.nav_orders)
        }
        binding.btnLogout.setOnClickListener {
            startActivity(
                Intent().setClassName(
                    requireContext(),
                    "com.hiskytechs.muhallinewuserapp.Ui.LoginActivity"
                )
            )
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
