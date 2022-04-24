package com.example.sihati_labo.repositories

import android.app.Activity
import android.app.Application
import android.util.Log
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
                        list.add(document.toObject())
                    }
                    schedules.value = list
                }
            }
    }

    fun getScheduleById(uid:String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = schedulesCollectionRef.document(uid).get().await()
            if (querySnapshot.toObject<Schedule>() != null) schedule = querySnapshot.toObject<Schedule>()
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("exeptions", e.message.toString())
            }
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

    fun getUserById(uid:String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val querySnapshot = usersCollectionRef.document(uid).get().await()
            if (querySnapshot.toObject<User>() != null) user = querySnapshot.toObject()
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("exeptions", e.message.toString())
            }
        }
    }
}
