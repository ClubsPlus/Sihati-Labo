package com.example.sihati_labo.repositories

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.adapters.ScheduleAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleRepository {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var schedules: MutableLiveData<List<Schedule>> = MutableLiveData<List<Schedule>>()
    var schedulesCollectionRef = firestore.collection("Schedule")

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        val currentDate= LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val date = currentDate.format(formatter)
        getSchedules(date)
    }

    fun getSchedules(date:String){
        Log.d("test","I'm in the getSchedules")
        val db = FirebaseFirestore.getInstance()
        val list  = ArrayList<Schedule>()
        db.collection("Schedule")
            .whereEqualTo("laboratory_id",auth.currentUser?.uid)
            .whereEqualTo("date",date)
            .orderBy("time_Start", Query.Direction.ASCENDING)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let{
                    Log.d("exeptions","error: "+it.message.toString())
                    return@addSnapshotListener
                }
                snapshot?.let{
                    schedules.value = emptyList()
                    for(document in it){
                        list.add(document.toObject())
                    }
                    Log.d("test","after cleaning the list in the repository size="+ schedules?.value?.size.toString())
                    schedules.value = list
                    Log.d("test","I'm done with seting the schedule in the repository ")
                }
            }
    }


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
