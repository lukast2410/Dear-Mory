package edu.bluejack20_1.dearmory.customs

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import kotlinx.android.synthetic.main.layout_custom_marker_view.view.*
import kotlin.math.roundToLong

class CustomMarkerView(context: Context?, layoutResource: Int) : MarkerView(context, layoutResource) {
    private var mOffset: MPPointF = MPPointF((-(width/2)).toFloat(), (-(height*6/5)).toFloat())

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val markerContent = findViewById<TextView>(R.id.tv_marker_view_content)
        val value = e?.y!!.roundToLong()
        markerContent.text = ExpenseIncome.shortenComa(value)
        markerContent.setTextColor(ContextCompat.getColor(context, getExpenseIncomeColor(value)))
        super.refreshContent(e, highlight)
        mOffset.x = -(width/2).toFloat()
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return mOffset
    }

    private fun getExpenseIncomeColor(value: Long): Int {
        if (value < 0)
            return R.color.defaultExpense
        else if (value > 0)
            return R.color.defaultIncome
        if (ThemeManager.THEME_INDEX == ThemeManager.LIGHT_THEME_INDEX)
            return R.color.black
        return R.color.white
    }
}