package edu.bluejack20_1.dearmory.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import edu.bluejack20_1.dearmory.fragments.CalendarFragment
import edu.bluejack20_1.dearmory.fragments.HomeFragment
import edu.bluejack20_1.dearmory.fragments.ProfileFragment

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