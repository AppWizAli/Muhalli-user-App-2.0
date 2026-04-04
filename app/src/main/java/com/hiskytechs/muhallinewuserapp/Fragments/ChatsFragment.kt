package com.hiskytechs.muhallinewuserapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytechs.muhallinewuserapp.Adapters.ChatThreadAdapter
import com.hiskytechs.muhallinewuserapp.Data.AppData
import com.hiskytechs.muhallinewuserapp.databinding.FragmentChatsBinding

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvChats.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChats.adapter = ChatThreadAdapter(AppData.chatThreads)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
