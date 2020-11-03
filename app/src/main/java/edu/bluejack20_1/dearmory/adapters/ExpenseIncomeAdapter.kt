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

class ExpenseIncomeAdapter() :
    RecyclerView.Adapter<ExpenseIncomeAdapter.ViewHolder>() {
    private lateinit var expenseIncomeModels: ArrayList<ExpenseIncome>
    private lateinit var expenseIncomeListener: ExpenseIncomeListener
    private lateinit var diaryMood: String

    constructor(
        expenseIncomeModels: ArrayList<ExpenseIncome>,
        diaryMood: String,
        expenseIncomeListener: ExpenseIncomeListener
    ) : this() {
        this.expenseIncomeModels = expenseIncomeModels
        this.expenseIncomeListener = expenseIncomeListener
        this.diaryMood = diaryMood
    }

    class ViewHolder(itemView: View, expenseIncomeListener: ExpenseIncomeListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var listener = expenseIncomeListener
        var notes: TextView = itemView.findViewById(R.id.tv_expense_income_notes_item)
        var time: TextView = itemView.findViewById(R.id.tv_expense_income_time_item)
        var amount: TextView = itemView.findViewById(R.id.tv_expense_income_amount_item)
        var icon: ImageView = itemView.findViewById(R.id.iv_expense_income_type_item)
        var container: CardView = itemView.findViewById(R.id.cv_expense_income_container_item)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onExpenseIncomeClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_expense_income, parent, false)
        return ViewHolder(view, expenseIncomeListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = expenseIncomeModels[position]
        holder.notes.text = expenseIncomeModels[position].getNotes()
        holder.time.text = expenseIncomeModels[position].getTime()
        holder.amount.setTextSize(TypedValue.COMPLEX_UNIT_SP, (ThemeManager.TEXT_SIZE + 11).toFloat())
        if (expenseIncomeModels[position].getAmount().toInt() > 0) { //income
            val amount = expenseIncomeModels[position].getAmount().toLong()
            holder.amount.text = "Rp ${shortenComa(amount)}"
            holder.amount.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.defaultIncome
                )
            )
            holder.icon.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.defaultIncome
                )
            )
        } else { //expense
            val amount = (expenseIncomeModels[position].getAmount().toLong() * (-1))
            holder.amount.text = "Rp ${shortenComa(amount)}"
            holder.amount.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    getExpenseIconColor()
                )
            )
            holder.icon.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context,
                    getExpenseIconColor()
                )
            )
        }
        holder.container.setCardBackgroundColor(
            ContextCompat.getColor(
                holder.itemView.context,
                getContainerBackgroundColor()
            )
        )
    }

    override fun getItemCount(): Int {
        return expenseIncomeModels.size
    }

    private fun getExpenseIconColor(): Int {
        if (ThemeManager.THEME_INDEX == ThemeManager.DARK_THEME_INDEX)
            return R.color.expenseIconDarkTheme
        else if (ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
            return R.color.expenseIconLightTheme
        else
            return R.color.expenseIconGalaxyTheme
    }

    private fun getContainerBackgroundColor(): Int {
        if (ThemeManager.THEME_INDEX == ThemeManager.DARK_THEME_INDEX)
            return R.color.componentBgDarkTheme
        else if (ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
            return R.color.componentBgLightTheme
        else
            return R.color.componentBgGalaxyTheme
//        if (diaryMood == Diary.HAPPY_MOOD) {
//            if (ThemeManager.THEME_INDEX == ThemeManager.DARK_THEME_INDEX)
//                return R.color.happyMoodDarkTheme
//            else if (ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
//                return R.color.happyMoodLightTheme
//            else
//                return R.color.happyMoodGalaxyTheme
//        } else if (diaryMood == Diary.ANGRY_MOOD) {
//            if (ThemeManager.THEME_INDEX == ThemeManager.DARK_THEME_INDEX)
//                return R.color.angryMoodDarkTheme
//            else if (ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
//                return R.color.angryMoodLightTheme
//            else
//                return R.color.angryMoodGalaxyTheme
//        } else if (diaryMood == Diary.SAD_MOOD) {
//            if (ThemeManager.THEME_INDEX == ThemeManager.DARK_THEME_INDEX)
//                return R.color.sadMoodDarkTheme
//            else if (ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
//                return R.color.sadMoodLightTheme
//            else
//                return R.color.sadMoodGalaxyTheme
//        }
        return R.color.white
    }

    fun shortenComa(numb: Long): String {
        var numbW = numb.toString();
        var length = numb.toString().length;
        var word = "";
        for (i: Int in (length - 3) downTo 0 step 3) {
            if (i <= 0)
                break
            word = ".${numbW.substring(i, length)}${word}"
            length -= 3
        }
        var mod = numb.toString().length % 3;
        word = if (mod == 0) {
            numbW.substring(0, 3) + word;
        } else {
            numbW.substring(0, mod) + word;
        }
        return word;
    }

    fun setDiaryMood(diaryMood: String) {
        this.diaryMood = diaryMood
    }

    interface ExpenseIncomeListener {
        fun onExpenseIncomeClick(position: Int)
    }
}
