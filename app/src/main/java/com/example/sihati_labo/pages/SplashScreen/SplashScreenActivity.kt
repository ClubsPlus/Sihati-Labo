package com.example.sihati_labo.pages.SplashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.sihati_labo.R
import com.example.sihati_labo.databinding.ActivitySplashScreenBinding
import com.example.sihati_labo.pages.authPages.AuthActivity
import com.example.sihati_labo.pages.mainPage.MainActivity
import com.example.sihati_labo.viewmodels.AuthViewModel

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        )[AuthViewModel::class.java]

        Glide.with(this).load(R.drawable.logo_blue).into(binding.logo)

        Handler().postDelayed({
            if(viewModel.userData.value!= null){
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                startActivity(Intent(this, AuthActivity::class.java))
            }
            finish()
        },1000)

    }
}