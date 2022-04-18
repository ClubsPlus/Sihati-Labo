package com.example.sihati_labo.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.repositories.ScheduleRepository

class ScheduleViewModel : ViewModel() {

    private val mRepository = ScheduleRepository()
    val auth = mRepository.auth
    var schedules: MutableLiveData<List<Schedule>>? = null

    fun init() {
        schedules = mRepository.schedules
    }

    fun updateScheduleWithDate(date:String){
        Log.d("test","I'm in the updateScheduleWithDate viewmodel")
        schedules?.value = emptyList()
        Log.d("test","after cleaning the list in the viewmodel size="+ schedules?.value?.size.toString())
        mRepository.getSchedules(date)
    }

    fun saveSchedule(schedule: Schedule, activity: Activity){
        mRepository.saveSchedule(schedule,activity)
    }
}