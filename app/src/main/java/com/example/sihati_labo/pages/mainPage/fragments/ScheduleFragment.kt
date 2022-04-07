package com.example.sihati_labo.pages.mainPage.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.R
import com.example.sihati_labo.adapters.ScheduleAdapter
import com.example.sihati_labo.pages.createSchedulePage.CreateScheduleActivity
import com.example.sihati_labo.databinding.FragmentScheduleBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class ScheduleFragment : Fragment(), ScheduleAdapter.TaskClickInterface {

    private lateinit var binding: FragmentScheduleBinding
    private lateinit var currentUserRef: Query

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentScheduleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //change the fab icon color
        val myFabSrc = resources.getDrawable(R.drawable.add)
        myFabSrc.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
        binding.fab.setImageDrawable(myFabSrc)

        binding.fab.setOnClickListener {
            startActivity(Intent(requireActivity(), CreateScheduleActivity::class.java))
        }
        val user = Firebase.auth.currentUser
        user?.let{
            val id = it.uid
            currentUserRef = Firebase.firestore.collection("Schedule").whereEqualTo("laboratory_id",id)
        }

        recyclerViewSetup()


    }

    private fun recyclerViewSetup(){
        // on below line we are setting layout
        // manager to our recycler view.
        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        // on below line we are initializing our adapter class.
        val scheduleAdapter = ScheduleAdapter(requireActivity(), this)

        // on below line we are setting
        // adapter to our recycler view.
        binding.recyclerview.adapter = scheduleAdapter
        binding.recyclerview.setHasFixedSize(true)

        subscribeToRealtimeUpdates(scheduleAdapter)
    }

    private fun subscribeToRealtimeUpdates(adapter: ScheduleAdapter){
        currentUserRef.addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let{
                Toast.makeText(requireActivity(),it.message,Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }
            querySnapshot?.let{
                val schedules  = mutableListOf<Schedule>()
                for (document in it){
                    val schedule = document.toObject<Schedule>()
                    schedules.add(schedule)
                }
                adapter.updateList(schedules)
            }
        }
    }

    override fun onClick(task: Schedule) {

    }
}