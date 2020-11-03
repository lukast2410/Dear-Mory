package edu.bluejack20_1.dearmory.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.models.User
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
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.ThemeManager
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
        setTheme(ThemeManager.setUpTheme())
        setContentView(R.layout.activity_signin)
        iv_signIn_background.setImageResource(ThemeManager.setUpBackground())
        initializeSignInButton()

        ref = FirebaseDatabase.getInstance().reference.child("User")
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient =  GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()

        if(FirebaseAuth.getInstance().currentUser == null){
            googleSignInClient.signOut()
        }

//        sign_out_button.setOnClickListener {
//            signOut()
//        }

//        save_button.setOnClickListener {
//            val user =
//                GoogleSignIn.getLastSignedInAccount(applicationContext)
//            if(user != null){
//                val temp: User =
//                    User()
//                temp.setName(user.displayName as String)
//                temp.setEmail(user.email as String)
//                temp.setProfilePicture(user.photoUrl)
//                temp.setId(user.id as String)
//                Toast.makeText(this, temp.getName(),Toast.LENGTH_LONG).show()
//                Toast.makeText(this, temp.getEmail(),Toast.LENGTH_LONG).show()
//                Toast.makeText(this, temp.getProfilePicture(),Toast.LENGTH_LONG).show()
//                Toast.makeText(this, temp.getId(),Toast.LENGTH_LONG).show()
//
//                ref.child(user.id as String).setValue(temp)
//            }
//        }
    }

    private fun initializeSignInButton() {
        if(ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
            google_sign_in_button.setCardBackgroundColor(Color.WHITE)
        google_sign_in_button.setOnClickListener {
            signIn()
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

    private fun saveUser(){
        val user =
            GoogleSignIn.getLastSignedInAccount(applicationContext)
        if(user != null){
            ref = user.id?.let { FirebaseDatabase.getInstance().reference.child("User").child(it) }!!
            ref.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        Log.d("REF", "USER ALREADY EXISTS")
                        User.getInstance().setName(snapshot.child("name").value.toString())
                        User.getInstance().setId(snapshot.child("id").value.toString())
                    }else{
                        Log.d("REF", "USER DOES NOT EXISTS..CREATING NEW USER..")
                        User.getInstance().setName(user.displayName as String)
                        User.getInstance().setEmail(user.email as String)
                        User.getInstance().setProfilePicture(user.photoUrl)
                        User.getInstance().setId(user.id as String)
                        ref.setValue(User.getInstance()).addOnCompleteListener {
                            Log.d("REF", "SUCCESS CREATE NEW USER")
                        }
                    }
                }
            })
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
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                }
            }else{
//                val loginIntent = Intent(this, LogInActivity::class.java)
//                startActivity(loginIntent)
            }
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            val acc: GoogleSignInAccount? = task?.getResult(ApiException::class.java)
            if (acc != null) {
                FirebaseGoogleAuth(acc)
            }
        } catch (e: ApiException) {
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
                            val user: FirebaseUser? = auth.getCurrentUser()
                            if (user != null) {
                                updateUI(user)
                            }
                        } else {
                            updateUI(null)
                        }
                    })
        } else {
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
                    val user: FirebaseUser? = auth.currentUser
                    if (user != null) {
//                        sign_out_button.visibility = View.VISIBLE
//                        save_button.visibility = View.VISIBLE
//                        google_sign_in_button.visibility = View.INVISIBLE
                        updateUI(user)
                        saveUser()
                        val intent = Intent(this, LogInActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                }
            }
    }
}