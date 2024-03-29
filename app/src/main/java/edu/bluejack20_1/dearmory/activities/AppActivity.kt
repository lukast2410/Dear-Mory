package edu.bluejack20_1.dearmory.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.adapters.MainPagerAdapter
import kotlinx.android.synthetic.main.activity_app.*

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeManager.setUpTheme())
        setContentView(R.layout.activity_app)
        iv_main_background.setImageResource(ThemeManager.setUpBackground())
        initFragments()
        setViewPagerAdapter()
        setBottomNav()
        setViewPagerListener()
    }

    private fun initFragments() {
        val bottomNav: MeowBottomNavigation = bottom_nav
        bottomNav.add(MeowBottomNavigation.Model(1,
            R.drawable.reminder_calendar
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
        vp_main.adapter = MainPagerAdapter(
            supportFragmentManager
        )
    }
}