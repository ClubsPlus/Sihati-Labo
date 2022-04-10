package com.example.sihati_labo.repositories

import android.app.Activity
import android.app.Application
import android.widget.Toast
import com.example.sihati_labo.Database.Schedule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
}