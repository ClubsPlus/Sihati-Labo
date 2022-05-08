package com.example.sihati_labo.pages.authPages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.sihati_labo.R
import com.example.sihati_labo.databinding.ActivityAuthBinding
import com.example.sihati_labo.pages.authPages.adapter.SectionsPagerAdapter
import com.example.sihati_labo.pages.mainPage.MainActivity
import com.example.sihati_labo.viewmodels.AuthViewModel
import com.google.android.material.tabs.TabLayout

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        val viewPager: ViewPager = binding.viewPager
        val tabs: TabLayout = binding.tabLayout
        tabs.setupWithViewPager(viewPager)
        viewPager.adapter = sectionsPagerAdapter

        // Check if user is signed in (non-null) and update UI accordingly.
        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        )[AuthViewModel::class.java]
    }
}