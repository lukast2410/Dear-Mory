package edu.bluejack20_1.dearmory.fragments

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.adapters.ReminderAdapter
import edu.bluejack20_1.dearmory.models.Reminder
import edu.bluejack20_1.dearmory.modelsViews.ReminderViewModel
import edu.bluejack20_1.dearmory.receivers.AlertReceiver
import kotlinx.android.synthetic.main.day_of_week_picker.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.util.*
import kotlin.collections.ArrayList

class CalendarFragment : Fragment(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var reminderViewModel: ReminderViewModel

    private lateinit var calendar: Calendar
    private lateinit var timeToNotify: String
    private lateinit var dialog: Dialog
    private lateinit var databaseReference: DatabaseReference
    private lateinit var choosenDate: String
    private lateinit var choosenTime: String

    private var th: Int = 0
    private  var tm: Int = 0
    private  var ts: Int = 0
    private  var ty: Int = 0
    private  var tmonth: Int = 0
    private  var td: Int = 0

    private lateinit var compactCalendar: CompactCalendarView
    @RequiresApi(Build.VERSION_CODES.N)
    private var dateFormatMonth: SimpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    companion object {
        fun newInstance() : CalendarFragment {
            return CalendarFragment()
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = context?.let { Dialog(it) }!!

        reminder_recycler_view.setHasFixedSize(true)
        reminder_recycler_view.layoutManager = LinearLayoutManager(context)
        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel::class.java)
        reminderViewModel.init(Date())
        reminderAdapter = ReminderAdapter(reminderViewModel.getReminders().value!!, context!!, this)
        reminderViewModel.getReminders().observe(viewLifecycleOwner, Observer<ArrayList<Reminder>> {
            reminderAdapter.notifyDataSetChanged()
            reminder_recycler_view.adapter = reminderAdapter
        })
        reminderAdapter.setOnItemClickListener(object : ReminderAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                reminderViewModel.getReminders().value!![position].updateDate(position.toString())
                reminderAdapter.notifyItemChanged(position)
            }

        })
        setUpCalendar()

        fab_add_reminder.setOnClickListener {
            calendar = Calendar.getInstance()

            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = context?.let { it1 -> DatePickerDialog(it1, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                    month = i2 + 1
                    val date = "$i3/$month/$i"
                    openTimePicker(hour, minute)
                    choosenDate = date
                    ty = i
                    tmonth = month
                    td = i3
                }
                ,year,month,day) }
            if (datePickerDialog != null) {
                datePickerDialog.datePicker.minDate = (calendar.time.time - (calendar.time.time % (24*60*60*1000)))
                datePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                datePickerDialog.show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun openTimePicker(hour: Int, minute: Int){
        val timePickerDialog = TimePickerDialog(context,TimePickerDialog.OnTimeSetListener(){ timePicker: TimePicker, i: Int, i1: Int ->
            timeToNotify = "$i:$i1"
//            add_reminder_button.text = "$i:$i1"
            choosenTime = "$i:$i1"
            th = i
            tm = i1
            showLabelPopUp()
        },hour,minute,false)
//        timePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePickerDialog.show()
    }

    fun formatTime(hour: Int, minute: Int): String{
        var time: String = ""
        var formattedMinute: String = ""

        if(minute / 10 == 0){
            formattedMinute = "0$minute"
        }else{
            formattedMinute = "" + minute
        }

        if(hour == 0){
            time = "12:$formattedMinute AM"
        }else if(hour < 12){
            time = "$hour:$formattedMinute AM"
        }else if(hour == 12){
            time = "12:$formattedMinute PM"
        }else{
            val temp: Int = hour - 12
            time = "$temp:$formattedMinute PM"
        }
        return time
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showLabelPopUp(){
        dialog.setContentView(R.layout.reminder_label_pop_up)
        val label = dialog.findViewById<EditText>(R.id.label_reminder_field)
        label.filters = arrayOf<InputFilter>(LengthFilter(18))
        val saveBtn = dialog.findViewById<Button>(R.id.save_label_reminder_logo)
        saveBtn.setOnClickListener {
            saveReminder()
        }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun saveReminder(){
        val label = dialog.findViewById<EditText>(R.id.label_reminder_field).text
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if(account != null && label != null){
            if(label.isNotBlank()){
                databaseReference = FirebaseDatabase.getInstance().getReference("Reminder").child(
                    account.id!!
                )
                val hashMap: HashMap<String, String> = HashMap()
                hashMap["label"] = label.toString()
                hashMap["date"] = choosenDate
                hashMap["time"] = choosenTime
                hashMap["vibrate"] = "on"
                hashMap["repeat"] = "off"
                hashMap["repeatDays"] = "1,2,3,4,5,6,7"
                val id = databaseReference.push().key.toString()
                hashMap["id"] = id
                databaseReference.child(id).setValue(hashMap as Map<String, Any>).addOnCompleteListener {
                    dialog.dismiss()
                }
            }
        }else{
        }

        val alarmManager: AlarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0)
        val n: Calendar = Calendar.getInstance()
        n.set(Calendar.HOUR_OF_DAY, th)
        n.set(Calendar.MINUTE, tm)
        n.set(Calendar.YEAR, ty)
        n.set(Calendar.MONTH, tmonth)
        n.set(Calendar.DAY_OF_MONTH, td)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, n.timeInMillis, pendingIntent)

//        val n = "1,2,3,4,5,6,7".split(",").map { it.toInt() }
//        for(t in n){
//            Log.d("trial", t.toString())
//        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setUpCalendar(){
        val actionBar: ActionBar? = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.title = null
        compactCalendar = (activity as AppCompatActivity).findViewById(R.id.compactcalendar_view) as CompactCalendarView
        compactCalendar.setUseThreeLetterAbbreviation(true)
        compactCalendar.shouldDrawIndicatorsBelowSelectedDays(true)
        compactCalendar.setFirstDayOfWeek(Calendar.SUNDAY)

        setCalendarEvent()

        compactCalendar.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                val context: Context = (activity as AppCompatActivity).applicationContext
                if(dateClicked.toString().compareTo("Thu Oct 2020 00:00:00 AST 2016") == 0){
                    Log.d("cal", "Todays")
                }else{
                    Log.d("cal", "not Todays")
                }
                reminder_recycler_view.setHasFixedSize(true)
                reminder_recycler_view.layoutManager = LinearLayoutManager(context)
                reminderViewModel = ViewModelProviders.of(this@CalendarFragment).get(ReminderViewModel::class.java)
                if (dateClicked != null) {
                    reminderViewModel.init(dateClicked)
                }
                reminderAdapter = ReminderAdapter(reminderViewModel.getReminders().value!!, context, this@CalendarFragment)
                reminderViewModel.getReminders().observe(viewLifecycleOwner, Observer<ArrayList<Reminder>> {
                    reminderAdapter.notifyDataSetChanged()
                    reminder_recycler_view.adapter = reminderAdapter
                })
                reminderAdapter.setOnItemClickListener(object : ReminderAdapter.OnItemClickListener{
                    override fun onItemClick(position: Int) {
                        reminderViewModel.getReminders().value!![position].updateDate(position.toString())
                        reminderAdapter.notifyItemChanged(position)
                    }
                })
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                month_indicator.text = dateFormatMonth.format(firstDayOfNewMonth)
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun showDatePickerDialog(holder: ReminderAdapter.ViewHolder, position: Int, reminderList: ArrayList<Reminder>){
        calendar = Calendar.getInstance()

        val n = holder.date.text.split("/").map { it.toInt() }
        val year = n[2]
        var month = n[1] - 1
        val day = n[0]
        val id = reminderList[position].getId()
        val account = GoogleSignIn.getLastSignedInAccount(context)
        val datePickerDialog = context?.let { it1 -> DatePickerDialog(it1, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                month = i2 + 1
                val date = "$i3/$month/$i"
                if(date.isNotBlank()){
                    if (account != null) {
                        databaseReference =
                            account.id?.let { FirebaseDatabase.getInstance().getReference("Reminder").child(it).child(id) }!!
                    }
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["date"] = date
                    databaseReference.updateChildren(hashMap as Map<String, Any>)
                    val hashMap2: HashMap<String, String> = HashMap()
                    hashMap2["repeat"] = "off"
                    databaseReference.updateChildren(hashMap2 as Map<String, Any>)
                }
            }
            ,year,month,day) }
        datePickerDialog?.datePicker?.minDate = (calendar.time.time - (calendar.time.time % (24*60*60*1000)))
        datePickerDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        datePickerDialog?.show()
    }

    fun showTimePickerDialog(holder: ReminderAdapter.ViewHolder, position: Int, reminderList: ArrayList<Reminder>){
        val n = holder.time.text.split(":").map { it.toInt() }
        val hour = n[0]
        val minute = n[1]
        val id = reminderList[position].getId()
        val account = GoogleSignIn.getLastSignedInAccount(context)

        val timePickerDialog = TimePickerDialog(context,
            TimePickerDialog.OnTimeSetListener(){ timePicker: TimePicker, i: Int, i1: Int ->
                val time = "$i:$i1"
                if(time.isNotBlank()){
                    if (account != null) {
                        databaseReference =
                            account.id?.let { FirebaseDatabase.getInstance().getReference("Reminder").child(it).child(id) }!!
                    }
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["time"] = time
                    databaseReference.updateChildren(hashMap as Map<String, Any>)
                }
            },hour,minute,false)
//        timePickerDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        timePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun showDayWeekPicker(holder: ReminderAdapter.ViewHolder, position: Int, reminderList: ArrayList<Reminder>){
        dialog.setContentView(R.layout.day_of_week_picker)
        var n: MutableList<Int> = mutableListOf()
        if(holder.repeatedDays.text.isNotBlank()){
            n = holder.repeatedDays.text.split(",").map { it.toInt() }.toMutableList()
            for(d in n){
                if(d == 1){
                    dialog.findViewById<TextView>(R.id.mo_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }else if(d == 2){
                    dialog.findViewById<TextView>(R.id.tu_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }else if(d == 3){
                    dialog.findViewById<TextView>(R.id.we_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }else if(d == 4){
                    dialog.findViewById<TextView>(R.id.th_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }else if(d == 5){
                    dialog.findViewById<TextView>(R.id.fr_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }else if(d == 6){
                    dialog.findViewById<TextView>(R.id.sa_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }else if(d == 7){
                    dialog.findViewById<TextView>(R.id.su_day).setBackgroundResource(R.drawable.ico_day_picker_active)
                }
            }
        }
        setClickListener(holder, n, position, reminderList)
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setClickListener(holder: ReminderAdapter.ViewHolder, n: MutableList<Int>, position: Int, reminderList: ArrayList<Reminder>){
        dialog.su_day.setOnClickListener {
            if(n.contains(7)){
                dialog.su_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(7)
            }else{
                dialog.su_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(7)
            }
            for(i in n){
                Log.d("bg", i.toString())
            }
        }
        dialog.mo_day.setOnClickListener {
            if(n.contains(1)){
                dialog.mo_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(1)
            }else{
                dialog.mo_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(1)
            }
            for(i in n){
                Log.d("bg", i.toString())
            }
        }
        dialog.tu_day.setOnClickListener {
            if(n.contains(2)){
                dialog.tu_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(2)
            }else{
                dialog.tu_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(2)
            }
            for(i in n){
                Log.d("bg", i.toString())
            }
        }
        dialog.we_day.setOnClickListener {
            if(n.contains(3)){
                dialog.we_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(3)
            }else{
                dialog.we_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(3)
            }
            for(i in n){
                Log.d("bg", i.toString())
            }
        }
        dialog.th_day.setOnClickListener {
            if(n.contains(4)){
                dialog.th_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(4)
            }else{
                dialog.th_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(4)
            }
            for(i in n){
                Log.d("bg", i.toString())
            }
        }
        dialog.fr_day.setOnClickListener {
            if(n.contains(5)){
                dialog.fr_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(5)
            }else{
                dialog.fr_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(5)
            }
            for(i in n){
                Log.d("bg", i.toString())
            }
        }
        dialog.sa_day.setOnClickListener {
            if(n.contains(6)){
                dialog.sa_day.setBackgroundResource(R.drawable.ico_day_picker_non_active)
                n.remove(6)
            }else{
                dialog.sa_day.setBackgroundResource(R.drawable.ico_day_picker_active)
                n.add(6)
            }
            for(i in n){
                Log.d("bg", i.toString())
            }
        }

        dialog.save_repeated_day_reminder_logo.setOnClickListener {
            val id = reminderList[position].getId()
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null) {
                databaseReference =
                    account.id?.let { FirebaseDatabase.getInstance().getReference("Reminder").child(it).child(id) }!!
                val hashMap: HashMap<String, String> = HashMap()
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
                    showDatePickerDialog(holder, position, reminderList)
                }
                hashMap["repeatDays"] = temp
                databaseReference.updateChildren(hashMap as Map<String, String>).addOnCompleteListener {
                    dialog.dismiss()
                }
            }
        }
    }

    fun showUpdateLabelDialog(holder: ReminderAdapter.ViewHolder, position: Int, reminderList: ArrayList<Reminder>){
        dialog.setContentView(R.layout.reminder_label_pop_up)
        val label = dialog.findViewById<EditText>(R.id.label_reminder_field)
        label.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(18))
        val saveBtn = dialog.findViewById<Button>(R.id.save_label_reminder_logo)
        label.setText(reminderList[position].getLabel(), TextView.BufferType.EDITABLE)
        saveBtn.setOnClickListener {
            val id = reminderList[position].getId()
            val account = GoogleSignIn.getLastSignedInAccount(context)

            if (account != null) {
                databaseReference =
                    account.id?.let { FirebaseDatabase.getInstance().getReference("Reminder").child(it).child(id) }!!
                val hashMap: HashMap<String, String> = HashMap()
                hashMap["label"] = label.text.toString()
                databaseReference.updateChildren(hashMap as Map<String, Any>).addOnCompleteListener {
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setCalendarEvent(){
        val events: ArrayList<java.util.Calendar> = reminderViewModel.getAllEvent(compactCalendar)
    }

}
