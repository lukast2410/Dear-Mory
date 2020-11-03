package edu.bluejack20_1.dearmory.repository

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.models.Reminder
import edu.bluejack20_1.dearmory.models.User
import edu.bluejack20_1.dearmory.notifications.NotificationChannelApp
import edu.bluejack20_1.dearmory.receivers.AlertReceiver
import java.util.*
import kotlin.collections.ArrayList

class ReminderRepo: AppCompatActivity() {

    var reminders: ArrayList<Reminder> = ArrayList()
    val reminder: MutableLiveData<ArrayList<Reminder>> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

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

    @RequiresApi(Build.VERSION_CODES.N)
    fun setAlarm(context: Context, time : String){
        val calendar: android.icu.util.Calendar = android.icu.util.Calendar.getInstance()
//        val n = time.split(":").map { it.toInt() }
        calendar.set(android.icu.util.Calendar.HOUR_OF_DAY, 21)
        calendar.set(android.icu.util.Calendar.MINUTE, 39)
        calendar.set(android.icu.util.Calendar.SECOND, 0)

        val intent: Intent = Intent(context, AlertReceiver::class.java)
        val pendingIntent : PendingIntent = PendingIntent.getBroadcast(context, 100, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }
}