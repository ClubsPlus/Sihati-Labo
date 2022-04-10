package com.example.sihati_labo.repositories

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.adapters.ScheduleAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ScheduleRepository(private val application: Application) {

    private var firestore = FirebaseFirestore.getInstance()
    var schedulesCollectionRef = firestore.collection("Schedule")
    val auth: FirebaseAuth = FirebaseAuth.getInstance()


    fun saveSchedule(schedule: Schedule,activity: Activity) = CoroutineScope(Dispatchers.IO).launch{
        try{
            schedulesCollectionRef.add(schedule).await()
            withContext(Dispatchers.Main){
                Toast.makeText(activity,"successfully saved data", Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun subscribeToRealtimeUpdates(date:String,activity: Activity,adapter: ScheduleAdapter){
        schedulesCollectionRef.whereEqualTo("laboratory_id",auth.uid).whereEqualTo("date",date).orderBy("time_Start", Query.Direction.ASCENDING)
            .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let{
                    Toast.makeText(activity,it.message,Toast.LENGTH_LONG).show()
                    Log.d("test",it.message.toString())
                    return@addSnapshotListener
                }
                querySnapshot?.let{
                    val schedules  = mutableListOf<Schedule>()
                    for (document in it){
                        val schedule = document.toObject<Schedule>()
                        schedule.id = document.id
                        schedules.add(schedule)
                    }
                    adapter.updateList(schedules)
                }
            }
    }
}