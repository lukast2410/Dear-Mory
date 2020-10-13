package edu.bluejack20_1.dearmory.modelsViews

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import edu.bluejack20_1.dearmory.adapters.ReminderAdapter
import edu.bluejack20_1.dearmory.models.Reminder
import edu.bluejack20_1.dearmory.repository.ReminderRepo

class ReminderViewModel: ViewModel() {

    private lateinit var reminders: MutableLiveData<ArrayList<Reminder>>

    fun init(){
        reminders = ReminderRepo.getInstance().getReminders()
    }

    fun getReminders(): MutableLiveData<ArrayList<Reminder>>{
        return reminders
    }
}