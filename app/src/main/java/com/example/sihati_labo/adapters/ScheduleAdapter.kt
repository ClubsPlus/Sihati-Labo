package com.example.sihati_labo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.paris.extensions.style
import com.example.sihati_labo.Database.Schedule
import com.example.sihati_labo.R


class ScheduleAdapter(
    val context: Context,
    private val taskClickInterface: TaskClickInterface?=null,
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>(){

    // on below line we are creating a
    // variable for our all notes list.
    private val allSchedules = ArrayList<Schedule>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        // inflating our layout file for each item of recycler view.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.schedule_item,
            parent, false
        )
        return ScheduleViewHolder(itemView)
    }

    @SuppressLint("ResourceType", "SetTextI18n")
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        // on below line we are setting data to item of recycler view.
        holder.date.text = allSchedules[position].date
        holder.startTime.text = allSchedules[position].time_Start
        holder.endTime.text = allSchedules[position].time_end
        holder.person.text = allSchedules[position].person.toString()+"/"+allSchedules[position].limite.toString()
        val progress = ((allSchedules[position].person)!! *100)/ allSchedules[position].limite!!
        holder.progressBar.progress = progress

        //change the color of the progressbar
//        holder.progressBar.style(R.style.progressBar)
//        when (progress) {
//            in 0..45 -> progressDrawable.setTint(R.color.green)
//            in 46..80 -> progressDrawable.setTint(R.color.yellow)
//            else -> progressDrawable.setTint(R.color.red)
//        }
//        holder.progressBar.progressDrawable = progressDrawable

        holder.schedule.setOnClickListener { taskClickInterface?.onClick(allSchedules[position]) }
    }

    override fun getItemCount() = allSchedules.size

    // below method is use to update our list of notes.
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Schedule>) {
        // on below line we are clearing
        // our notes array list
        allSchedules.clear()
        // on below line we are adding a
        // new list to our all notes list.
        allSchedules.addAll(newList)
        // on below line we are calling notify data
        // change method to notify our adapter.
        notifyDataSetChanged()
    }

    inner class ScheduleViewHolder(itView: View) :
        RecyclerView.ViewHolder(itView){
        // on below line we are creating an initializing all our
        // variables which we have added in layout file.
        val date: TextView = itemView.findViewById(R.id.date)
        val startTime: TextView = itemView.findViewById(R.id.startTime)
        val endTime: TextView = itemView.findViewById(R.id.endTime)
        val person: TextView = itemView.findViewById(R.id.persons)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val schedule: ConstraintLayout = itemView.findViewById(R.id.schedule)
    }

    interface TaskClickInterface {
        // creating a method for click action
        // on recycler view item for updating it.
        fun onClick(schedule: Schedule)
    }
}