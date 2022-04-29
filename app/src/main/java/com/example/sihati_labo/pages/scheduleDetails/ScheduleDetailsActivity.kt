package com.example.sihati_labo.pages.scheduleDetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sihati_labo.R
import com.example.sihati_labo.adapters.TestAdapter
import com.example.sihati_labo.databinding.ActivityScheduleDetailsBinding
import com.example.sihati_labo.viewmodels.ScheduleViewModel
import com.example.sihati_labo.viewmodels.TestViewModel



class ScheduleDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleDetailsBinding
    private lateinit var testViewModel: TestViewModel
    private lateinit var scheduleViewModel:  ScheduleViewModel
    private lateinit var testAdapter:  TestAdapter

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

            Log.d("test","I did receive data")
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
        testAdapter = TestAdapter(this,scheduleViewModel)

        // on below line we are setting
        // adapter to our recycler view.
        binding.recyclerView.adapter = testAdapter
        binding.recyclerView.setHasFixedSize(true)

        testViewModel.getTestsWithScheduleId(id)
        testViewModel.testsWithId?.observe(this){ list ->
            list?.let {
                // on below line we are updating our list.
                testAdapter.updateList(it)
            }
        }
    }
}