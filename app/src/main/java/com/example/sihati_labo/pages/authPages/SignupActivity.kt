package com.example.sihati_labo.pages.authPages

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sihati_labo.viewmodels.AuthViewModel
import com.example.sihati_labo.R
import com.example.sihati_labo.databinding.ActivitySignupBinding
import com.example.sihati_labo.pages.mainPage.MainActivity


class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        // Check if user is signed in (non-null) and update UI accordingly.
        val viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        )[AuthViewModel::class.java]

        viewModel.userData.observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.signup.setOnClickListener {
            if(binding.name.text.toString().isNotEmpty()
                &&binding.number.text.toString().isNotEmpty()
                &&binding.adress.text.toString().isNotEmpty()
                &&binding.email.text.toString().isNotEmpty()
                &&binding.password.text.toString().isNotEmpty()){
                viewModel.register(binding.email.text.toString(),
                    binding.password.text.toString(),
                    binding.adress.text.toString(),
                    binding.name.text.toString(),
                    binding.number.text.toString(),
                    this)
            }else   Toast.makeText(this,"fill your fields plz",Toast.LENGTH_SHORT).show()
        }
    }
}