package com.hiskytechs.muhallinewuserapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.CategoryAdapter
import com.hiskytechs.muhallinewuserapp.Models.Category
import com.hiskytechs.muhallinewuserapp.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val categories = listOf(
            Category("Snacks", "245 products", android.R.drawable.ic_menu_gallery, "#FFF8E1"),
            Category("Chips", "189 products", android.R.drawable.ic_menu_gallery, "#E3F2FD"),
            Category("Mineral Water", "158 products", android.R.drawable.ic_menu_gallery, "#E0F2F1"),
            Category("Groceries", "567 products", android.R.drawable.ic_menu_gallery, "#FCE4EC"),
            Category("Drinks", "234 products", android.R.drawable.ic_menu_gallery, "#E8EAF6"),
            Category("Fresh Fruits", "145 products", android.R.drawable.ic_menu_gallery, "#FFEBEE"),
            Category("Meat", "98 products", android.R.drawable.ic_menu_gallery, "#FFF3E0"),
            Category("Coffee & Tea", "123 products", android.R.drawable.ic_menu_gallery, "#F3E5F5")
        )

        binding.rvCategories.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCategories.adapter = CategoryAdapter(categories)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
