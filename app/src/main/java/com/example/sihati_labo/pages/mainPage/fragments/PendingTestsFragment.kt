package com.example.sihati_labo.pages.mainPage.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sihati_labo.viewmodels.AuthViewModel
import com.example.sihati_labo.R
import com.example.sihati_labo.adapters.TestAdapter
import com.example.sihati_labo.databinding.FragmentPendingTestsBinding
import com.example.sihati_labo.databinding.FragmentTestHistoryBinding
import com.example.sihati_labo.viewmodels.ScheduleViewModel
import com.example.sihati_labo.viewmodels.TestViewModel
import com.firebase.ui.auth.AuthUI

class PendingTestsFragment : Fragment() {


    private lateinit var binding: FragmentPendingTestsBinding
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var testViewModel: TestViewModel
    private lateinit var testAdapter : TestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPendingTestsBinding.inflate(inflater, container, false)
        return binding.root
     }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scheduleViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[ScheduleViewModel::class.java]

        testViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[TestViewModel::class.java]

        testViewModel.init()

        recyclerViewSetup()
    }

    private fun recyclerViewSetup() {
        // on below line we are setting layout
        // manager to our recycler view.
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        // on below line we are initializing our adapter class.
        testAdapter = TestAdapter(requireActivity(),scheduleViewModel)

        // on below line we are setting
        // adapter to our recycler view.
        binding.recyclerView.adapter = testAdapter
        binding.recyclerView.setHasFixedSize(true)

        testViewModel.pendingTests?.observe(requireActivity()){ list ->
            list?.let {
                // on below line we are updating our list.
                testAdapter.updateList(it)
            }
        }
    }
}