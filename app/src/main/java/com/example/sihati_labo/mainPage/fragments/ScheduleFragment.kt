package com.example.sihati_labo.mainPage.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sihati_labo.R
import com.example.sihati_labo.createSchedulePage.CreateScheduleActivity
import com.example.sihati_labo.databinding.FragmentScheduleBinding


class ScheduleFragment : Fragment() {

    private lateinit var binding: FragmentScheduleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentScheduleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //change the fab icon color
        val myFabSrc = resources.getDrawable(R.drawable.add)
        myFabSrc.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
        binding.fab.setImageDrawable(myFabSrc)

        binding.fab.setOnClickListener {
            startActivity(Intent(requireActivity(),CreateScheduleActivity::class.java))
        }
    }
}