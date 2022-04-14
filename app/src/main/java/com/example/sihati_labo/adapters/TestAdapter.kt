package com.example.sihati_labo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sihati_labo.R
import com.example.sihati_labo.Database.Test

class TestAdapter(
    val context: Context,
) : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

    // on below line we are creating a
    // variable for our all notes list.
    private val allTests = ArrayList<Test>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        // inflating our layout file for each item of recycler view.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.test_item,
            parent, false
        )
        return TestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        TODO("Not yet implemented")
    }


    override fun getItemCount() = allTests.size

    // below method is use to update our list of notes.
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Test>) {
        // on below line we are clearing
        // our notes array list
        allTests.clear()
        // on below line we are adding a
        // new list to our all notes list.
        allTests.addAll(newList)
        // on below line we are calling notify data
        // change method to notify our adapter.
        notifyDataSetChanged()
    }


    inner class TestViewHolder (itView: View) :
        RecyclerView.ViewHolder(itView) {

    }
}