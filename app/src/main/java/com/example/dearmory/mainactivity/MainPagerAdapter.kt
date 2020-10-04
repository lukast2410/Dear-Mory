package com.example.dearmory.mainactivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.dearmory.calendar.CalendarFragment
import com.example.dearmory.home.HomeFragment
import com.example.dearmory.profile.ProfileFragment

public class MainPagerAdapter(var fm: FragmentManager): FragmentStatePagerAdapter(fm){


    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> CalendarFragment.newInstance()
            1 -> HomeFragment.newInstance()
            2 -> ProfileFragment.newInstance()
            else -> CalendarFragment.newInstance()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}