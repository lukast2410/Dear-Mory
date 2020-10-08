package com.example.dearmory.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.dearmory.R

class SliderAdapter : PagerAdapter {

    private lateinit var context: Context
    private lateinit var layoutInflater: LayoutInflater

    constructor (context: Context){
        this.context = context
    }

    var slide_images = arrayOf(
        R.drawable.journal,
        R.drawable.cash,
        R.drawable.calendar)

    var slide_headings = arrayOf(
        "DIARY",
        "MONEY MANAGER",
        "REMINDER")

    var slide_description = arrayOf(
        "Create your own diary, manually!! :)",
        "Manage your income and expenses :D",
        "We will always remind you :)"
    )

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.slide_layout, container, false)

        val slideImageView: ImageView = view.findViewById(R.id.diaryImage)
        val slideHeading: TextView = view.findViewById(R.id.diaryHeading)
        val slideDescription: TextView = view.findViewById(R.id.diaryDescription)

        slideImageView.setImageResource(slide_images[position])
        slideHeading.setText(slide_headings[position])
        slideDescription.setText(slide_description[position])

        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    override fun getCount(): Int {
        return slide_headings.size
    }


}