package com.example.sihati_client.repositories

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.sihati_labo.Database.Laboratory
import com.example.sihati_labo.pages.authPages.LoginActivity
import com.example.sihati_labo.pages.mainPage.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AuthenticationRepository(private val application: Application) {
    val firebaseUserMutableLiveData: MutableLiveData<FirebaseUser?> = MutableLiveData()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private var firestore = FirebaseFirestore.getInstance()
    private var laboratoryCollectionRef = firestore.collection("Laboratory")

    fun register(email: String?, pass: String?,adresse: String,name: String,number: String,activity: Activity) {
        auth.createUserWithEmailAndPassword(email!!, pass!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser!!.uid
                saveLaboratory(Laboratory(adresse,name,number),uid,activity)
            }else{
                Toast.makeText(application, task.exception?.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun saveLaboratory(laboratory: Laboratory, uid: String, activity: Activity) = CoroutineScope(Dispatchers.IO).launch{
        try{
            laboratoryCollectionRef.document(uid).set(laboratory).await()
            withContext(Dispatchers.Main){
                Toast.makeText(activity,"account created seccesufy",Toast.LENGTH_LONG).show()
                val mainActivity = MainActivity()
                activity.startActivity(Intent(activity,mainActivity::class.java))
            }
        }catch (e: Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun login(email: String?, pass: String?,activity: Activity) {
        auth.signInWithEmailAndPassword(email!!, pass!!).addOnCompleteListener { task ->
            if (task.isSuccessful){
                retrieveLaboratories(auth.currentUser!!,activity)
                firebaseUserMutableLiveData.postValue(auth.currentUser)
            } else {
                Toast.makeText(application, task.exception?.message  , Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun retrieveLaboratories(user: FirebaseUser,activity: Activity) = CoroutineScope(Dispatchers.IO).launch {
        try {
            var succes = 0
            val querySnapshot = laboratoryCollectionRef.get().await()
            for(document in querySnapshot.documents){
                if(document.id==user.uid){
                    succes = 1
                    firebaseUserMutableLiveData.postValue(auth.currentUser)
                    break
                }
            }
            if(succes==0){
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "email or password is not correct", Toast.LENGTH_LONG).show()
                    signOut(activity)
                }
            }

        }catch (e:Exception){

        }
    }

    fun signOut(requireActivity: Activity) {
        val loginActivity = LoginActivity()
        auth.signOut()
        requireActivity.startActivity(Intent(requireActivity,loginActivity::class.java))
    }

    init {
        if (auth.currentUser != null) {
            firebaseUserMutableLiveData.postValue(auth.currentUser)
        }
    }
}