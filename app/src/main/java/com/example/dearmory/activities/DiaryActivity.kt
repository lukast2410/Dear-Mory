package com.example.dearmory.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dearmory.R
import com.example.dearmory.ThemeManager
import kotlinx.android.synthetic.main.activity_app.*
import kotlinx.android.synthetic.main.activity_main.*

class DiaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeManager.setUpTheme())
        setContentView(R.layout.activity_diary)
        iv_main_background.setImageResource(ThemeManager.setUpBackground())
    }
}
