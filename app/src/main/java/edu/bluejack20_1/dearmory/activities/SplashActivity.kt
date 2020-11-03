package edu.bluejack20_1.dearmory.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.receivers.AlertReceiver
import kotlinx.android.synthetic.main.activity_reminder.*
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @RequiresApi(Build.VERSION_CODES.N)
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
                    Thread.sleep(2000)
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
        val theme = sharedPreferences.getInt(ThemeManager.THEME, ThemeManager.DARK_THEME_INDEX)
        ThemeManager.THEME_INDEX = theme

        val textSize = sharedPreferences.getInt(ThemeManager.TEXT, ThemeManager.SMALL_TEXT_SIZE)
        ThemeManager.TEXT_SIZE = textSize
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun nextActivity(){
        sharedPreferences = getSharedPreferences(ThemeManager.SHARED_PREFS, MODE_PRIVATE)
        val never = sharedPreferences.getBoolean(ThemeManager.GUIDE_PAGE, true)
        if(never){
            setDailyNotification()
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setDailyNotification(){
        val calendar: Calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val intent: Intent = Intent(applicationContext, AlertReceiver::class.java)
        intent.putExtra("label", "Don't forget to write your diary :)")
        intent.putExtra("vibrate", "on")
        intent.putExtra("daily", "true")

        val pendingIntent : PendingIntent = PendingIntent.getBroadcast(
            applicationContext, 100, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager : AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}