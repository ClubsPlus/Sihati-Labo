package com.example.sihati_labo.pages.mainPage.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sihati_labo.Database.Test
import com.example.sihati_labo.Database.User
import com.example.sihati_labo.R
import com.example.sihati_labo.adapters.PendingAdapter
import com.example.sihati_labo.databinding.FragmentPendingTestsBinding
import com.example.sihati_labo.notification.NotificationData
import com.example.sihati_labo.notification.PushNotification
import com.example.sihati_labo.notification.RetrofitInstance
import com.example.sihati_labo.viewmodels.AuthViewModel
import com.example.sihati_labo.viewmodels.ScheduleViewModel
import com.example.sihati_labo.viewmodels.TestViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PendingTestsFragment : Fragment(), PendingAdapter.SetOnClickInterface {

    private lateinit var binding: FragmentPendingTestsBinding
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var testViewModel: TestViewModel
    private lateinit var pendingAdapter : PendingAdapter

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
        pendingAdapter = PendingAdapter(requireActivity(),scheduleViewModel,this)

        // on below line we are setting
        // adapter to our recycler view.
        binding.recyclerView.adapter = pendingAdapter
        binding.recyclerView.setHasFixedSize(true)

        testViewModel.pendingTests?.observe(requireActivity()){ list ->
            list?.let {
                // on below line we are updating our list.
                pendingAdapter.updateList(it)
            }
        }
    }

    override fun onClick(test: Test) {
        testViewModel.sendNotificationToUserWithID(test,requireActivity())
    }
}