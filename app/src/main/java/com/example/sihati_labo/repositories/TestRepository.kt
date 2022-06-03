package com.example.sihati_labo.repositories

import android.app.Activity
import android.app.Dialog
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.MutableLiveData
import com.example.sihati_labo.Database.Test
import com.example.sihati_labo.Database.User
import com.example.sihati_labo.R
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
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

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
                        result,
                        userQuery.toObject<User>()!!.token),
                    SetOptions.merge()
                ).await()
            }catch (e:Exception){
                Log.d("exeptions","error: "+e.message.toString())
            }

        }else{
            Log.d("exeptions","error: the retrieving query is empty")
        }
    }

    fun sendNotificationToUserWithID(test: Test,activity: Activity){
        FirebaseFirestore.getInstance().collection("User").document(test.user_id!!).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    setupDialog(test,document.toObject<User>()!!.token.toString(),activity)
                } else {
                    Log.d("exeptions", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("exeptions", "get failed with ", exception)
            }
    }

    private fun setupDialog(test: Test,token: String,activity: Activity){
        val dialog_set_result = Dialog(activity)
        dialog_set_result.setContentView(R.layout.set_result_dialog)
        dialog_set_result.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                activity,
                R.drawable.back_round_white
            )
        )

        val positive = dialog_set_result.findViewById<AppCompatButton>(R.id.positive)
        val negative = dialog_set_result.findViewById<AppCompatButton>(R.id.negative)

        positive.setOnClickListener {
            val oldTest = Test(test.date_end,test.laboratory_id,test.result,test.user_id,test.schedule_id)
            val newTest = Test(test.date_end,test.laboratory_id,"Positive",test.user_id,test.schedule_id)
            updateTest(oldTest,newTest)
            updateUser(test.user_id!!,"Positive")
            PushNotification(
                NotificationData("Resultat","Votre resultat est pret"),
                token
            ).also {
                sendNotification(it)
            }
            sendEmail()
            dialog_set_result.dismiss()
        }

        negative.setOnClickListener {
            val oldTest = Test(test.date_end,test.laboratory_id,test.result,test.user_id,test.schedule_id)
            val newTest = Test(test.date_end,test.laboratory_id,"Negative",test.user_id,test.schedule_id)
            updateTest(oldTest,newTest)
            updateUser(test.user_id!!,"Negative")
            PushNotification(
                NotificationData("Resultat","Votre resultat est pret"),
                token
            ).also {
                sendNotification(it)
            }
            dialog_set_result.dismiss()
        }

        dialog_set_result.show()
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            RetrofitInstance.api.postNotification(notification)
        }catch (e: Exception){
            Log.d("exeptions", "error: $e")
        }
    }

    private fun sendEmail() = CoroutineScope(Dispatchers.IO).launch {
        val props = Properties()
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.port"] = "587"

        val session = Session.getInstance(props,
            object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(
                        "sihatiAlgeria@gmail.com",
                        "pL8H45BzkXNo")
                }
            }
        )
        try {
            val message:Message = MimeMessage(session)
            message.setFrom(InternetAddress("sihatiAlgeria@gmail.com"))
            message.setRecipient(Message.RecipientType.TO,
                InternetAddress("houssembababendermel@gmail.com"))
            message.subject = "testing sending the email"
            message.setText("this is a test from me to me")
            Transport.send(message)
            Log.d("mail","email has been sent")
        }catch (e:MessagingException){
            throw RuntimeException(e)
        }
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun deleteTestsWithScheduleID(id: String) {
        testCollectionRef
            .whereEqualTo("schedule_id", id)
            .get().addOnSuccessListener {
                for (document in it) {
                    if(document.toObject<Test>().result=="Not Tested" ||
                        document.toObject<Test>().result=="Pending"){
                        testCollectionRef.document(document.id).delete()
                    }
                }
            }
    }
}