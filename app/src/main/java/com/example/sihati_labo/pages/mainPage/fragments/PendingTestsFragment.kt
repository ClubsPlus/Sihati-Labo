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
    private lateinit var authViewModel: AuthViewModel
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

        authViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[AuthViewModel::class.java]

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
        FirebaseFirestore.getInstance().collection("User").document(test.user_id!!).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    setupDialog(test,document.toObject<User>()!!.token.toString())
                } else {
                    Log.d("exeptions", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("exeptions", "get failed with ", exception)
            }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            RetrofitInstance.api.postNotification(notification)
        }catch (e: Exception){
            Log.d("exeptions", "error: $e")
        }
    }

    private fun setupDialog(test: Test,token: String){
        /*setup the dialog*/
        val dialog_set_result = Dialog(requireActivity())
        dialog_set_result.setContentView(R.layout.set_result_dialog)
        dialog_set_result.window?.setBackgroundDrawable(getDrawable(requireContext(),R.drawable.back_round_white))

        val positive = dialog_set_result.findViewById<AppCompatButton>(R.id.positive)
        val negative = dialog_set_result.findViewById<AppCompatButton>(R.id.negative)

        positive.setOnClickListener {
            val oldTest = Test(test.date_end,test.laboratory_id,test.result,test.user_id,test.schedule_id)
            val newTest = Test(test.date_end,test.laboratory_id,"Positive",test.user_id,test.schedule_id)
            testViewModel.updateTest(oldTest,newTest)
            testViewModel.updateUser(test.user_id!!,"Positive")
            PushNotification(
                NotificationData("Resultat","Votre resultat est pret"),
                token
            ).also {
                sendNotification(it)
            }
            dialog_set_result.dismiss()
        }

        negative.setOnClickListener {
            val oldTest = Test(test.date_end,test.laboratory_id,test.result,test.user_id,test.schedule_id)
            val newTest = Test(test.date_end,test.laboratory_id,"Negative",test.user_id,test.schedule_id)
            testViewModel.updateTest(oldTest,newTest)
            testViewModel.updateUser(test.user_id!!,"Negative")
            PushNotification(
                NotificationData("Resultat","Votre resultat est pret"),
                token
            ).also {
                sendNotification(it)
            }
            dialog_set_result.dismiss()
        }

        dialog_set_result.show()
    }
}