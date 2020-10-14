package edu.bluejack20_1.dearmory.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import edu.bluejack20_1.dearmory.models.Reminder
import edu.bluejack20_1.dearmory.models.User

class ReminderRepo {

    var reminders: ArrayList<Reminder> = ArrayList()
    val reminder: MutableLiveData<ArrayList<Reminder>> = MutableLiveData()

    companion object{
        var instance: ReminderRepo? = null
        @JvmName("Asd")
        fun getInstance(): ReminderRepo{
            if(instance == null){
                instance = ReminderRepo()
            }
            return instance as ReminderRepo
        }
    }

    fun getReminders(): MutableLiveData<ArrayList<Reminder>>{
//        if(reminders.size == 0){
            loadReminder()
//        }

        reminder.value = reminders
        return reminder
    }

    private fun loadReminder() {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Reminder")
        val query: Query = ref.child(User.getInstance().getId().toString())
        query.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                reminders.clear()
                for (snap in snapshot.children){
                    reminders.add(snap.getValue(Reminder::class.java)!!)
                }
                reminder.postValue(reminders)
            }

        })
    }
}