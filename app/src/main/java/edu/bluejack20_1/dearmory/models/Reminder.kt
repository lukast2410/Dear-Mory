package edu.bluejack20_1.dearmory.models

class Reminder {
    private lateinit var date: String
    private lateinit var id: String
    private lateinit var label: String
    private lateinit var repeat: String
    private lateinit var repeatDays: String
    private lateinit var time: String
    private lateinit var vibrate: String

    constructor() {

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