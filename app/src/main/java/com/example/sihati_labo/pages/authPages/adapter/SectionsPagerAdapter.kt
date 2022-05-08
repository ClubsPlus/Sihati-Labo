package com.example.sihati_labo.pages.authPages.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.sihati_labo.pages.authPages.fragments.LoginFragment
import com.example.sihati_labo.pages.authPages.fragments.SignUpFragment


class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    private val TAB_FRAGMENTS = arrayOf(
        LoginFragment(),
        SignUpFragment()
    )

    private val TAB_TITLES = arrayOf(
        "Connexion",
        "Inscription"
    )

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return TAB_FRAGMENTS [position]
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return TAB_FRAGMENTS.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return TAB_TITLES[position]
    }
}