package edu.bluejack20_1.dearmory.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import edu.bluejack20_1.dearmory.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.models.User

class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        val loadingDialog = LoadingSignIn(this)
        loadingDialog.startLoading()

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        Handler().postDelayed({
            if(user != null){
                updateStaticUser()
                loadingDialog.dismissDialog()
                Toast.makeText(this, "Welcome ${user.displayName}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AppActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }else {
                loadingDialog.dismissDialog()
                Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }, 1)
    }

    private fun updateStaticUser(){
        ref = FirebaseDatabase.getInstance().reference.child("User")
        val user =
            GoogleSignIn.getLastSignedInAccount(applicationContext)
        if(user != null){
            ref = user.id?.let { FirebaseDatabase.getInstance().reference.child("User").child(it) }!!
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        Log.d("REF", "USER ALREADY EXISTS")
                        User.getInstance().setName(snapshot.child("name").value.toString())
                        User.getInstance().setId(snapshot.child("id").value.toString())
                    }else{
                    }
                }
            })
        }
    }
}