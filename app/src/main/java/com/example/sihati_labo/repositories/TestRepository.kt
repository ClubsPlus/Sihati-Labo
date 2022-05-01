package com.example.sihati_labo.repositories

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.sihati_labo.Database.Test
import com.example.sihati_labo.Database.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TestRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var testCollectionRef = firestore.collection("Test")
    var userCollectionRef = firestore.collection("User")

    var tests: MutableLiveData<List<Test>> = MutableLiveData<List<Test>>()
    var testsWithId: MutableLiveData<List<Test>> = MutableLiveData<List<Test>>()
    var testsReady: MutableLiveData<List<Test>> = MutableLiveData<List<Test>>()
    var pendingTests: MutableLiveData<List<Test>> = MutableLiveData<List<Test>>()

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        getTestsReady()
        getPendingTests()
    }

    private fun getTestsReady() {
        val db = FirebaseFirestore.getInstance()
        val list = ArrayList<Test>()
        val ref = auth.currentUser?.let {
            db.collection("Test")
                .whereEqualTo("laboratory_id", auth.currentUser!!.uid)
        }
        ref?.addSnapshotListener { snapshot, firebaseFirestoreException ->
            list.clear()
            testsReady.value = emptyList()
            firebaseFirestoreException?.let {
                Log.d("exeptions", "error: " + it.message.toString())
                return@addSnapshotListener
            }
            snapshot?.let {
                for (document in it) {
                    if (document.toObject<Test>().result == "Positive" || document.toObject<Test>().result == "Negative")
                        list.add(document.toObject())
                }
                testsReady.value = list
            }
        }
    }

    private fun getPendingTests() {
        val db = FirebaseFirestore.getInstance()
        val list = ArrayList<Test>()
        val ref = auth.currentUser?.let {
            db.collection("Test")
                .whereEqualTo("laboratory_id", auth.currentUser!!.uid)
                .whereEqualTo("result","Pending")
        }
        ref?.addSnapshotListener { snapshot, firebaseFirestoreException ->
            list.clear()
            firebaseFirestoreException?.let {
                Log.d("exeptions", "error: " + it.message.toString())
                return@addSnapshotListener
            }
            snapshot?.let {
                for (document in it) {
                    list.add(document.toObject())
                }
                pendingTests.value = list
            }
        }
    }

    fun getTestsWithDate(date: String) {
        val list = ArrayList<Test>()
        testCollectionRef.whereEqualTo("date", date)
            .orderBy("time_Start", Query.Direction.ASCENDING)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, firebaseFirestoreException ->
                tests.value = emptyList()
                list.clear()
                firebaseFirestoreException?.let {
                    Log.d("exeptions", "error: " + it.message.toString())
                    return@addSnapshotListener
                }
                snapshot?.let {
                    tests.value = emptyList()
                    for (document in it) {
                        if (document.toObject<Test>().result == "Positive" || document.toObject<Test>().result == "Negative")
                            list.add(document.toObject())
                    }
                    tests.value = list
                }
            }
    }

    fun getTestsWithScheduleId(id: String) {
        val list = ArrayList<Test>()
        testCollectionRef
            .whereEqualTo("schedule_id", id)
            .whereEqualTo("result","Not Tested")
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, firebaseFirestoreException ->
                testsWithId.value = emptyList()
                list.clear()
                firebaseFirestoreException?.let {
                    Log.d("exeptions", "error: " + it.message.toString())
                    return@addSnapshotListener
                }
                snapshot?.let {
                    testsWithId.value = emptyList()
                    for (document in it) {
                        list.add(document.toObject())
                    }
                    testsWithId.value = list
                    Log.d("test",testsWithId.value?.size.toString())
                }
            }
    }

    fun createTest(test: Test, activity: Activity) = CoroutineScope(Dispatchers.IO).launch {
        try {
            testCollectionRef.add(test).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, "successfully saved data", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun updateTest(test: Test, newTest: Test) = CoroutineScope(Dispatchers.IO).launch {
        val scheduleQuery = testCollectionRef
            .whereEqualTo("laboratory_id",test.laboratory_id)
            .whereEqualTo("result",test.result)
            .whereEqualTo("schedule_id",test.schedule_id)
            .whereEqualTo("user_id",test.user_id)
            .get()
            .await()
        if(scheduleQuery.documents.isNotEmpty()){
            for(document in scheduleQuery){
                try {
                    testCollectionRef.document(document.id).set(
                        newTest,
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

    fun updateUser(uid: String, result: String) = CoroutineScope(Dispatchers.IO).launch {
        val userQuery = userCollectionRef.document(uid).get().await()
        if(userQuery!=null){
            try {
                userCollectionRef.document(uid).set(
                    User(userQuery.toObject<User>()!!.id,
                        userQuery.toObject<User>()!!.name,
                        userQuery.toObject<User>()!!.number,
                        result),
                    SetOptions.merge()
                ).await()
            }catch (e:Exception){
                Log.d("exeptions","error: "+e.message.toString())
            }

        }else{
            Log.d("exeptions","error: the retrieving query is empty")
        }
    }
}