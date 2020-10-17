package edu.bluejack20_1.dearmory.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide

import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.activities.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.models.User
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        fun newInstance() : ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        var user = auth.currentUser
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (account != null) {
            databaseReference =
                account.id?.let { FirebaseDatabase.getInstance().reference.child("User").child(it) }!!
            databaseReference.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        profile_name.text = snapshot.child("name").value.toString()
                        profile_email.text = snapshot.child("email").value.toString()
                        val pp = snapshot.child("profilePicture").value.toString()
                        Glide.with(this@ProfileFragment).load(pp).into(profile_picture)
                    }
                }
            })
        }

        profile_name.setOnClickListener {
            Log.d("Updated", "name clicked")
            username_field.visibility = View.VISIBLE
            profile_name.visibility = View.INVISIBLE
        }

        username_field.setOnClickListener{
            username_field.visibility = View.INVISIBLE
            profile_name.visibility = View.VISIBLE
        }

        sign_out_button.setOnClickListener {
            Log.d("IN", "Log out clicked")
            if(user != null){
                Log.d("IN", "name is ${user!!.displayName}")
                FirebaseAuth.getInstance().signOut()
            }
            user = FirebaseAuth.getInstance().currentUser
            if(user == null) {
                Log.d("IN", "name is. ${user?.displayName}")
            }

            startActivity(Intent(context, SignInActivity::class.java))
        }
    }

}
