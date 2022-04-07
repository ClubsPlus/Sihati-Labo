package com.example.sihati_labo.pages.authPages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sihati_labo.Database.Laboratory
import com.example.sihati_labo.R
import com.example.sihati_labo.databinding.ActivitySignupBinding
import com.example.sihati_labo.pages.mainPage.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private val laboratoryCollectionRef = Firebase.firestore.collection("Laboratory")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference

        auth = Firebase.auth

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signup.setOnClickListener {
            if(binding.name.text.toString().isNotEmpty()
                &&binding.number.text.toString().isNotEmpty()
                &&binding.adress.text.toString().isNotEmpty()
                &&binding.email.text.toString().isNotEmpty()
                &&binding.password.text.toString().isNotEmpty()){
                signup(binding.email.text.toString(),binding.password.text.toString())
            }else   Toast.makeText(this,"fill your fields plz",Toast.LENGTH_SHORT).show()
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

    private fun signup(email:String, password:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("test", "createUserWithEmail:success")

                    val user = auth.currentUser
                    val id = user!!.uid
                    saveLaboratory(
                        Laboratory( binding.adress.text.toString(),
                        binding.name.text.toString(),
                        binding.number.text.toString()),id)

                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("test", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun saveLaboratory(laboratory: Laboratory, id: String) = CoroutineScope(Dispatchers.IO).launch{
        try{
            laboratoryCollectionRef.document(id).set(laboratory).await()
            withContext(Dispatchers.Main){
                Toast.makeText(this@SignupActivity,"seccesufy saved data",Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(this@SignupActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

    }

}