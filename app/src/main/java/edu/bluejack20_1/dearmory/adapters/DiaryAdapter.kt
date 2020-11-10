package edu.bluejack20_1.dearmory.adapters

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class DiaryAdapter() : RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {
    private lateinit var diaryModels: ArrayList<Diary>
    private lateinit var totalModels: ArrayList<HashMap<String, ExpenseIncome>>
    private lateinit var diaryClickListener: DiaryClickListener

    constructor(
        diaryModels: ArrayList<Diary>,
        totalModels: ArrayList<HashMap<String, ExpenseIncome>>,
        diaryClickListener: DiaryClickListener
    ) : this() {
        this.diaryModels = diaryModels
        this.totalModels = totalModels
        this.diaryClickListener = diaryClickListener
    }

    class ViewHolder(itemView: View, diaryClickListener: DiaryClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var listener = diaryClickListener
        var diaryContainer = itemView.findViewById(R.id.cv_diary_container_item) as CardView
        var diaryMood = itemView.findViewById(R.id.iv_diary_mood_item) as ImageView
        var diaryType = itemView.findViewById(R.id.iv_diary_type_item) as ImageView
        var diaryDate = itemView.findViewById(R.id.tv_diary_date_item) as TextView
        var diaryText = itemView.findViewById(R.id.tv_diary_text_item) as TextView
        var diaryAmount = itemView.findViewById(R.id.tv_diary_amount_item) as TextView

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onDiaryClicked(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_diary, parent, false)
        return ViewHolder(view, diaryClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var total: Long = 0
        if (totalModels.size > position) {
            total = totalModels[position].map { exp -> exp.value.getAmount().toLong() }.sum()
        }
        holder.itemView.tag = diaryModels[position]
        holder.diaryDate.text = getDiaryDate(diaryModels[position].getDate())
        holder.diaryAmount.text = getDiaryTotalAmount(position, total)
        holder.diaryText.text = getDiaryText(diaryModels[position].getText())
        holder.diaryMood.setImageResource(getMoodResource(diaryModels[position].getMood()))
        holder.diaryContainer.setCardBackgroundColor(
            ContextCompat.getColor(
                holder.itemView.context,
                getDiaryContainerColor(diaryModels[position].getMood())
            )
        )
        holder.diaryType.setColorFilter(
            ContextCompat.getColor(
                holder.itemView.context,
                getExpenseIncomeColor(position, total)
            )
        )
        holder.diaryAmount.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                getExpenseIncomeColor(position, total)
            )
        )
    }

    override fun getItemCount(): Int {
        return diaryModels.size
    }

    private fun getDiaryDate(date: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val localDate = LocalDate.parse(date) as LocalDate
        return localDate.format(formatter).toString()
    }

    private fun getDiaryTotalAmount(position: Int, total: Long): String {
        if (totalModels.size > position) {
            var finalTotal = total
            if (finalTotal < 0)
                finalTotal *= (-1)
            return "Rp. " + ExpenseIncome.shortenComa(finalTotal)
        }
        return "Loading..."
    }

    private fun getDiaryText(text: String): String {
        val maxLength = 96 - (2 * ThemeManager.TEXT_SIZE)
        var finalText: String = text
        val firstEnter = finalText.indexOfFirst { c -> c == '\n' }
        if (firstEnter >= 0){
            val minus = finalText.length - firstEnter
            if(minus >= (maxLength/2))
                finalText = finalText.substring(0, firstEnter+(maxLength/2))
            val nextEnter = finalText.indexOf('\n', firstEnter+1)
            if(nextEnter >= 0)
                finalText = finalText.substring(0, nextEnter)
        }
        if (finalText.length > maxLength)
            finalText = finalText.substring(0, maxLength)
        if (finalText != text)
            finalText = "$finalText..."
        return finalText
    }

    private fun getMoodResource(mood: String): Int {
        return when (mood) {
            Diary.HAPPY_MOOD -> R.drawable.mood_happy
            Diary.ANGRY_MOOD -> R.drawable.mood_angry
            else -> R.drawable.mood_sad
        }
    }

    private fun getDiaryContainerColor(mood: String): Int {
        return when (mood) {
            Diary.HAPPY_MOOD ->
                return if(ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
                    R.color.happyDiaryContainerLight
                else R.color.happyDiaryContainerDark
            Diary.ANGRY_MOOD ->
                return if(ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
                    R.color.angryDiaryContainerLight
                else R.color.angryDiaryContainerDark
            else ->
                return if(ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
                    R.color.sadDiaryContainerLight
                else R.color.sadDiaryContainerDark
        }
    }

    private fun getExpenseIncomeColor(position: Int, total: Long): Int {
        if (totalModels.size > position) {
            if (total < 0)
                return R.color.defaultExpense
            else if (total > 0)
                return R.color.defaultIncome
        }
        if (ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
            return R.color.black
        return R.color.white
    }

    interface DiaryClickListener {
        fun onDiaryClicked(position: Int)
    }
}