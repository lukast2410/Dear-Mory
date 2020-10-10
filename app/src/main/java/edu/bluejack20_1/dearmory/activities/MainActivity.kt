package edu.bluejack20_1.dearmory.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.adapters.SliderAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var dotLayout: LinearLayout

    private var dots = arrayOfNulls<TextView>(3)
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var finishButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.sliderView)
        dotLayout = findViewById(R.id.dotsLayout)
        finishButton = finish_button
        sliderAdapter = SliderAdapter(this)

        viewPager.adapter = sliderAdapter

        addDots(0)
        setViewPagerListener()

        finishButton.setOnClickListener{
            val loginIntent = Intent(this, LogInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginIntent)
        }
    }

    private fun setViewPagerListener() {
        sliderView.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                addDots(position)
                if(position == dots.size-1){
                    finishButton.visibility = View.VISIBLE
                }else{
                    finishButton.visibility = View.INVISIBLE
                }
            }

        })
    }

    fun addDots(position: Int){
        dotLayout.removeAllViews()
        for (i in 0 until dots.size step 1){
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35F
            dots[i]?.setTextColor(resources.getColor(R.color.colorTransparent))
            dotLayout.addView(dots[i])
        }

        if(dots.isNotEmpty()){
            dots[position]?.setTextColor(resources.getColor(R.color.colorWhite))
        }
    }

}