package com.example.sihati_labo.pages.createSchedulePage

import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.R
import com.example.sihati_labo.databinding.ActivityCreateScheduleBinding
import com.example.sihati_labo.viewmodels.AuthViewModel
import com.example.sihati_labo.viewmodels.ScheduleViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shrikanthravi.collapsiblecalendarview.data.Day
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CreateScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateScheduleBinding

    private lateinit var date: String
    private lateinit var startTime: String
    private lateinit var endTime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_schedule)
        binding = ActivityCreateScheduleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        // Check if user is signed in (non-null) and update UI accordingly.
        val viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        )[ScheduleViewModel::class.java]

        binding.rollback.setOnClickListener { finish() }
        setCalendar(binding.calander)
        binding.add.setOnClickListener {
            if (date.isNotEmpty()
                &&getsTime(binding.startTime).isNotEmpty()
                &&getsTime(binding.endTime).isNotEmpty()
                &&binding.max.text.toString().isNotEmpty()){
                startTime = getsTime(binding.startTime)
                endTime = getsTime(binding.endTime)
                viewModel.saveSchedule(Schedule(date=date
                    ,laboratory_id=viewModel.auth.uid
                    ,limite=binding.max.text.toString().toInt()
                    ,time_Start=getsTime(binding.startTime)
                    ,time_end=getsTime(binding.endTime)),this)
                finish()
            }else Toast.makeText(this,"Please fill all the fields",Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCalendar(collapsibleCalendar: CollapsibleCalendar){
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

                date=thistoday+"/"+thismonth+"/"+day.year
                Toast.makeText(this@CreateScheduleActivity,date,Toast.LENGTH_SHORT).show()
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

    private fun getsTime(timePicker: TimePicker): String {
        val minute = if(timePicker.minute<10) "0"+timePicker.minute.toString() else timePicker.minute.toString()
        val hour= if(timePicker.hour<10) "0"+timePicker.hour.toString() else timePicker.hour.toString()
        return hour+":"+minute
    }
}