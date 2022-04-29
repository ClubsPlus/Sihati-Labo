package com.example.sihati_labo.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.Database.User
import com.example.sihati_labo.repositories.ScheduleRepository

class ScheduleViewModel : ViewModel() {

    private val mRepository = ScheduleRepository()
    val auth = mRepository.auth
    var schedules: MutableLiveData<List<Schedule>>? = null
    var schedule: Schedule? = null
    var user: User? = null

    fun init() {
        schedules = mRepository.schedules
    }

    fun updateScheduleWithDate(date:String){
        schedules?.value = emptyList()
        mRepository.getSchedules(date)
    }

    fun saveSchedule(schedule: Schedule, activity: Activity){
        mRepository.saveSchedule(schedule,activity)
    }

    fun getScheduleById(uid:String){
        mRepository.getScheduleById(uid)
        schedule = mRepository.schedule
    }

    fun updateSchedule(schedule: Schedule, newSchedule: Schedule){
        mRepository.updateSchedule(schedule,newSchedule)
    }

    fun getUserById(uid:String){
        mRepository.getUserById(uid)
        user = mRepository.user
    }
}