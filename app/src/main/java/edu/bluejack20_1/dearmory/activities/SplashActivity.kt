package edu.bluejack20_1.dearmory.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        loadTheme()

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        actionBar?.hide()

        if(ThemeManager.isDarkTheme(this))
            developers_label.setTextColor(Color.WHITE)
        else
            developers_label.setTextColor(Color.BLACK)

        val thread = Thread(){
            run (){
                try {
                    Thread.sleep(3000)
                }catch (e: InterruptedException){
                    e.printStackTrace()
                }finally {
                    nextActivity()
                }
            }
        }

        thread.start()
    }

    private fun loadTheme(){
        sharedPreferences = getSharedPreferences(ThemeManager.SHARED_PREFS, MODE_PRIVATE)
        val theme = sharedPreferences.getInt(ThemeManager.THEME, ThemeManager.LIGHT_THEME_INDEX)
        ThemeManager.THEME_INDEX = theme

        val textSize = sharedPreferences.getInt(ThemeManager.TEXT, ThemeManager.SMALL_TEXT_SIZE)
        ThemeManager.TEXT_SIZE = textSize

        val language = sharedPreferences.getString(ThemeManager.LANG, ThemeManager.ENGLISH)
        if (language != null) {
            ThemeManager.LANGUAGE = language
        }
    }

    private fun nextActivity(){
        sharedPreferences = getSharedPreferences(ThemeManager.SHARED_PREFS, MODE_PRIVATE)
        val never = sharedPreferences.getBoolean(ThemeManager.GUIDE_PAGE, true)
        if(never){
            editor = sharedPreferences.edit()
            editor.putBoolean(ThemeManager.GUIDE_PAGE, false)
            editor.apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
    }
}