package edu.bluejack20_1.dearmory.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import edu.bluejack20_1.dearmory.models.Reminder
import edu.bluejack20_1.dearmory.repositories.ReminderRepo
import java.util.*
import kotlin.collections.ArrayList

class ReminderViewModel: ViewModel() {

    private lateinit var reminders: MutableLiveData<ArrayList<Reminder>>

    fun init(date: Date, userId: String){
        reminders = ReminderRepo.getInstance().getReminders(date, userId)
    }

    fun getAllEvent(compactCalendarView: CompactCalendarView, userId: String): ArrayList<java.util.Calendar>{
        return ReminderRepo.getInstance().getAllEvent(compactCalendarView, userId)
    }

    fun getReminders(): MutableLiveData<ArrayList<Reminder>>{
        return reminders
    }

//    fun setAlarm(context: Context, time:String){
//        ReminderRepo.getInstance().setAlarm(context, time)
//    }


}