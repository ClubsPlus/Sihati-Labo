package com.example.sihati_labo.pages.authPages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.sihati_labo.viewmodels.AuthViewModel
import com.example.sihati_labo.R
import com.example.sihati_labo.databinding.ActivityLoginBinding
import com.example.sihati_labo.pages.mainPage.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Check if user is signed in (non-null) and update UI accordingly.
        val viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        )[AuthViewModel::class.java]

        viewModel.userData.observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        binding.signup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.login.setOnClickListener {
            if(binding.email.text.toString().isNotEmpty()
                &&binding.password.text.toString().isNotEmpty()){
                viewModel.signIn(binding.email.text.toString(),binding.password.text.toString(),this)
            }else Toast.makeText(this,"please fill your email and password",Toast.LENGTH_LONG).show()
        }
    }
}