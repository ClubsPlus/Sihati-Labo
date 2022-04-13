package com.example.sihati_labo.viewmodels

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.sihati_labo.repositories.AuthenticationRepository
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AuthenticationRepository = AuthenticationRepository(application)
    val userData: MutableLiveData<FirebaseUser?> = repository.firebaseUserMutableLiveData

    fun register(email: String?, pass: String?,adresse: String,name: String,number: String,activity: Activity) {
        repository.register(email, pass,adresse,name,number,activity)
    }

    fun signIn(email: String?, pass: String?,activity: Activity) {
        repository.login(email, pass, activity)
    }

    fun signOut(requireActivity: Activity) {
        repository.signOut(requireActivity)
    }

}