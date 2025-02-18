package com.example.sihati_labo.repositories

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.sihati_labo.Database.Laboratory
import com.example.sihati_labo.Database.User
import com.example.sihati_labo.pages.authPages.AuthActivity
import com.example.sihati_labo.pages.mainPage.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.toObject
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

    private var userCollectionRef = firestore.collection("User")
    var users: MutableLiveData<List<User>> = MutableLiveData<List<User>>()

    init {
        if (auth.currentUser != null) {
            firebaseUserMutableLiveData.postValue(auth.currentUser)
        }
        getUsers()
    }

    private fun getUsers(){
        val list  = ArrayList<User>()
        userCollectionRef
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, firebaseFirestoreException ->
                users.value = emptyList()
                list.clear()
                firebaseFirestoreException?.let{
                    Log.d("exeptions","error: "+it.message.toString())
                    return@addSnapshotListener
                }
                snapshot?.let{
                    for(document in it){
                        list.add(document.toObject())
                    }
                    users.value = list
                }
            }
    }

    fun register(email: String?, pass: String?,adresse: String,name: String,number: String,activity: Activity) {
        auth.createUserWithEmailAndPassword(email!!, pass!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser!!.uid
                auth.currentUser!!.sendEmailVerification()
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
                Toast.makeText(activity,"Compte créé avec succès",Toast.LENGTH_LONG).show()
            }
            signOut(activity)
        }catch (e: Exception){
            Log.d("exeptions","error: "+e.message.toString())
        }
    }

    fun login(email: String?, pass: String?,activity: Activity) {
        auth.signInWithEmailAndPassword(email!!, pass!!).addOnCompleteListener { task ->
            if (task.isSuccessful){
                if(auth.currentUser!!.isEmailVerified){
                    retrieveLaboratories(auth.currentUser!!,activity)
                    firebaseUserMutableLiveData.postValue(auth.currentUser)
                }else{
                    Toast.makeText(application, "Vérifier votre email", Toast.LENGTH_SHORT).show()
                    signOut(activity)
                }
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
                    withContext(Dispatchers.Main){
                        activity.startActivity(Intent(activity,MainActivity()::class.java))
                        activity.finish()
                    }
                    break
                }
            }
            if(succes==0){
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "l'email ou le mot de passe n'est pas correct", Toast.LENGTH_LONG).show()
                    signOut(activity)
                }
            }

        }catch (e:Exception){

        }
    }

    fun signOut(requireActivity: Activity) {
        auth.signOut()
        requireActivity.startActivity(Intent(requireActivity,AuthActivity::class.java))
    }
}