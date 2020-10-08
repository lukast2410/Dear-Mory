package com.example.dearmory.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.dearmory.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        actionBar?.hide()

        val thread = Thread(){
            run (){
                try {
                    Thread.sleep(3000)
                }catch (e: InterruptedException){
                    e.printStackTrace()
                }finally {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }

        thread.start()

    }
}