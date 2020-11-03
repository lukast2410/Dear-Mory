package edu.bluejack20_1.dearmory.models

import android.R
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.Serializable
import kotlin.coroutines.coroutineContext

class Reminder: Serializable {
    private lateinit var date: String
    private lateinit var id: String
    private lateinit var label: String
    private lateinit var repeat: String
    private lateinit var repeatDays: String
    private lateinit var time: String
    private lateinit var vibrate: String

    constructor() {

    }

    companion object{
        const val REMINDER = "Reminder"
    }

    fun getDate(): String{
        return this.date
    }

    fun setDate(newDate: String){
        this.date = newDate
    }

    fun getId(): String{
        return this.id
    }

    fun setId(newId: String){
        this.id = newId
    }
    fun getLabel(): String{
        return this.label
    }

    fun setLabel(newLabel: String){
        this.label = newLabel
    }
    fun getRepeat(): String{
        return this.repeat
    }

    fun setRepeat(newRepeat: String){
        this.repeat = newRepeat
    }
    fun getRepeatDays(): String{
        return this.repeatDays
    }

    fun setRepeatDays(newDays: String){
        this.repeatDays = newDays
    }
    fun getTime(): String{
        return this.time
    }

    fun setTime(newTime: String){
        this.time = newTime
    }
    fun getVibrate(): String{
        return this.vibrate
    }

    fun setVibrate(newVibrate: String){
        this.vibrate = newVibrate
    }
}