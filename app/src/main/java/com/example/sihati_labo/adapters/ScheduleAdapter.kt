package com.example.sihati_labo.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        // on below line we are setting data to item of recycler view.
        holder.date.text = allSchedules[position].date
        holder.startTime.text = allSchedules[position].time_Start
        holder.endTime.text = allSchedules[position].time_end
        holder.person.text = allSchedules[position].person.toString()+"/"+allSchedules[position].limite.toString()
        val progress = ((allSchedules[position].person)!! *100)/ allSchedules[position].limite!!
        holder.progressBar.progress = progress
        val progressDrawable: Drawable = holder.progressBar.progressDrawable.mutate()

        when (progress) {
            in 0..45 ->progressDrawable.setColorFilter(R.color.green, PorterDuff.Mode.SRC_IN)
            in 46..80 ->progressDrawable.setColorFilter(R.color.yellow, PorterDuff.Mode.SRC_IN)
            else ->progressDrawable.setColorFilter(R.color.red, PorterDuff.Mode.SRC_IN)
        }
        holder.progressBar.progressDrawable = progressDrawable
    }

    override fun getItemCount() = allSchedules.size

    // below method is use to update our list of notes.
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
        val date = itemView.findViewById<TextView>(R.id.date)
        val startTime = itemView.findViewById<TextView>(R.id.startTime)
        val endTime = itemView.findViewById<TextView>(R.id.endTime)
        val person = itemView.findViewById<TextView>(R.id.persons)
        val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
    }

    interface TaskClickInterface {
        // creating a method for click action
        // on recycler view item for updating it.
        fun onClick(task: Schedule)
    }
}