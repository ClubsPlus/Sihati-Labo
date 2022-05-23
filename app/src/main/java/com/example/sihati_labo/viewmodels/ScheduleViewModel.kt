package com.example.sihati_labo.viewmodels

import android.app.Activity
import android.widget.TextView
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

    fun getScheduleByIdAndSet(uid:String,date:TextView?=null,time:TextView?=null){
        mRepository.getScheduleByIdAndSet(uid,date,time)
    }

    fun updateSchedule(schedule: Schedule, newSchedule: Schedule){
        mRepository.updateSchedule(schedule,newSchedule)
    }

    fun getUserByIdAndSet(uid: String,name: TextView){
        mRepository.getUserByIdAndSet(uid,name)
    }

    fun deleteSchedule(schedule: Schedule){
        mRepository.deleteSchedule(schedule)
    }

    fun sendNotificationToUserWithSChedule(schedule: Schedule,title: String,text:String){
        mRepository.sendNotificationToUserWithSChedule(schedule,title,text)
    }

    fun editSchedule(oldSchedule: Schedule, newSchedule: Schedule, activity: Activity) {
        mRepository.editSchedule(oldSchedule,newSchedule,activity)
    }
}