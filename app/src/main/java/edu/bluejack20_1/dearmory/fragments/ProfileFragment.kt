package edu.bluejack20_1.dearmory.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide

import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.activities.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import edu.bluejack20_1.dearmory.activities.SettingActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.update_profile_pop_up.*

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dialog: Dialog
    private lateinit var chosen_profile_image_url: Uri

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
                        if(profile_name != null){
                            profile_name.text = snapshot.child("name").value.toString()
                            profile_email.text = snapshot.child("email").value.toString()
                            val pp = snapshot.child("profilePicture").value.toString()
                            Glide.with(this@ProfileFragment).load(pp).into(profile_picture)
                        }
                    }
                }
            })
        }

        profile_picture.setOnClickListener {
            selectPictureIntent()
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

        ib_go_setting.setOnClickListener {
            startActivity(Intent(context, SettingActivity::class.java))
        }
    }

    fun selectPictureIntent(){
        val intent: Intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1 && resultCode == -1 && data != null && data.data != null){
            chosen_profile_image_url = data.data!!
            profile_picture.setImageURI(chosen_profile_image_url)
            updateProfilePicture(chosen_profile_image_url)
        }
    }

    private fun showUsernameUpdatePopUp(){
        dialog.setContentView(R.layout.update_profile_pop_up)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val saveBtn = dialog.findViewById(R.id.save_username_logo) as CardView
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
                    val uName = dialog.findViewById<EditText>(R.id.username_field)
                    uName.setText(name, TextView.BufferType.EDITABLE)
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

    private fun updateProfilePicture(uri: Uri){
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if(account != null){
            databaseReference = FirebaseDatabase.getInstance().getReference("User").child(
                account.id!!
            )
            val hashMap: HashMap<String, String> = HashMap<String, String>()
            hashMap["profilePicture"] = uri.toString()
            databaseReference.updateChildren(hashMap as Map<String, Any>).addOnCompleteListener {
                dialog.dismiss()
            }
            Log.d("UPDATE PP", "acc not null and pp not null ")
        }else{
            Log.d("UPDATE PP", "acc null or pp null")
        }
    }

}