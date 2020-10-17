package edu.bluejack20_1.dearmory.repository

import android.annotation.SuppressLint
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.models.Reminder
import edu.bluejack20_1.dearmory.models.User
import java.util.*
import kotlin.collections.ArrayList

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

    fun getReminders(date: Date): MutableLiveData<ArrayList<Reminder>>{
//        if(reminders.size == 0){
            loadReminder(date)
//        }

        reminder.value = reminders
        return reminder
    }

    private fun loadReminder(date: Date) {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Reminder")
        val query: Query = ref.child(User.getInstance().getId().toString())
        query.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            @SuppressLint("SimpleDateFormat")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                reminders.clear()

                for (snap in snapshot.children){
                    val temp = snap.child("date").value.toString().split("/").map { it.toInt() }
                    val cal: Calendar = Calendar.getInstance()
                    cal.set(Calendar.YEAR, temp[2])
                    cal.set(Calendar.MONTH, temp[1]-1)
                    cal.set(Calendar.DAY_OF_MONTH, temp[0])
                    val pattern = "yyyy-MM-dd"
                    val simpleDateFormat = SimpleDateFormat(pattern)

                    val snapDate: String = simpleDateFormat.format(cal.time)
                    val chosenDate: String = simpleDateFormat.format(date)
                    if(chosenDate == snapDate){
                        reminders.add(snap.getValue(Reminder::class.java)!!)
                    }
                }
                reminder.postValue(reminders)
            }

        })
    }

    fun getAllEvent(compactCalendar: CompactCalendarView): ArrayList<Calendar>{
        val events: ArrayList<Calendar> = ArrayList()
        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Reminder")
        val query: Query = ref.child(User.getInstance().getId().toString())
        query.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            @SuppressLint("SimpleDateFormat")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                compactCalendar.removeAllEvents()
                for (snap in snapshot.children){
                    val temp = snap.child("date").value.toString().split("/").map { it.toInt() }
                    val cal: Calendar = Calendar.getInstance()
                    cal.set(Calendar.YEAR, temp[2])
                    cal.set(Calendar.MONTH, temp[1]-1)
                    cal.set(Calendar.DAY_OF_MONTH, temp[0])
                    events.add(cal)
                    val event = Event(Color.GREEN, cal.timeInMillis, "")
                    compactCalendar.addEvent(event)
                }
            }

        })
        return events
    }
}