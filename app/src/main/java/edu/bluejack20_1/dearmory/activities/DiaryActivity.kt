package edu.bluejack20_1.dearmory.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import kotlinx.android.synthetic.main.activity_app.*

class DiaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeManager.setUpTheme())
        setContentView(R.layout.activity_diary)
        iv_main_background.setImageResource(ThemeManager.setUpBackground())
    }
}
