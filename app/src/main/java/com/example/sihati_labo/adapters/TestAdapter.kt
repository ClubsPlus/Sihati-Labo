package com.example.sihati_labo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sihati_labo.R
import com.example.sihati_labo.Database.Test
import com.example.sihati_labo.viewmodels.ScheduleViewModel

class TestAdapter(
    val context: Context,
    private val viewModel: ScheduleViewModel,
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
            viewModel.getUserById(it)
            holder.laboratoryName.text = "${viewModel.user?.name}"
        }

        allTests[position].schedule_id?.let {
            viewModel.getScheduleById(it)
            holder.date.text = "${viewModel.schedule?.date}"
            holder.time.text = "${viewModel.schedule?.time_Start} - ${viewModel.schedule?.time_end}"
        }

        when(allTests[position].result){
            "Positive" -> Glide.with(context).load(R.drawable.logo_red).into(holder.result)
            "Negative" -> Glide.with(context).load(R.drawable.logo_green).into(holder.result)
            "Pending" -> Glide.with(context).load(R.drawable.logo_yellow).into(holder.result)
            "Not Tested" -> Glide.with(context).load(R.drawable.logo_grey).into(holder.result)
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
        val laboratoryName : TextView = itemView.findViewById(R.id.laboratory_name)
        val date : TextView = itemView.findViewById(R.id.date)
        val time : TextView = itemView.findViewById(R.id.time)
        val result : ImageView = itemView.findViewById(R.id.result_img)
    }
}