package com.example.dearmory.mainactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.dearmory.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.LightTheme)
        setContentView(R.layout.activity_main)
        iv_main_background.setImageResource(R.drawable.light)
        initFragments()
        setViewPagerAdapter()
        setBottomNav()
        setViewPagerListener()

        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun initFragments() {
        val bottomNav: MeowBottomNavigation = bottom_nav
        bottomNav.add(MeowBottomNavigation.Model(1,
            R.drawable.calendar
        ))
        bottomNav.add(MeowBottomNavigation.Model(2,
            R.drawable.home
        ))
        bottomNav.add(MeowBottomNavigation.Model(3,
            R.drawable.profile
        ))
        bottomNav.show(2)
    }

    private fun setViewPagerListener() {
        vp_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                bottom_nav.show(
                    when(position){
                        0 -> 1
                        1 -> 2
                        2 -> 3
                        else -> 2
                    }
                )
            }
        })
    }

    private fun setBottomNav() {
        bottom_nav.setOnShowListener {
            when(it.id){
                1 -> vp_main.currentItem = 0
                2 -> vp_main.currentItem = 1
                3 -> vp_main.currentItem = 2
            }
        }
    }

    private fun setViewPagerAdapter() {
        vp_main.adapter = MainPagerAdapter(supportFragmentManager)
    }
}
