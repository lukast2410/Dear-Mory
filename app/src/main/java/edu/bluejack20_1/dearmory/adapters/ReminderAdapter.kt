package edu.bluejack20_1.dearmory.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.models.Reminder
import kotlinx.android.synthetic.main.reminder_item.view.*

class ReminderAdapter : RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private lateinit var reminderList: ArrayList<Reminder>

    constructor(reminderModel: ArrayList<Reminder>){
        this.reminderList = reminderModel
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val time: TextView = itemView.findViewById(R.id.time_label)
        val vibration: ImageView = itemView.findViewById(R.id.vibrate_button)
        val label: TextView = itemView.findViewById(R.id.reminder_label_label)
        val repeatedDays: TextView = itemView.findViewById(R.id.repetition_day_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.reminder_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = reminderList[position]
        holder.time.text = reminderList[position].getTime()
        holder.label.text = reminderList[position].getLabel()
        holder.repeatedDays.text = reminderList[position].getRepeatDays()
        if(reminderList[position].getVibrate() == "on"){
            holder.vibration.setImageResource(R.drawable.notifications_on)
        }else{
            holder.vibration.setImageResource(R.drawable.notifications_off)
        }
    }
}