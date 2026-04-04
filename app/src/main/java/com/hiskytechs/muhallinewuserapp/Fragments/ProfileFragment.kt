package com.hiskytechs.muhallinewuserapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hiskytechs.muhallinewuserapp.Ui.LoginActivity
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
        
        // This is a mockup, in a real app you'd handle clicks for each item
        // For now, let's just make the logout work for demonstration
        // Note: The layout uses <include>, so we'd need to set IDs in the layout to access them specifically via binding
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
