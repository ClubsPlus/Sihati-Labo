package com.example.sihati_labo.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sihati_labo.Database.Test
import com.example.sihati_labo.repositories.TestRepository

class TestViewModel : ViewModel() {
    private val mRepository = TestRepository()
    var tests: MutableLiveData<List<Test>>? = null

    fun init() {
        tests = mRepository.tests
    }

    fun updateScheduleWithDate(date:String){
        Log.d("test","I'm in the updateScheduleWithDate viewmodel")
        tests?.value = emptyList()
        Log.d("test","after cleaning the list in the viewmodel size="+ tests?.value!!.size.toString())
        mRepository.getTestsWithDate(date)
    }
}