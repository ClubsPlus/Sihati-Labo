package com.example.sihati_labo.pages.authPages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sihati_labo.R
import com.example.sihati_labo.databinding.ActivityLoginBinding
import com.example.sihati_labo.pages.mainPage.MainActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth;
    private val laboratoryCollectionRef = Firebase.firestore.collection("Laboratory")
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        binding.signup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.login.setOnClickListener {
            if(binding.email.text.toString().isNotEmpty()
                &&binding.password.text.toString().isNotEmpty()){
                login(binding.email.text.toString(),binding.password.text.toString())
            }else Toast.makeText(this,"please fill your email and password",Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun login(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("test", "signInWithEmail:success")
                    user = auth.currentUser!!
                    retrieveLaboratories(user,user.uid)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("test", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun retrieveLaboratories(user: FirebaseUser, uid: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            var succes = 0
            val querySnapshot = laboratoryCollectionRef.get().await()
            for(document in querySnapshot.documents){
                if(document.id==uid){
                    succes = 1
                    updateUI(user)
                    break
                }
            }
            if(succes==0){
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "email or password is not correct", Toast.LENGTH_LONG).show()
                    AuthUI.getInstance()
                        .signOut(this@LoginActivity)
                        .addOnCompleteListener {
                            updateUI(null)
                        }
                }
            }

        }catch (e:Exception){

        }
    }
}