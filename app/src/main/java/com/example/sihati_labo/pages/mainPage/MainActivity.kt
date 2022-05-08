package com.example.sihati_labo.pages.mainPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sihati_labo.viewmodels.AuthViewModel
import com.example.sihati_labo.R
import com.example.sihati_labo.pages.authPages.AuthActivity
import com.example.sihati_labo.pages.mainPage.fragments.PendingTestsFragment
import com.example.sihati_labo.pages.mainPage.fragments.ScheduleFragment
import com.example.sihati_labo.pages.mainPage.fragments.TestHistoryFragment
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainActivity : AppCompatActivity() {

    lateinit var fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // Check if user is signed in (non-null) and update UI accordingly.
        val viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        )[AuthViewModel::class.java]

        viewModel.userData.observe(this) { firebaseUser ->
            if (firebaseUser == null) {
                startActivity(Intent(this, AuthActivity::class.java))
            }
        }

        /*setup the bottomNavigationView*/
        val bottomNavigationView = findViewById<ChipNavigationBar>(R.id.bottomNavigationView)

        //set a default value for the fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, ScheduleFragment()).commit()
        bottomNavigationView.setItemSelected(R.id.schedules)
        //display the selected fragment using setOnItemSelectedListener function
        bottomNavigationView.setOnItemSelectedListener{
            when (it) {
                R.id.schedules -> {
                    fragment = ScheduleFragment()
                }
                R.id.pendingTests -> {
                    fragment = PendingTestsFragment()
                }
                R.id.testHistory -> {
                    fragment = TestHistoryFragment()
                }
            }
            supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit()
        }
    }
}