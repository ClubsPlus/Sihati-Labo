package com.example.sihati_labo.pages.scheduleDetails

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.Database.Test
import com.example.sihati_labo.R
import com.example.sihati_labo.adapters.NotTestedAdapter
import com.example.sihati_labo.databinding.ActivityScheduleDetailsBinding
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
                        val schedule = Schedule(intent.getStringExtra("id"),
                            intent.getStringExtra("date"),
                            intent.getStringExtra("laboratory_id"),
                            intent.getStringExtra("limite")!!.toInt(),
                            intent.getStringExtra("person")!!.toInt(),
                            intent.getStringExtra("time_Start"),
                            intent.getStringExtra("time_end")
                        )

                        testViewModel.deleteTestsWithScheduleID(schedule.id!!)
                        scheduleViewModel.deleteSchedule(schedule)
                        scheduleViewModel.sendNotificationToUserWithSChedule(schedule,this)
                    }
                    .setNegativeButton("non", null).show()
            }

            binding.date.text = intent.getStringExtra("date")
            binding.time.text = "${intent.getStringExtra("time_Start")} - ${intent.getStringExtra("time_end")}"
            val rest = intent.getStringExtra("limite")!!.toInt() - intent.getStringExtra("person")!!.toInt()
            binding.person.text = "Il reste $rest patient a tester"

            recyclerViewSetup(intent.getStringExtra("id")!!)
        }
    }

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
                // on below line we are updating our list.
                notTestedAdapter.updateList(it)
            }
        }
    }

    override fun onClick(test: Test) {
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
}