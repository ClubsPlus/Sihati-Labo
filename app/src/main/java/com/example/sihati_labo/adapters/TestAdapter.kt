package com.example.sihati_labo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.R
import com.example.sihati_labo.Database.Test
import com.example.sihati_labo.viewmodels.ScheduleViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class TestAdapter(
    val context: Context,
    private val viewModel: ScheduleViewModel,
    private val setOnClickInterface: SetOnClickInterface,
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        allTests[position].user_id?.let {
            viewModel.getUserByIdAndSet(it,holder.personName)
        }

        allTests[position].schedule_id?.let {
            viewModel.getScheduleByIdAndSet(it,holder.date,holder.time)
        }

        when(allTests[position].result){
            "Positive" -> Glide.with(context).load(R.drawable.logo_red).into(holder.result)
            "Negative" -> Glide.with(context).load(R.drawable.logo_green).into(holder.result)
            "Pending" -> Glide.with(context).load(R.drawable.logo_yellow).into(holder.result)
            "Not Tested" -> Glide.with(context).load(R.drawable.logo_grey).into(holder.result)
        }

        holder.container.setOnClickListener {
            setOnClickInterface.onClick(allTests[position])
        }
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
        val personName : TextView = itemView.findViewById(R.id.laboratory_name)
        val date : TextView = itemView.findViewById(R.id.date)
        val time : TextView = itemView.findViewById(R.id.time)
        val result : ImageView = itemView.findViewById(R.id.result_img)
        var container : ConstraintLayout = itemView.findViewById(R.id.container)
    }

    interface SetOnClickInterface {
        // creating a method for click action
        // on recycler view item for updating it.
        fun onClick(test: Test)
    }
}