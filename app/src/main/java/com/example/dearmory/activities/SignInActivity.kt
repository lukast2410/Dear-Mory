package com.example.dearmory.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dearmory.R
import com.example.dearmory.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signin.*


class SignInActivity : AppCompatActivity() {

    companion object{
        private const val RC_SIGN_IN = 123

        fun logOut(a: SignInActivity){
            a.signOut()
        }
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        ref = FirebaseDatabase.getInstance().reference.child("User")
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient =  GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()

        google_sign_in_button.setOnClickListener {
            signIn()
        }

        if(FirebaseAuth.getInstance().currentUser == null){
            googleSignInClient.signOut()
        }

//        sign_out_button.setOnClickListener {
//            signOut()
//        }

        save_button.setOnClickListener {
            val user =
                GoogleSignIn.getLastSignedInAccount(applicationContext)
            if(user != null){
                val temp: User =
                    User()
                temp.setName(user.displayName as String)
                temp.setEmail(user.email as String)
                temp.setProfilePicture(user.photoUrl)
                temp.setId(user.id as String)
                Toast.makeText(this, temp.getName(),Toast.LENGTH_LONG).show()
                Toast.makeText(this, temp.getEmail(),Toast.LENGTH_LONG).show()
                Toast.makeText(this, temp.getProfilePicture(),Toast.LENGTH_LONG).show()
                Toast.makeText(this, temp.getId(),Toast.LENGTH_LONG).show()

                ref.child(user.id as String).setValue(temp)
            }
        }
    }

    fun signOut(){
        googleSignInClient.signOut()
        Log.w("SignInAct", "Singed out")
//        auth.signOut()
//        Toast.makeText(this, "Logged out",Toast.LENGTH_SHORT).show()
//        sign_out_button.visibility = View.INVISIBLE
//        save_button.visibility = View.INVISIBLE
//        google_sign_in_button.visibility = View.VISIBLE
//        val loginIntent = Intent(this, MainActivity::class.java)
//        startActivity(loginIntent)
//        finish()
    }

    fun saveUser(){
        val user =
            GoogleSignIn.getLastSignedInAccount(applicationContext)
        if(user != null){
            val temp: User =
                User()
            temp.setName(user.displayName as String)
            temp.setEmail(user.email as String)
            temp.setProfilePicture(user.photoUrl)
            temp.setId(user.id as String)
            Toast.makeText(this, temp.getName(),Toast.LENGTH_LONG).show()
            Toast.makeText(this, temp.getEmail(),Toast.LENGTH_LONG).show()
            Toast.makeText(this, temp.getProfilePicture(),Toast.LENGTH_LONG).show()
            Toast.makeText(this, temp.getId(),Toast.LENGTH_LONG).show()

            ref.child(user.id as String).setValue(temp)
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent,
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)

//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)!!
//                Log.d("SignInAct", "firebaseAuthWithGoogle:" + account.id)
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                // Google Sign In failed, update UI appropriately
//                Toast.makeText(this, "Sign In Failed $e", Toast.LENGTH_SHORT).show()
//                Log.w("SignInAct", "Google sign in failed", e)
//            }


            val exception = task.exception
            if(task.isSuccessful){
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SignInActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SignInActivity", "Google sign in failed", e)
                }
            }else{
                Log.w("SignInActivity",  exception.toString())
                Toast.makeText(applicationContext,exception.toString(),Toast.LENGTH_SHORT).show()
                val loginIntent = Intent(this, MainActivity::class.java)
                startActivity(loginIntent)
            }
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            val acc: GoogleSignInAccount? = task?.getResult(ApiException::class.java)
            Toast.makeText(this, "Signed In Successfully", Toast.LENGTH_SHORT).show()
            if (acc != null) {
                FirebaseGoogleAuth(acc)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Sign In Failed $e", Toast.LENGTH_SHORT).show()
            Log.w("SignInActivity", "Google sign in failed", e)
            FirebaseGoogleAuth(null)
        }
    }

    private fun FirebaseGoogleAuth(acct: GoogleSignInAccount?) {
        if (acct != null) {
            val authCredential =
                GoogleAuthProvider.getCredential(acct.getIdToken(), null)
            auth.signInWithCredential(authCredential)
                .addOnCompleteListener(this,
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Successful", Toast.LENGTH_SHORT)
                                .show()
                            val user: FirebaseUser? = auth.getCurrentUser()
                            if (user != null) {
                                updateUI(user)
                            }
                        } else {
                            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    })
        } else {
            Toast.makeText(this, "acc failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        // btnSignOut.setVisibility(View.VISIBLE)
        val account =
            GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (account != null) {
            val personName = account.displayName
            val personGivenName = account.givenName
            val personFamilyName = account.familyName
            val personEmail = account.email
            val personId = account.id
            val personPhoto: Uri? = account.photoUrl
            Toast.makeText(this, "$personName $personEmail $personPhoto", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "$personId", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignInActivity", "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    if (user != null) {
//                        sign_out_button.visibility = View.VISIBLE
//                        save_button.visibility = View.VISIBLE
//                        google_sign_in_button.visibility = View.INVISIBLE
                        updateUI(user)
                        saveUser()
                        startActivity(Intent(this, LogInActivity::class.java))
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignInActivity", "signInWithCredential:failure", task.exception)
                }
            }
    }
}