package com.example.sihati_labo.pages.mainPage.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.R
import com.example.sihati_labo.adapters.ScheduleAdapter
import com.example.sihati_labo.databinding.FragmentScheduleBinding
import com.example.sihati_labo.pages.createSchedulePage.CreateScheduleActivity
import com.example.sihati_labo.pages.scheduleDetails.ScheduleDetailsActivity
import com.example.sihati_labo.viewmodels.ScheduleViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shrikanthravi.collapsiblecalendarview.data.Day
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ScheduleFragment : Fragment(), ScheduleAdapter.TaskClickInterface {

    private lateinit var binding: FragmentScheduleBinding
    private lateinit var scheduleAdapter:  ScheduleAdapter
    private lateinit var viewModel: ScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentScheduleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup the viewModel
         viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[ScheduleViewModel::class.java]

        /*setup the fab*/
        //change the fab icon color
        val myFabSrc = resources.getDrawable(R.drawable.add)
        myFabSrc.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
        binding.fab.setImageDrawable(myFabSrc)
        binding.fab.setOnClickListener { startActivity(Intent(requireActivity(), CreateScheduleActivity::class.java)) }


        val currentDate= LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val date = currentDate.format(formatter)
        recyclerViewSetup(date)
        setupCalendar(binding.calander)
    }

    private fun recyclerViewSetup(date: String) {
        // on below line we are setting layout
        // manager to our recycler view.
        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        // on below line we are initializing our adapter class.
        scheduleAdapter = ScheduleAdapter(requireActivity(), this)

        // on below line we are setting
        // adapter to our recycler view.
        binding.recyclerview.adapter = scheduleAdapter
        binding.recyclerview.setHasFixedSize(true)

        viewModel.subscribeToRealtimeUpdates(date,requireActivity(),scheduleAdapter)
    }

    private fun setupCalendar(collapsibleCalendar: CollapsibleCalendar){
        collapsibleCalendar.setCalendarListener(object : CollapsibleCalendar.CalendarListener {
            override fun onDayChanged() {

            }

            override fun onClickListener() {
                if(collapsibleCalendar.expanded){
                    collapsibleCalendar.collapse(400)
                }
                else{
                    collapsibleCalendar.expand(400)
                }
            }

            override fun onDaySelect() {
                val day: Day = collapsibleCalendar.selectedDay!!

                val thistoday = if(day.day<10) "0"+(day.day).toString() else day.day.toString()
                val thismonth= if(day.month + 1<10) "0"+(day.month+1).toString() else (day.month + 1).toString()
                val date = thistoday+"/"+thismonth+"/"+day.year

                viewModel.subscribeToRealtimeUpdates(date,requireActivity(),scheduleAdapter)
            }

            override fun onItemClick(v: View) {

            }

            override fun onDataUpdate() {

            }

            override fun onMonthChange() {

            }

            override fun onWeekChange(position: Int) {

            }
        })
    }

    override fun onClick(schedule: Schedule) {
        val intent = Intent(requireActivity(),ScheduleDetailsActivity::class.java)
        intent.putExtra("id",schedule.id)
        intent.putExtra("date",schedule.date)
        intent.putExtra("laboratory_id",schedule.laboratory_id)
        intent.putExtra("limite",schedule.limite.toString())
        intent.putExtra("person",schedule.person.toString())
        intent.putExtra("time_Start",schedule.time_Start)
        intent.putExtra("time_end",schedule.time_end)
        requireActivity().startActivity(intent)
    }

}