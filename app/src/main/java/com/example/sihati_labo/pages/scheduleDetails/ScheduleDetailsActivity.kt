package com.example.sihati_labo.pages.scheduleDetails

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.Database.Test
import com.example.sihati_labo.R
import com.example.sihati_labo.adapters.NotTestedAdapter
import com.example.sihati_labo.databinding.ActivityScheduleDetailsBinding
import com.example.sihati_labo.pages.createSchedulePage.CreateScheduleActivity
import com.example.sihati_labo.viewmodels.ScheduleViewModel
import com.example.sihati_labo.viewmodels.TestViewModel
import com.google.firebase.messaging.FirebaseMessaging
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ScheduleDetailsActivity : AppCompatActivity(), NotTestedAdapter.SetOnClickInterface {

    private lateinit var binding: ActivityScheduleDetailsBinding
    private lateinit var testViewModel: TestViewModel
    private lateinit var scheduleViewModel:  ScheduleViewModel
    private lateinit var notTestedAdapter: NotTestedAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_details)
        binding = ActivityScheduleDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        binding.rollback.setOnClickListener{
            finish()
        }
        if(intent.getStringExtra("id")!=null&&
            intent.getStringExtra("date")!=null&&
            intent.getStringExtra("laboratory_id")!=null&&
            intent.getStringExtra("limite")!=null&&
            intent.getStringExtra("person")!=null&&
            intent.getStringExtra("time_Start")!=null&&
            intent.getStringExtra("time_end")!=null){

            val id = intent.getStringExtra("id")
            val date = intent.getStringExtra("date")
            val laboratory_id = intent.getStringExtra("laboratory_id")
            val limite = intent.getStringExtra("limite")
            val person = intent.getStringExtra("person")
            val time_Start = intent.getStringExtra("time_Start")
            val time_end = intent.getStringExtra("time_end")

            binding.date.text = date
            binding.time.text = time_Start + " - " + time_end


            // setup the viewModel
            scheduleViewModel = ViewModelProvider(
                this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
            )[ScheduleViewModel::class.java]
            scheduleViewModel.init()

            // setup the viewModel
            testViewModel = ViewModelProvider(
                this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
            )[TestViewModel::class.java]

            binding.delete.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("suprimer")
                    .setMessage("Vous voulez vraiment suprimer le rendez-vous?")
                    .setPositiveButton("oui"
                    ) { _, _ ->
                        val schedule = Schedule(id,date,laboratory_id,limite!!.toInt(),person!!.toInt(),time_Start,time_end)

                        testViewModel.deleteTestsWithScheduleID(schedule.id!!)
                        scheduleViewModel.deleteSchedule(schedule)
                        scheduleViewModel.sendNotificationToUserWithSChedule(schedule,
                            "anulation"
                        ,"Votre rendez-vous est annuler")
                        finish()
                    }
                    .setNegativeButton("non", null).show()
            }

            binding.edit.setOnClickListener {
                val editIntent = Intent(this,CreateScheduleActivity::class.java)
                editIntent.putExtra("id",id)
                editIntent.putExtra("date",date)
                editIntent.putExtra("laboratory_id",laboratory_id)
                editIntent.putExtra("limite",limite)
                editIntent.putExtra("person",person)
                editIntent.putExtra("time_Start",time_Start)
                editIntent.putExtra("time_end",time_end)
                startActivity(editIntent)
            }
            recyclerViewSetup(intent.getStringExtra("id")!!)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun recyclerViewSetup(id:String) {
        // on below line we are setting layout
        // manager to our recycler view.
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        // on below line we are initializing our adapter class.
        notTestedAdapter = NotTestedAdapter(this,scheduleViewModel,this)

        // on below line we are setting
        // adapter to our recycler view.
        binding.recyclerView.adapter = notTestedAdapter
        binding.recyclerView.setHasFixedSize(true)

        testViewModel.getTestsWithScheduleId(id)
        testViewModel.testsWithId?.observe(this){ list ->
            list?.let {
                binding.person.text = "Il reste "+ it.size +" patient a tester"
                // on below line we are updating our list.
                notTestedAdapter.updateList(it)
            }
        }
    }

    override fun onClick(test: Test) {
        AlertDialog.Builder(this)
            .setTitle("fait")
            .setMessage("le test st vraiment fait?")
            .setPositiveButton("oui"
            ) { _, _ ->
                val oldTest = Test(laboratory_id=test.laboratory_id,
                    result=test.result,
                    user_id=test.user_id,
                    schedule_id=test.schedule_id)

                val finishDate= LocalDateTime.now().plusDays(7)
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val date = finishDate.format(formatter)

                val newTest = Test(date_end=date,
                    laboratory_id=test.laboratory_id,
                    result = "Pending",
                    user_id=test.user_id,
                    schedule_id=test.schedule_id)

                testViewModel.updateTest(oldTest,newTest)
            }
            .setNegativeButton("non", null).show()

    }
}