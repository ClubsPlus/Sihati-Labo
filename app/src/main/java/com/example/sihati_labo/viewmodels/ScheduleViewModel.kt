package com.example.sihati_labo.viewmodels

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.repositories.ScheduleRepository
import com.google.firebase.firestore.FirebaseFirestore

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ScheduleRepository = ScheduleRepository(application)

    private var firestore = FirebaseFirestore.getInstance()
    private var schedulesCollectionRef = repository.schedulesCollectionRef
    val auth = repository.auth

    fun saveSchedule(schedule: Schedule, activity: Activity){
        repository.saveSchedule(schedule,activity)
    }

}