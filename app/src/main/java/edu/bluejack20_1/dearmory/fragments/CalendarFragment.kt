package edu.bluejack20_1.dearmory.fragments

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.SensorManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.adapters.ReminderAdapter
import edu.bluejack20_1.dearmory.models.Reminder
import edu.bluejack20_1.dearmory.modelsViews.ReminderViewModel
import edu.bluejack20_1.dearmory.receivers.AlertReceiver
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.text.SimpleDateFormat
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
        reminderViewModel.init()
        reminderAdapter = ReminderAdapter(reminderViewModel.getReminders().value!!)
        reminderViewModel.getReminders().observe(viewLifecycleOwner, Observer<ArrayList<Reminder>> {
            reminderAdapter.notifyDataSetChanged()
            reminder_recycler_view.adapter = reminderAdapter
        })

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
                val hashMap: HashMap<String, String> = HashMap<String, String>()
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


}
