package com.example.sihati_labo.viewmodels

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sihati_labo.Database.Test
import com.example.sihati_labo.repositories.TestRepository

class TestViewModel : ViewModel() {
    private val mRepository = TestRepository()
    var testsReady: MutableLiveData<List<Test>>? = null
    var pendingTests: MutableLiveData<List<Test>>? = null

    fun init() {
        testsReady = mRepository.testsReady
        pendingTests = mRepository.pendingTests
    }

    fun getTestsWithDate(date:String){
        mRepository.getTestsWithDate(date)
    }

    fun createTest(test: Test, activity: Activity){
        mRepository.createTest(test,activity)
    }
}