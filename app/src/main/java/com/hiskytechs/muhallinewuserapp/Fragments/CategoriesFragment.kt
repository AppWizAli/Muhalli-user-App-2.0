package com.hiskytechs.muhallinewuserapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.CategoryAdapter
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.Ui.SuppliersActivity
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
        binding.rvCategories.layoutManager = GridLayoutManager(requireContext(), 2)
        loadCategories()
    }

    private fun loadCategories() {
        AppData.loadCategories(
            onSuccess = { categories ->
                if (_binding == null) return@loadCategories
                binding.rvCategories.adapter = CategoryAdapter(categories) { category ->
                    val intent = Intent(requireContext(), SuppliersActivity::class.java).apply {
                        putExtra(SuppliersActivity.EXTRA_CATEGORY_NAME, category.name)
                    }
                    startActivity(intent)
                }
            },
            onError = { message ->
                if (_binding == null) return@loadCategories
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
