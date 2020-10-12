package edu.bluejack20_1.dearmory.adapters

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.graphics.ColorFilter
import android.media.Image
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import kotlinx.android.synthetic.main.item_expense_income.view.*

class ExpenseIncomeAdapter(private var diaryMood: String): RecyclerView.Adapter<ExpenseIncomeAdapter.ViewHolder>() {
    private lateinit var expenseIncomeModels: ArrayList<ExpenseIncome>

    constructor(expenseIncomeModels: ArrayList<ExpenseIncome>, diaryMood: String) : this(diaryMood) {
        this.expenseIncomeModels = expenseIncomeModels
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var notes: TextView = itemView.findViewById(R.id.tv_expense_income_notes_item)
        var time: TextView = itemView.findViewById(R.id.tv_expense_income_time_item)
        var amount: TextView = itemView.findViewById(R.id.tv_expense_income_amount_item)
        var icon: ImageView = itemView.findViewById(R.id.iv_expense_income_type_item)
        var container: CardView = itemView.findViewById(R.id.cv_expense_income_container_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_expense_income, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setTag(expenseIncomeModels.get(position))
        holder.notes.setText(expenseIncomeModels.get(position).getNotes())
        holder.time.setText(expenseIncomeModels.get(position).getTime())
        if (expenseIncomeModels.get(position).getAmount().toInt() > 0){
            holder.amount.setText("Rp. ${expenseIncomeModels.get(position).getAmount()}")
            holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.defaultIncome))
        }else{
            holder.amount.setText("Rp. ${(expenseIncomeModels.get(position).getAmount().toInt()*(-1))}")
            if(ThemeManager.THEME_INDEX == ThemeManager.DARK_THEME_INDEX)
                holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.expenseIconDarkTheme))
            else if(ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
                holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.expenseIconLightTheme))
            else
                holder.icon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.expenseIconGalaxyTheme))

        }
        if (diaryMood == Diary.HAPPY_MOOD){
            if(ThemeManager.THEME_INDEX == ThemeManager.DARK_THEME_INDEX)
                holder.container.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.happyMoodDarkTheme))
            else if(ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
                holder.container.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.happyMoodLightTheme))
            else
                holder.container.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.happyMoodGalaxyTheme))
        }else if (diaryMood == Diary.ANGRY_MOOD){
            if(ThemeManager.THEME_INDEX == ThemeManager.DARK_THEME_INDEX)
                if(ThemeManager.THEME_INDEX == ThemeManager.DARK_THEME_INDEX)
                    holder.container.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.angryMoodDarkTheme))
                else if(ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
                    holder.container.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.angryMoodLightTheme))
                else
                    holder.container.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.angryMoodGalaxyTheme))
        }else if(diaryMood == Diary.SAD_MOOD){
            if(ThemeManager.THEME_INDEX == ThemeManager.DARK_THEME_INDEX)
                if(ThemeManager.THEME_INDEX == ThemeManager.DARK_THEME_INDEX)
                    holder.container.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.sadMoodDarkTheme))
                else if(ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
                    holder.container.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.sadMoodLightTheme))
                else
                    holder.container.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.sadMoodGalaxyTheme))
        }
    }

    override fun getItemCount(): Int {
        return expenseIncomeModels.size
    }
}
