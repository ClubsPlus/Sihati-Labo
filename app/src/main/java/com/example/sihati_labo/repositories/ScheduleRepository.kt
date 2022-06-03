package com.example.sihati_labo.repositories

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.Database.User
import com.example.sihati_labo.notification.NotificationData
import com.example.sihati_labo.notification.PushNotification
import com.example.sihati_labo.notification.RetrofitInstance
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

    fun saveSchedule(schedule: Schedule,activity: Activity) = CoroutineScope(Dispatchers.IO).launch{
        try{
            schedulesCollectionRef.add(schedule).addOnSuccessListener {
                schedule.id = it.id
                it.set(
                    schedule,
                    SetOptions.merge()
                )
            }
            withContext(Dispatchers.Main){
                Toast.makeText(activity,"données enregistrées avec succès", Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            Log.d("exeptions","error: "+e.message.toString())
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

    fun deleteSchedule(schedule: Schedule){
        schedulesCollectionRef
            .whereEqualTo("id", schedule.id)
            .whereEqualTo("date", schedule.date)
            .whereEqualTo("laboratory_id", schedule.laboratory_id)
            .whereEqualTo("limite", schedule.limite)
            .whereEqualTo("person", schedule.person)
            .whereEqualTo("time_Start", schedule.time_Start)
            .whereEqualTo("time_end", schedule.time_end)
            .get().addOnSuccessListener {
                for (document in it) {
                    schedulesCollectionRef.document(document.id).delete()
                }
            }
    }

    fun sendNotificationToUserWithSChedule(schedule: Schedule,title:String,text:String){
        PushNotification(
            NotificationData(title,text),
            "/topics/"+schedule.id!!
        ).also {
            sendNotification(it)
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            RetrofitInstance.api.postNotification(notification)
        }catch (e: Exception) {
        }
    }

    fun editSchedule(oldSchedule: Schedule, newSchedule: Schedule, activity: Activity) = CoroutineScope(Dispatchers.IO).launch {
        schedulesCollectionRef
            .whereEqualTo("id",oldSchedule.id)
            .whereEqualTo("date",oldSchedule.date)
            .whereEqualTo("laboratory_id",oldSchedule.laboratory_id)
            .whereEqualTo("limite",oldSchedule.limite)
            .whereEqualTo("person",oldSchedule.person)
            .whereEqualTo("time_Start",oldSchedule.time_Start)
            .whereEqualTo("time_end",oldSchedule.time_end)
            .get().addOnSuccessListener { query->
                Log.d("edit","get done")
                if(query.documents.isNotEmpty()){
                    Log.d("edit","not empty")
                    for(document in query){
                        try {
                            schedulesCollectionRef.document(document.id).set(
                                newSchedule,
                                SetOptions.merge()
                            ).addOnSuccessListener {
                                Log.d("edit","edit done")

                            }
                            Toast.makeText(activity,"le rendez-vous est modifier", Toast.LENGTH_LONG).show()
                        }catch (e:Exception){
                            Log.d("edit","error: "+e.message.toString())
                        }
                    }
                }else{
                    Log.d("edit","error: the retrieving query is empty")
                }
            }
    }
}
