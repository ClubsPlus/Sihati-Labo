package com.example.sihati_labo.repositories

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.sihati_labo.Database.Laboratory
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.Database.User
import com.example.sihati_labo.adapters.ScheduleAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
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
    private var schedulesCollectionRef = firestore.collection("Schedule")
    private var usersCollectionRef = firestore.collection("User")

    var schedule: Schedule? = null
    var user: User? = null

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        val currentDate= LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val date = currentDate.format(formatter)
        getSchedules(date)
    }

    fun getSchedules(date:String){
        val db = FirebaseFirestore.getInstance()
        val list  = ArrayList<Schedule>()
        db.collection("Schedule")
            .whereEqualTo("laboratory_id",auth.currentUser?.uid)
            .whereEqualTo("date",date)
            .orderBy("time_Start", Query.Direction.ASCENDING)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, firebaseFirestoreException ->
                schedules.value = emptyList()
                list.clear()
                firebaseFirestoreException?.let{
                    Log.d("exeptions","error: "+it.message.toString())
                    return@addSnapshotListener
                }
                snapshot?.let{
                    for(document in it){
                        val thisSchedule :Schedule= document.toObject()
                        thisSchedule.id = document.id
                        list.add(thisSchedule)
                    }
                    schedules.value = list
                }
            }
    }

    @SuppressLint("SetTextI18n")
    fun getScheduleByIdAndSet(uid:String, date:TextView?=null, time:TextView?=null) {
        schedulesCollectionRef.document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    date?.text = "${document.toObject<Schedule>()?.date}"
                    time?.text = "${document.toObject<Schedule>()?.time_Start} - ${document.toObject<Schedule>()?.time_end}"
                } else {
                    Log.d("exeptions", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("exeptions", "get failed with ", exception)
            }
    }

    fun updateSchedule(schedule: Schedule, newSchedule: Schedule) = CoroutineScope(Dispatchers.IO).launch {
        val scheduleQuery = schedulesCollectionRef
            .whereEqualTo("date",schedule.date)
            .whereEqualTo("laboratory_id",schedule.laboratory_id)
            .whereEqualTo("limite",schedule.limite)
            .whereEqualTo("person",schedule.person)
            .whereEqualTo("time_Start",schedule.time_Start)
            .whereEqualTo("time_end",schedule.time_end)
            .get()
            .await()
        if(scheduleQuery.documents.isNotEmpty()){
            for(document in scheduleQuery){
                try {
                    schedulesCollectionRef.document(document.id).set(
                        newSchedule,
                        SetOptions.merge()
                    ).await()
                }catch (e:Exception){
                    Log.d("exeptions","error: "+e.message.toString())
                }
            }
        }else{
            Log.d("exeptions","error: the retrieving query is empty")
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

    fun getUserByIdAndSet(uid:String, name: TextView){
        usersCollectionRef.document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    name.text = "${document.toObject<User>()?.name}"
                } else {
                    Log.d("exeptions", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("exeptions", "get failed with ", exception)
            }
    }
}
