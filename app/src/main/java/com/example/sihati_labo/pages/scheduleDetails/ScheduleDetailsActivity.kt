package com.example.sihati_labo.pages.scheduleDetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sihati_labo.R
import com.example.sihati_labo.databinding.ActivityScheduleDetailsBinding

private lateinit var binding: ActivityScheduleDetailsBinding


class ScheduleDetailsActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_details)
        binding = ActivityScheduleDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        if(intent.getStringExtra("id")!=null&&
            intent.getStringExtra("date")!=null&&
            intent.getStringExtra("limite")!=null&&
            intent.getStringExtra("person")!=null&&
            intent.getStringExtra("time_Start")!=null&&
            intent.getStringExtra("time_end")!=null){
            Log.d("test","I did receive data")
            binding.date.text = intent.getStringExtra("date")
            binding.time.text = "${intent.getStringExtra("time_Start")} - ${intent.getStringExtra("time_end")}"
            val rest = intent.getStringExtra("limite")!!.toInt() - intent.getStringExtra("person")!!.toInt()
            binding.person.text = "Il reste $rest patient a tester"
        }
    }
}