package edu.bluejack20_1.dearmory.activities

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.adapters.ReminderAdapter
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import edu.bluejack20_1.dearmory.models.Reminder
import edu.bluejack20_1.dearmory.network.MyFirebaseMessagingService
import edu.bluejack20_1.dearmory.receivers.AlertReceiver
import kotlinx.android.synthetic.main.activity_expense_income.*
import kotlinx.android.synthetic.main.activity_reminder.*
import kotlinx.android.synthetic.main.day_of_week_picker.*
import kotlinx.android.synthetic.main.reminder_label_pop_up.*
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class ReminderActivity : AppCompatActivity() {

    private lateinit var reminder: Reminder
    private lateinit var labelField: EditText
    private lateinit var dateLabel: TextView
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var vibrateSwitch: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var repeatSwitch: Switch
    private lateinit var databaseReference: DatabaseReference
    private lateinit var calendar: Calendar
    private lateinit var time: String
    private lateinit var confirmDialog: Dialog
    private lateinit var repeatedDays: String
    private lateinit var n: MutableList<Int>
    private lateinit var isVibrate: String
    private lateinit var tempID: String

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        reminder_time_picker.setIs24HourView(true)
        initializeSwitchRepeat()
        if(intent.getSerializableExtra("isCreate") == 1){
            initializeToolbar()
            createNewReminder()
        }else{
            initReminder()
            initializeToolbar()
            fetchReminderData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeSwitchRepeat() {
        repeat_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
                ll_repeat_days_container.visibility = View.VISIBLE
            else
                ll_repeat_days_container.visibility = View.GONE
        }

        var hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var minute = Calendar.getInstance().get(Calendar.MINUTE)
        var finalHour: String = hour.toString()
        var finalMinute: String = minute.toString()

        if(hour < 10)
            finalHour = "0${hour}"
        if(minute < 10)
            finalMinute = "0${minute}"
        time = "$finalHour:$finalMinute"
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initReminder(){
        reminder = intent.getSerializableExtra(Reminder.REMINDER) as Reminder
        save_reminder_button.setOnClickListener {
            updateReminder()
        }
        reminder_edit_date.setOnClickListener {
            setDate()
        }
        reminder_time_picker.setOnTimeChangedListener { timePicker, hour, minute ->
            var finalHour: String = hour.toString()
            var finalMinute: String = minute.toString()
            if(hour < 10)
                finalHour = "0${hour}"
            if(minute < 10)
                finalMinute = "0${minute}"
            time = "$finalHour:$finalMinute"
        }
        initializeDeleteConfirmation()
        setDayWeek()
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar_reminder)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun fetchReminderData(){
        //time
        val n = reminder.getTime().split(":").map { it.toInt() }
        val hour = n[0]
        val minute = n[1]
        reminder_time_picker.minute = minute
        reminder_time_picker.hour = hour

        //label
        labelField = findViewById<EditText>(R.id.reminder_edit_label_field)
        labelField.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(20))
        labelField.isSingleLine = true
        labelField.setText(reminder.getLabel(), TextView.BufferType.EDITABLE)

        //date
        dateLabel = findViewById<TextView>(R.id.reminder_edit_date)
        dateLabel.text = reminder.getDate()

        //vibrate
        vibrateSwitch = findViewById<Switch>(R.id.vibrate_switch)
        if(reminder.getVibrate() == "on"){
            vibrateSwitch.isChecked = true
        }else if(reminder.getVibrate() == "off"){
            vibrateSwitch.isChecked = false
        }

        //repeat
        repeatSwitch = findViewById<Switch>(R.id.repeat_switch)
        if(reminder.getRepeat() == "on"){
            repeatSwitch.isChecked = true
            ll_repeat_days_container.visibility = View.VISIBLE
        }else if(reminder.getRepeat() == "off"){
            repeatSwitch.isChecked = false
            ll_repeat_days_container.visibility = View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateReminder(){
        tempID = reminder.getId()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account != null){
            databaseReference = FirebaseDatabase.getInstance().getReference("Reminder").child(account.id!!).child(reminder.getId())
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["label"] = labelField.text.toString()
            hashMap["date"] = dateLabel.text.toString()
            hashMap["time"] = time
            if(vibrateSwitch.isChecked){
                hashMap["vibrate"] = "on"
                isVibrate = "on"
            }else if(!vibrateSwitch.isChecked){
                hashMap["vibrate"] = "off"
                isVibrate = "off"
            }
            if(repeatSwitch.isChecked){
                hashMap["repeat"] = "on"
                ll_repeat_days_container.visibility = View.VISIBLE
            }else if(!repeatSwitch.isChecked){
                hashMap["repeat"] = "off"
                ll_repeat_days_container.visibility = View.GONE
            }
            var temp: String = ""
            for(i in n){
                temp += "$i,"
            }
            if(temp.isNotBlank()){
                temp = temp.substring(0, temp.length-1)
            }else if(temp.isBlank()){
                val hashMapRep: HashMap<String, String> = HashMap()
                hashMapRep["repeat"] = "off"
                databaseReference.updateChildren(hashMapRep as Map<String, Any>)
            }
            hashMap["repeatDays"] = temp
            databaseReference.updateChildren(hashMap as Map<String, Any>).addOnCompleteListener {
                setAlarm()
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setDate(){
        calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                month = i2 + 1
                val date = "$i3/$month/$i"
                reminder_edit_date.text = date
            }
            ,year,month,day)
        datePickerDialog.datePicker.minDate = (calendar.time.time - (calendar.time.time % (24*60*60*1000)))
        datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        datePickerDialog.show()
    }

    private fun initializeDeleteConfirmation(){
        confirmDialog = Dialog(this)
        confirmDialog.setContentView(R.layout.confirmation_dialog)
        confirmDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogMessage = confirmDialog.findViewById(R.id.tv_confirmation_message) as TextView
        val confirmYes = confirmDialog.findViewById(R.id.btn_confirm_yes) as Button
        val confirmNo = confirmDialog.findViewById(R.id.btn_confirm_no) as Button
        dialogMessage.text = getString(R.string.confirm_delete_expense_income)
        confirmYes.setOnClickListener{
            deleteReminder()
            finish()
        }
        confirmNo.setOnClickListener{
            confirmDialog.dismiss()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(intent.getSerializableExtra("isCreate") != 1){
            if(item.itemId == R.id.toolbar_delete_menu){
                confirmDialog.show()
            }
            return super.onOptionsItemSelected(item)
        }
        return false
    }

    private fun deleteReminder(){
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account != null) {
            databaseReference =
                FirebaseDatabase.getInstance().getReference("Reminder").child(account.id!!).child(reminder.getId())
            databaseReference.removeValue()
        }
    }

    private fun setDayWeek(){
        n = mutableListOf()
        if(reminder.getRepeatDays().isNotBlank()){
            n = reminder.getRepeatDays().split(",").map { it.toInt() }.toMutableList()
            for(d in n){
                if(d == 1){
                    findViewById<TextView>(R.id.mo_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }else if(d == 2){
                    findViewById<TextView>(R.id.tu_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }else if(d == 3){
                    findViewById<TextView>(R.id.we_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }else if(d == 4){
                    findViewById<TextView>(R.id.th_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }else if(d == 5){
                    findViewById<TextView>(R.id.fr_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }else if(d == 6){
                    findViewById<TextView>(R.id.sa_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }else if(d == 7){
                    findViewById<TextView>(R.id.su_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }
            }
        }
        setClickListener(n)
    }

    private fun setClickListener(n: MutableList<Int>) {
        su_day.setOnClickListener {
            if (n.contains(7)) {
                su_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(7)
            } else {
                su_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(7)
            }
        }
        mo_day.setOnClickListener {
            if (n.contains(1)) {
                mo_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(1)
            } else {
                mo_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(1)
            }
        }
        tu_day.setOnClickListener {
            if (n.contains(2)) {
                tu_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(2)
            } else {
                tu_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(2)
            }
        }
        we_day.setOnClickListener {
            if (n.contains(3)) {
                we_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(3)
            } else {
                we_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(3)
            }
        }
        th_day.setOnClickListener {
            if (n.contains(4)) {
                th_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(4)
            } else {
                th_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(4)
            }
        }
        fr_day.setOnClickListener {
            if (n.contains(5)) {
                fr_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(5)
            } else {
                fr_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(5)
            }
        }
        sa_day.setOnClickListener {
            if (n.contains(6)) {
                sa_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(6)
            } else {
                sa_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(6)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun createNewReminder(){
        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        reminder_edit_date.text = "$day/$month/$year"

        val label = findViewById<EditText>(R.id.reminder_edit_label_field)
        label.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(20))
        label.isSingleLine = true

        save_reminder_button.setOnClickListener {
            insertReminderData()
        }
        reminder_edit_date.setOnClickListener {
            setDate()
        }
        reminder_time_picker.setOnTimeChangedListener { timePicker, hour, minute ->
            var finalHour: String = hour.toString()
            var finalMinute: String = minute.toString()
            if(hour < 10)
                finalHour = "0${hour}"
            if(minute < 10)
                finalMinute = "0${minute}"
            time = "$finalHour:$finalMinute"
        }
        n = mutableListOf()
        setClickListener(n)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun insertReminderData(){
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account != null && reminder_edit_label_field.text != null){
            if(reminder_edit_label_field.text.isNotBlank()){
                databaseReference = FirebaseDatabase.getInstance().getReference("Reminder").child(
                    account.id!!
                )
                val hashMap: HashMap<String, String> = HashMap()
                hashMap["label"] = reminder_edit_label_field.text.toString()
                hashMap["date"] = reminder_edit_date.text.toString()
                hashMap["time"] = time
                if(vibrate_switch.isChecked){
                    hashMap["vibrate"] = "on"
                    isVibrate = "on"
                }else if(!vibrate_switch.isChecked){
                    hashMap["vibrate"] = "off"
                    isVibrate = "off"
                }
                if(repeat_switch.isChecked){
                    hashMap["repeat"] = "on"
                }else if(!repeat_switch.isChecked){
                    hashMap["repeat"] = "off"
                }
                var temp: String = ""
                for(i in n){
                    temp += "$i,"
                }
                if(temp.isNotBlank()){
                    temp = temp.substring(0, temp.length-1)
                }else if(temp.isBlank()){
                    hashMap["repeat"] = "off"
                    temp = "1,2,3,4,5,6,7"
                }
                hashMap["repeatDays"] = temp
                val id = databaseReference.push().key.toString()
                hashMap["id"] = id
                tempID = id
                databaseReference.child(id).setValue(hashMap as Map<String, Any>).addOnCompleteListener {
                    setAlarm()
                    finish()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setAlarm(){
//        offReminder(applicationContext)
        val calendar: Calendar = Calendar.getInstance()
        val t = time.split(":").map { it.toInt() }
        val d = reminder_edit_date.text.toString().split("/").map { it.toInt() }
//        for (i: Int in 0..(d.size-1)){
//            Log.d("asd $i", d[i].toString())
//        }
//        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.YEAR, d[2])
        calendar.set(Calendar.MONTH, d[1]-1)
        calendar.set(Calendar.DAY_OF_MONTH, d[0])
        calendar.set(Calendar.HOUR_OF_DAY, t[0])
        calendar.set(Calendar.MINUTE, t[1])
        calendar.set(Calendar.SECOND, 0)

        val intent: Intent = Intent(applicationContext, AlertReceiver::class.java)
        intent.putExtra("label", reminder_edit_label_field.text.toString())
        intent.putExtra("remID", tempID)

        if(isVibrate == "on"){
            intent.putExtra("vibrate", "on")
        }else if(isVibrate == "off"){
            intent.putExtra("vibrate", "off")
        }

        val pendingIntent : PendingIntent = PendingIntent.getBroadcast(applicationContext, Calendar.getInstance().timeInMillis.toInt(), intent, 0)
        val alarmManager : AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if(repeat_switch.isChecked){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//        Log.d("asd", reminder_edit_date.text.toString())
//        Log.d("asd", calendar.get(Calendar.YEAR).toString())
//        Log.d("asd", calendar.get(Calendar.MONTH).toString())
//        Log.d("asd", calendar.get(Calendar.DAY_OF_MONTH).toString())
//        Log.d("asd", calendar.get(Calendar.HOUR_OF_DAY).toString())
//        Log.d("asd", calendar.get(Calendar.MINUTE).toString())
//        Log.d("asd", calendar.get(Calendar.SECOND).toString())
//        Log.d("asd", calendar.timeInMillis.toString())
//        var ms = Calendar.getInstance().timeInMillis
//        Log.d("asdms", ms.toString())
    }

    fun offReminder(ctx: Context){
        var intent: Intent = Intent(ctx, AlertReceiver::class.java)
        var pendingIntent = PendingIntent.getBroadcast(ctx, 1, intent, 0)
        var alarmManager = ctx.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}