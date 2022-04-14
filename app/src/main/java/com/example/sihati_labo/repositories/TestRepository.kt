package com.example.sihati_labo.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.sihati_labo.Database.Test
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

class TestRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    var tests: MutableLiveData<List<Test>> = MutableLiveData<List<Test>>()

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        getTests()
    }

    fun getTestsWithDate(date:String){
        Log.d("test","I'm in the getSchedules")
        val db = FirebaseFirestore.getInstance()
        val list  = ArrayList<Test>()
        db.collection("Schedule").whereEqualTo("date",date)
            .orderBy("time_Start", Query.Direction.ASCENDING)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let{
                    Log.d("exeptions","error: "+it.message.toString())
                    return@addSnapshotListener
                }
                snapshot?.let{
                    tests.value = emptyList()
                    for(document in it){
                        list.add(document.toObject())
                    }
                    Log.d("test","after cleaning the list in the repository size="+ tests?.value?.size.toString())
                    tests.value = list
                    Log.d("test","I'm done with seting the schedule in the repository ")
                }
            }
    }

    fun getTests(){
        val db = FirebaseFirestore.getInstance()
        val list  = ArrayList<Test>()
        val ref = auth.currentUser?.let { db.collection("Test")
            .whereEqualTo("user_id", auth.currentUser!!.uid)
            .whereEqualTo("result","Positive")
            .whereEqualTo("result","Negative")}
        ref?.addSnapshotListener { snapshot, firebaseFirestoreException ->
            firebaseFirestoreException?.let{
                Log.d("exeptions","error: "+it.message.toString())
                return@addSnapshotListener
            }
            snapshot?.let{
                tests.value = emptyList()
                for(document in it){
                    list.add(document.toObject())
                }
                tests.value = list
            }
        }
    }
}