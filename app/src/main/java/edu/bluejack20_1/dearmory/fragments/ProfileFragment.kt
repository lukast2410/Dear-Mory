package edu.bluejack20_1.dearmory.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.activities.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.update_profile_pop_up.*

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dialog: Dialog

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

        dialog = context?.let { Dialog(it) }!!

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
            showUsernameUpdatePopUp()
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

            val intent = Intent(context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun showUsernameUpdatePopUp(){
        dialog.setContentView(R.layout.update_profile_pop_up)
        val btn = dialog.findViewById<TextView>(R.id.close_update_username_button)
        btn.setOnClickListener {
            dialog.dismiss()
        }
        val saveBtn = dialog.findViewById<Button>(R.id.save_username_logo)
        saveBtn.setOnClickListener {
            updateUsername()
        }
        dialog.show()


        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (account != null) {
            databaseReference = account.id?.let {
                FirebaseDatabase.getInstance().getReference("User").child(it) }!!
            databaseReference.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value.toString()
                    Log.d("uName", name)
                    val uName = dialog.findViewById<EditText>(R.id.username_field)
                    uName.setText(name, TextView.BufferType.EDITABLE)
                    Log.d("uName", "oh yes")
                }
            })
        }
    }

    private fun updateUsername(){
        val name = dialog.findViewById<EditText>(R.id.username_field).text
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if(account != null && name != null){
            if(name.isNotBlank()){
                databaseReference = FirebaseDatabase.getInstance().getReference("User").child(
                    account.id!!
                )
                val hashMap: HashMap<String, String> = HashMap<String, String>()
                hashMap["name"] = name.toString()
                databaseReference.updateChildren(hashMap as Map<String, Any>).addOnCompleteListener {
                    dialog.dismiss()
                }
                Log.d("UPDATE USERNAME", "acc not null and name not null ")
            }
            Log.d("UPDATE USERNAME", "acc not null but name is blank")
        }else{
            Log.d("UPDATE USERNAME", "acc null or name null")
        }
    }

}
