package com.example.sihati_labo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sihati_labo.Database.Test
import com.example.sihati_labo.R
import com.example.sihati_labo.viewmodels.ScheduleViewModel

class PendingAdapter(
    val context: Context,
    private val viewModel: ScheduleViewModel,
    private val setOnClickInterface: SetOnClickInterface,
) : RecyclerView.Adapter<PendingAdapter.PendingViewHolder>() {

    // on below line we are creating a
    // variable for our all notes list.
    private val allTests = ArrayList<Test>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingViewHolder {
        // inflating our layout file for each item of recycler view.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.pending_item,
            parent, false
        )
        return PendingViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PendingViewHolder, position: Int) {
        allTests[position].user_id?.let {
            viewModel.getUserByIdAndSet(it,holder.personName)
        }

        allTests[position].schedule_id?.let {
            viewModel.getScheduleByIdAndSet(uid=it,date=holder.date)
        }

        holder.button.setOnClickListener {
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

    inner class PendingViewHolder (itView: View) :
        RecyclerView.ViewHolder(itView) {
        val personName : TextView = itemView.findViewById(R.id.person_name)
        val date : TextView = itemView.findViewById(R.id.date)
        val button : Button = itemView.findViewById(R.id.set)
    }

    interface SetOnClickInterface {
        // creating a method for click action
        // on recycler view item for updating it.
        fun onClick(test: Test)
    }
}