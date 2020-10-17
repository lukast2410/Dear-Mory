package edu.bluejack20_1.dearmory.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Transformations.map
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.internal.ListenerHolder
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.fragments.CalendarFragment
import edu.bluejack20_1.dearmory.models.Reminder
import edu.bluejack20_1.dearmory.models.User
import kotlinx.android.synthetic.main.day_of_week_picker.*
import java.util.HashMap

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
        val date: TextView = itemView.findViewById(R.id.reminder_date_label)
        val repeatCheckBox: CheckBox = itemView.findViewById(R.id.repeat_check_box)

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
        holder.repeatedDays.text = reminderList[position].getRepeatDays()
        holder.date.text = reminderList[position].getDate()
        if(reminderList[position].getRepeat() == "on"){
            holder.repeatCheckBox.isChecked = true
            holder.repeatedDays.visibility = View.VISIBLE
        }else if(reminderList[position].getRepeat() == "off"){
            holder.repeatCheckBox.isChecked = false
            holder.repeatedDays.visibility = View.INVISIBLE
        }
        if(reminderList[position].getVibrate() == "on"){
            holder.vibration.setImageResource(R.drawable.notifications_on)
        }else if(reminderList[position].getVibrate() == "off"){
            holder.vibration.setImageResource(R.drawable.notifications_off)
        }

        holder.date.setOnClickListener {
            updateDate(holder, position, reminderList)
        }
        holder.time.setOnClickListener {
            updateTime(holder, position, reminderList)
        }
        holder.vibration.setOnClickListener {
            updateVibration(holder, position)
        }
        holder.repeatCheckBox.setOnClickListener {
            updateRepeat(holder, position)
        }
        holder.label.setOnClickListener {
            updateLabel(holder, position, reminderList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun updateDate(holder: ViewHolder, position: Int, reminderList: ArrayList<Reminder>){
        calendarFragment.showDatePickerDialog(holder, position, reminderList)
    }

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
            vibrator.vibrate(100)
            databaseReference.updateChildren(hashMap as Map<String, Any>)
//                .addOnCompleteListener {
//                vibrator.vibrate(100)
//            }
        }else if (reminderList[position].getVibrate() == "off"){
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["vibrate"] = "on"
            databaseReference.updateChildren(hashMap as Map<String, Any>)
            vibrator.vibrate(200)
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

        if(holder.repeatCheckBox.isChecked){
            dayOfWeekPickerPopUp(holder, position, reminderList)
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["repeat"] = "on"
            databaseReference.updateChildren(hashMap as Map<String, Any>)
        }else if(!holder.repeatCheckBox.isChecked){
            val hashMap: HashMap<String, String> = HashMap()
            hashMap["repeat"] = "off"
            databaseReference.updateChildren(hashMap as Map<String, Any>)
            updateDate(holder, position, reminderList)
        }
    }

    private fun updateLabel(holder: ViewHolder, position: Int, reminderList: ArrayList<Reminder>){
        calendarFragment.showUpdateLabelDialog(holder, position, reminderList)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun dayOfWeekPickerPopUp(holder: ViewHolder, position: Int, reminderList: ArrayList<Reminder>){
        calendarFragment.showDayWeekPicker(holder, position, reminderList)
    }

}