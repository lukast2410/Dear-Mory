package edu.bluejack20_1.dearmory.modelsViews

import android.content.Context
import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import edu.bluejack20_1.dearmory.adapters.ReminderAdapter
import edu.bluejack20_1.dearmory.models.Reminder
import edu.bluejack20_1.dearmory.repository.ReminderRepo
import java.util.*
import kotlin.collections.ArrayList

class ReminderViewModel: ViewModel() {

    private lateinit var reminders: MutableLiveData<ArrayList<Reminder>>

    fun init(date: Date){
        reminders = ReminderRepo.getInstance().getReminders(date)
    }

    fun getAllEvent(compactCalendarView: CompactCalendarView): ArrayList<java.util.Calendar>{
        return ReminderRepo.getInstance().getAllEvent(compactCalendarView)
    }

    fun getReminders(): MutableLiveData<ArrayList<Reminder>>{
        return reminders
    }
}