package edu.bluejack20_1.dearmory.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.models.Diary
import org.w3c.dom.Text

class DiaryAdapter(): RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {
    private lateinit var diaryModels: ArrayList<Diary>

    constructor(diaryModels: ArrayList<Diary>) : this() {
        this.diaryModels = diaryModels
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var diaryMood = itemView.findViewById(R.id.iv_diary_mood_item) as ImageView
        var diaryType = itemView.findViewById(R.id.iv_diary_type_item) as ImageView
        var diaryDate = itemView.findViewById(R.id.tv_diary_date_item) as TextView
        var diaryText = itemView.findViewById(R.id.tv_diary_text_item) as TextView
        var diaryAmount = itemView.findViewById(R.id.tv_diary_amount_item) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_expense_income, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = diaryModels.get(position)

    }

    override fun getItemCount(): Int {
        return diaryModels.size
    }
}