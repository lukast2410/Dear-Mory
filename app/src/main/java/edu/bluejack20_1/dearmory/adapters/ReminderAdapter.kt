package edu.bluejack20_1.dearmory.adapters

import android.app.Dialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.os.Vibrator
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.fragments.CalendarFragment
import edu.bluejack20_1.dearmory.models.Reminder
import java.util.*

class ReminderAdapter : RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private lateinit var reminderList: ArrayList<Reminder>
    private lateinit var calendar: Calendar
    private lateinit var mListener: OnItemClickListener
    private lateinit var context: Context
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dialog: Dialog
    private lateinit var calendarFragment: CalendarFragment
    private lateinit var vibrator: Vibrator

    public interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    constructor(
        reminderModel: ArrayList<Reminder>,
        context: Context,
        calendarFragment: CalendarFragment
    ){
        this.reminderList = reminderModel
        this.context = context
        this.calendarFragment = calendarFragment
        this.dialog = Dialog(context)
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    class ViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView){
        val time: TextView = itemView.findViewById(R.id.time_label)
        val vibration: ImageView = itemView.findViewById(R.id.vibrate_button)
        val label: TextView = itemView.findViewById(R.id.reminder_label_label)
        val repeatedDays: TextView = itemView.findViewById(R.id.repetition_day_label)
//        val date: TextView = itemView.findViewById(R.id.reminder_date_label)
//        val repeatCheckBox: CheckBox = itemView.findViewById(R.id.repeat_check_box)

        val n = itemView.setOnClickListener {
            if(listener != null){
                val position: Int = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    listener.onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.reminder_item,parent,false)
        return ViewHolder(view, mListener)
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = reminderList[position]
        holder.time.text = reminderList[position].getTime()
        holder.label.text = reminderList[position].getLabel()
        holder.label.setTextSize(TypedValue.COMPLEX_UNIT_SP, (ThemeManager.TEXT_SIZE + 2).toFloat())
        holder.repeatedDays.text = repeatedDayFormat(reminderList[position].getRepeatDays())
//        holder.date.text = formatDate(reminderList[position].getDate())
        if(reminderList[position].getRepeat() == "on"){
//            holder.repeatCheckBox.isChecked = true
            holder.repeatedDays.visibility = View.VISIBLE
        }else if(reminderList[position].getRepeat() == "off"){
//            holder.repeatCheckBox.isChecked = false
            holder.repeatedDays.visibility = View.INVISIBLE
        }
        if(reminderList[position].getVibrate() == "on"){
            holder.vibration.setBackgroundResource(R.drawable.notifications_on)
        }else if(reminderList[position].getVibrate() == "off"){
            holder.vibration.setBackgroundResource(R.drawable.notifications_off)
        }

//        holder.date.setOnClickListener {
//            updateDate(holder, position, reminderList)
//        }
//        holder.time.setOnClickListener {
//            updateTime(holder, position, reminderList)
//        }
        holder.vibration.setOnClickListener {
            updateVibration(holder, position)
        }
//        holder.repeatCheckBox.setOnClickListener {
//            updateRepeat(holder, position)
//        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun updateDate(holder: ViewHolder, position: Int, reminderList: ArrayList<Reminder>){
        calendarFragment.showDatePickerDialog(holder, position, reminderList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTime(holder: ViewHolder, position: Int, reminderList: ArrayList<Reminder>){
        calendarFragment.showTimePickerDialog(holder, position, reminderList)
    }

    private fun updateVibration(holder: ViewHolder, position: Int){
        val id = reminderList[position].getId()
        val account = GoogleSignIn.getLastSignedInAccount(context)

        if (account != null) {
            databaseReference =
                account.id?.let { FirebaseDatabase.getInstance().getReference("Reminder").child(it).child(id) }!!
        }
        if(reminderList[position].getVibrate() == "on"){
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["vibrate"] = "off"
            vibrator.vibrate(300)
            databaseReference.updateChildren(hashMap as Map<String, Any>)
//                .addOnCompleteListener {
//                vibrator.vibrate(100)
//            }
        }else if (reminderList[position].getVibrate() == "off"){
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["vibrate"] = "on"
            databaseReference.updateChildren(hashMap as Map<String, Any>)
            vibrator.vibrate(700)
//                .addOnCompleteListener {
//                vibrator.vibrate(200)
//            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateRepeat(holder: ViewHolder, position: Int){
        val id = reminderList[position].getId()
        val account = GoogleSignIn.getLastSignedInAccount(context)

        if (account != null) {
            databaseReference =
                account.id?.let { FirebaseDatabase.getInstance().getReference("Reminder").child(it).child(id) }!!
        }

//        if(holder.repeatCheckBox.isChecked){
//            dayOfWeekPickerPopUp(holder, position, reminderList)
//            val hashMap: HashMap<String, String> = HashMap()
//            hashMap["repeat"] = "on"
//            databaseReference.updateChildren(hashMap as Map<String, Any>)
//        }else if(!holder.repeatCheckBox.isChecked){
//            val hashMap: HashMap<String, String> = HashMap()
//            hashMap["repeat"] = "off"
//            databaseReference.updateChildren(hashMap as Map<String, Any>)
//            updateDate(holder, position, reminderList)
//        }
    }

    private fun updateLabel(holder: ViewHolder, position: Int, reminderList: ArrayList<Reminder>){
        calendarFragment.showUpdateLabelDialog(holder, position, reminderList)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun dayOfWeekPickerPopUp(holder: ViewHolder, position: Int, reminderList: ArrayList<Reminder>){
        calendarFragment.showDayWeekPicker(holder, position, reminderList)
    }

    private fun formatDate(date: String): String {
        val n = date.split("/").map { it.toInt() }
        var tempDate = ""
        when (n[0]) {
            1 -> {
                tempDate = "Mon, "
            }
            2 -> {
                tempDate = "Tue, "
            }
            3 -> {
                tempDate = "Wed, "
            }
            4 -> {
                tempDate = "Thu, "
            }
            5 -> {
                tempDate = "Fri, "
            }
            6 -> {
                tempDate = "Sat, "
            }
            7 -> {
                tempDate = "Sun, "
            }
        }
        when (n[1]) {
            1 -> {
                tempDate += "Jan "
            }
            2 -> {
                tempDate += "Feb "
            }
            3 -> {
                tempDate += "Mar "
            }
            4 -> {
                tempDate += "Apr "
            }
            5 -> {
                tempDate += "May "
            }
            6 -> {
                tempDate += "Jun "
            }
            7 -> {
                tempDate += "Jul "
            }
            8 -> {
                tempDate += "Aug "
            }
            9 -> {
                tempDate += "Sep "
            }
            10 -> {
                tempDate += "Okt "
            }
            11 -> {
                tempDate += "Nov "
            }
            12 -> {
                tempDate += "Des "
            }
        }
        tempDate += n[2]

        return tempDate
    }

    private fun repeatedDayFormat(days: String): String{
        val n = days.split(",").map { it.toInt() }
        n.sorted()
        var tempDate = ""
        for (day in n){
            when (day) {
                1 -> {
                    tempDate += "Mo,"
                }
                2 -> {
                    tempDate += "Tu,"
                }
                3 -> {
                    tempDate += "We,"
                }
                4 -> {
                    tempDate += "Th,"
                }
                5 -> {
                    tempDate += "Fr,"
                }
                6 -> {
                    tempDate += "Sa,"
                }
                7 -> {
                    tempDate += "Su,"
                }
            }
        }
        tempDate = tempDate.substring(0, tempDate.length-1)
        return tempDate
    }

}