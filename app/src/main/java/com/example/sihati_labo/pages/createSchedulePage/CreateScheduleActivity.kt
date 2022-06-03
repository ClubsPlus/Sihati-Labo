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
import com.example.sihati_labo.viewmodels.ScheduleViewModel
import com.shrikanthravi.collapsiblecalendarview.data.Day
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateScheduleBinding

    private var date: String = ""
    private lateinit var dateEdit: String
    private lateinit var startTime: String
    private lateinit var endTime: String
    private var edit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_schedule)
        binding = ActivityCreateScheduleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        if(intent.getStringExtra("id")!=null&&
            intent.getStringExtra("date")!=null&&
            intent.getStringExtra("laboratory_id")!=null&&
            intent.getStringExtra("limite")!=null&&
            intent.getStringExtra("person")!=null&&
            intent.getStringExtra("time_Start")!=null&&
            intent.getStringExtra("time_end")!=null){

            binding.title.text = "Modifier le rendez-vous"
            binding.add.text = "Modifier"

            edit = true

            binding.max.setText(intent.getStringExtra("limite"))

            binding.startTime.currentHour = intent.getStringExtra("time_Start").toString().dropLast(3).toInt()
            binding.startTime.currentMinute = intent.getStringExtra("time_Start").toString().drop(3).toInt()

            binding.endTime.currentHour = intent.getStringExtra("time_end").toString().dropLast(3).toInt()
            binding.endTime.currentMinute = intent.getStringExtra("time_end").toString().drop(3).toInt()

            date = intent.getStringExtra("date")!!
            dateEdit = intent.getStringExtra("date")!!
            binding.calander.selectedDay =
                Day(intent.getStringExtra("date").toString().drop(6).toInt(),
                intent.getStringExtra("date").toString().dropLast(5).drop(3).toInt(),
                intent.getStringExtra("date").toString().dropLast(8).toInt())
        }
        // Check if user is signed in (non-null) and update UI accordingly.
        val viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        )[ScheduleViewModel::class.java]

        binding.rollback.setOnClickListener { finish() }

        setCalendar(binding.calander)

        val currentDate= LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val today = currentDate.format(formatter)

        binding.add.setOnClickListener {
            if (date.isNotEmpty()
                &&getsTime(binding.startTime).isNotEmpty()
                &&getsTime(binding.endTime).isNotEmpty()
                &&binding.max.text.toString().isNotEmpty()){
                startTime = getsTime(binding.startTime)
                endTime = getsTime(binding.endTime)
                if(checkTime(startTime,endTime)){
                    if(!LocalDate.parse(date, formatter).isBefore(LocalDate.parse(today, formatter))){
                        if(!edit){
                            viewModel.saveSchedule(Schedule(date=date
                                ,laboratory_id=viewModel.auth.uid
                                ,limite=binding.max.text.toString().toInt()
                                ,time_Start=getsTime(binding.startTime)
                                ,time_end=getsTime(binding.endTime)),this)
                        }else{
                            val oldSchedule = Schedule(id=intent.getStringExtra("id")
                                ,date=dateEdit
                                ,laboratory_id=intent.getStringExtra("laboratory_id")
                                ,limite=intent.getStringExtra("limite")!!.toInt()
                                ,person=intent.getStringExtra("person")!!.toInt()
                                ,time_Start=intent.getStringExtra("time_Start")
                                ,time_end=intent.getStringExtra("time_end"))

                            val newSchedule = Schedule(id=intent.getStringExtra("id")
                                ,date=date
                                ,laboratory_id=intent.getStringExtra("laboratory_id")
                                ,limite=binding.max.text.toString().toInt()
                                ,time_Start=getsTime(binding.startTime)
                                ,time_end=getsTime(binding.endTime))

                            viewModel.editSchedule(oldSchedule,newSchedule,this)
                            viewModel.sendNotificationToUserWithSChedule(newSchedule,
                                "modification"
                                ,"Votre rendez-vous a ete modifié")
                        }
                        finish()
                    }else{
                        Toast.makeText(this,"veuillez insérer une date antérieure à aujourd'hui",Toast.LENGTH_LONG).show()
                    }
                }else Toast.makeText(this,"veuillez régler l'heure de fin après l'heure de début",Toast.LENGTH_SHORT).show()
            }else Toast.makeText(this,"remplissez vos champs svp",Toast.LENGTH_SHORT).show()
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

    private fun checkTime(startTime: String , endTime: String):Boolean{
        return startTime<endTime
    }
}