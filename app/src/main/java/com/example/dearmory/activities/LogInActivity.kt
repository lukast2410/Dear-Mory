package com.example.dearmory.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.dearmory.R
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        val loadingDialog = LoadingSignIn(this)
        loadingDialog.startLoading()

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        Handler().postDelayed({
            if(user != null){
                loadingDialog.dismissDialog()
                Toast.makeText(this, "Welcome ${user.displayName}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, AppActivity::class.java))
//                startActivity(Intent(this, SignInActivity::class.java))
            }else {
                loadingDialog.dismissDialog()
                Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SignInActivity::class.java))
            }
        }, 1)
    }
}