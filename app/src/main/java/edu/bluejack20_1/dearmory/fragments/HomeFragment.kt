package edu.bluejack20_1.dearmory.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.activities.DiaryActivity
import edu.bluejack20_1.dearmory.adapters.DiaryAdapter
import edu.bluejack20_1.dearmory.customs.CustomMarkerView
import edu.bluejack20_1.dearmory.factories.DiaryViewModelFactory
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import edu.bluejack20_1.dearmory.repositories.DiaryRepository
import edu.bluejack20_1.dearmory.viewmodels.DiaryViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import java.time.LocalDate
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment(), DiaryAdapter.DiaryClickListener{
    private lateinit var userId: String
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var diariesAdapter: DiaryAdapter
    private lateinit var totalValues: ArrayList<Entry>
    private lateinit var totalDataSet: LineDataSet
    private lateinit var lineData: LineData
    private var widthScreen: Int = 0
    private var month: Int = 0
    private var year: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = GoogleSignIn.getLastSignedInAccount(context)?.id.toString()
        initializeDate()
        initializeButton()
        initializeExpenseIncomeChart()
    }

    private fun initializeDate() {
        val date = LocalDate.now()
        year = date.year
        month = date.monthValue
        cv_next_month_diary.visibility = View.GONE
        initializeDiaryRecyclerView()
    }

    private fun initializeDiaryRecyclerView() {
        var date = "$year-$month"
        if(month < 10)
            date = "$year-0$month"
        rv_diaries_container.setHasFixedSize(true)
        rv_diaries_container.layoutManager = LinearLayoutManager(context)
        val factory = DiaryViewModelFactory(DiaryRepository.getInstance())
        diaryViewModel = ViewModelProviders.of(this, factory).get(DiaryViewModel::class.java)
        diaryViewModel.init(userId, date)
        diariesAdapter = DiaryAdapter(
            diaryViewModel.getDiaries().value!!,
            diaryViewModel.getTotals().value!!,
            this
        )
        diaryViewModel.getDiaries().observe(viewLifecycleOwner, Observer<ArrayList<Diary>> {
            diariesAdapter.notifyDataSetChanged()
            rv_diaries_container.adapter = diariesAdapter
        })
        diaryViewModel.getTotals().observe(
            viewLifecycleOwner,
            Observer<ArrayList<HashMap<String, ExpenseIncome>>> {
                diariesAdapter.notifyDataSetChanged()
                rv_diaries_container.adapter = diariesAdapter
                updateLineChartValues()
            })
    }

    private fun initializeButton() {
        fab_write_diary.setOnClickListener {
            writeDiary()
        }
        cv_previous_month_diary.setOnClickListener {
            prevMonth()
        }
        cv_next_month_diary.setOnClickListener {
            nextMonth()
        }
        cv_this_month_diary.setOnClickListener {
            thisMonth()
        }
    }

    private fun writeDiary(){
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        var theme = R.style.DatePickerDark
        when (ThemeManager.THEME_INDEX) {
            ThemeManager.LIGHT_THEME_INDEX -> theme = R.style.DatePickerLight
            ThemeManager.GALAXY_THEME_INDEX -> theme = R.style.DatePickerGalaxy
        }
        val datePickerDialog = context?.let { it1 -> DatePickerDialog(it1, theme,
            { _, i, i2, i3 ->
                month = i2 + 1
                var tempMonth = "$month"
                var tempDay = "$i3"
                if (month < 10)
                    tempMonth = "0$month"
                if (i3 < 10)
                    tempDay = "0$i3"
                var date = "$i-$tempMonth-$tempDay"
                var intent = Intent(context, DiaryActivity::class.java)
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra(Diary.SEND_DIARY_TYPE, Diary.WRITE_DIARY)
                intent.putExtra(Diary.DATE_DIARY, date)
                startActivity(intent)
            }, year, month, day) }
        if (datePickerDialog != null) {
            datePickerDialog.datePicker.maxDate = (calendar.time.time - (calendar.time.time % (24*60*60*1000)))
            datePickerDialog.show()
        }
    }

    private fun prevMonth() {
        month -= 1
        if(month < 1){
            month += 12
            year -= 1
        }
        val date = LocalDate.now()
        if(year != date.year || month < date.monthValue){
            cv_next_month_diary.visibility = View.VISIBLE
        }
        initializeDiaryRecyclerView()
    }

    private fun nextMonth() {
        month += 1
        if (month > 12){
            month -= 12
            year += 1
        }
        val date = LocalDate.now()
        if(year == date.year && month == date.monthValue){
            cv_next_month_diary.visibility = View.GONE
        }
        initializeDiaryRecyclerView()
    }

    private fun thisMonth(){
        val date = LocalDate.now()
        if(month != date.monthValue || year != date.year)
            initializeDate()
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onDiaryClicked(position: Int) {
        var selectedDiary: Diary = diaryViewModel.getDiaries().value!![position]
        var intent = Intent(context, DiaryActivity::class.java)
        intent.putExtra(Diary.SEND_DIARY_TYPE, Diary.SELECT_DIARY)
        intent.putExtra(Diary.DIARY, selectedDiary)
        startActivity(intent)
    }

    private fun initializeExpenseIncomeChart() {
        initializeLineChartView()
        initializeTotalValuesBase()
        initializeTotalDataSet()
        initializeAxisLeftRight()

        lineData = LineData(totalDataSet)
        lc_expense_income_chart.data = lineData
        initializeXAxis()
        lc_expense_income_chart.setVisibleXRangeMaximum(6.2F)
    }

    private fun initializeLineChartView(){
        lc_expense_income_chart.setTouchEnabled(true)
        lc_expense_income_chart.isDragEnabled = false
        lc_expense_income_chart.setScaleEnabled(false)
        lc_expense_income_chart.legend.isEnabled = false
        lc_expense_income_chart.description.isEnabled = false

        lc_expense_income_chart.marker = CustomMarkerView(
            context,
            R.layout.layout_custom_marker_view
        )
        lc_expense_income_chart.extraBottomOffset = 2F;
        lc_expense_income_chart.animateXY(2000, 2000)

        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getRealMetrics(metrics)
        widthScreen = metrics.widthPixels
        lc_expense_income_chart.layoutParams.width = widthScreen
        lc_expense_income_chart.isAutoScaleMinMaxEnabled = true
    }

    private fun initializeTotalValuesBase(){
        totalValues = ArrayList()
        for (x in 1..31){
            totalValues.add(Entry((x-1).toFloat(), 0F))
        }
    }

    private fun initializeTotalDataSet(){
        totalDataSet = LineDataSet(totalValues, "Expense Income")
        totalDataSet.fillAlpha = 1000
        totalDataSet.lineWidth = 3.5f
        totalDataSet.color = ContextCompat.getColor(context as Context, getLineChartColor())
        totalDataSet.setCircleColor(ContextCompat.getColor(context as Context, getLineChartColor()))
        totalDataSet.circleRadius = (ThemeManager.TEXT_SIZE/3).toFloat()
        totalDataSet.circleHoleRadius = (ThemeManager.TEXT_SIZE/6).toFloat()
        totalDataSet.setCircleColorHole(ContextCompat.getColor(context as Context, getLineChartHoleColor()))
        totalDataSet.highlightLineWidth = 2F
        totalDataSet.highLightColor = ContextCompat.getColor(context as Context, getLineChartHighlightColor())
        totalDataSet.setDrawHorizontalHighlightIndicator(false)
        totalDataSet.setDrawValues(false)
        totalDataSet.setDrawIcons(true)
    }

    private fun initializeAxisLeftRight(){
        val axisRight = lc_expense_income_chart.axisRight
        axisRight.setDrawGridLines(true)
        axisRight.setDrawAxisLine(false)
        axisRight.setDrawLabels(false)
        axisRight.gridColor = ContextCompat.getColor(context as Context, getLineChartGridColor())
        axisRight.gridLineWidth = 0.6F

        val axisLeft = lc_expense_income_chart.axisLeft
        axisLeft.setDrawLabels(false)
        axisLeft.setDrawGridLines(true)
        axisLeft.setDrawAxisLine(false)
        axisLeft.gridColor = ContextCompat.getColor(context as Context, getLineChartGridColor())
        axisLeft.gridLineWidth = 0.6F
    }

    private fun initializeXAxis(){
        val xAxis = lc_expense_income_chart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.spaceMax = 0.2f
        xAxis.spaceMin = 0.2f
        xAxis.textColor = ContextCompat.getColor(context as Context, getLineChartGridColor())
        xAxis.setDrawAxisLine(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setLabelCount(31, false)

        val xValues: ArrayList<String> = ArrayList()
        for (x in 1..31){
            xValues.add("$x")
        }

        xAxis.valueFormatter = MyXAxisValueFormatter(xValues)
        xAxis.granularity = 1F
        xAxis.textSize = ThemeManager.TEXT_SIZE.toFloat()
    }

    private fun updateLineChartValues(){
        for(x: Int in 1..31){
            totalValues[x-1].y = 0F
        }
        val totals = diaryViewModel.getTotals().value!!
        val diaries = diaryViewModel.getDiaries().value!!
        var date: LocalDate = LocalDate.now()
        val monthNow = date.monthValue
        val yearNow = date.year
        val dayNow = date.dayOfMonth
        for(idx: Int in 0 until diaries.size){
            val total = totals[idx].map { exp -> exp.value.getAmount().toLong() }.sum().toFloat()
            date = LocalDate.parse(diaries[idx].getDate())
            totalValues[date.dayOfMonth-1].y = total
        }
        if(date.monthValue == monthNow && date.year == yearNow){
            updateLineChartView(dayNow)
            totalValues[dayNow-1].icon = ContextCompat.getDrawable(context as Context, getTodayIcon())
            totalValues[dayNow-1].icon.alpha = 255
        } else {
            updateLineChartView(date.month.length(date.isLeapYear))
            totalValues[dayNow-1].icon.alpha = 0
        }
    }

    private fun updateLineChartView(lastDay: Int){
        if(lastDay >= 14){
            lc_expense_income_chart.setVisibleXRangeMinimum((lastDay-1).toFloat() + 0.2F)
            lc_expense_income_chart.setVisibleXRangeMaximum((lastDay-1).toFloat() + 0.2F)
        }else{
            lc_expense_income_chart.setVisibleXRangeMinimum(13.2F)
            lc_expense_income_chart.setVisibleXRangeMaximum(13.2F)
        }
        lc_expense_income_chart.layoutParams.width = widthScreen*2
        lc_expense_income_chart.notifyDataSetChanged()
        lc_expense_income_chart.invalidate()
    }

    private fun getLineChartColor(): Int{
        return when(ThemeManager.THEME_INDEX){
            ThemeManager.DARK_THEME_INDEX -> R.color.backgroundForIconDarkTheme
            ThemeManager.LIGHT_THEME_INDEX -> R.color.backgroundForIconLightTheme
            ThemeManager.GALAXY_THEME_INDEX -> R.color.navBackgroundColorGalaxyTheme
            else -> R.color.backgroundForIconDarkTheme
        }
    }

    private fun getLineChartHighlightColor(): Int{
        return when(ThemeManager.THEME_INDEX){
            ThemeManager.DARK_THEME_INDEX -> R.color.navBackgroundColorDarkTheme
            ThemeManager.LIGHT_THEME_INDEX -> R.color.navBackgroundColorLightTheme
            ThemeManager.GALAXY_THEME_INDEX -> R.color.backgroundForIconGalaxyTheme
            else -> R.color.navBackgroundColorDarkTheme
        }
    }

    private fun getLineChartHoleColor(): Int{
        return when(ThemeManager.THEME_INDEX){
            ThemeManager.LIGHT_THEME_INDEX -> R.color.white
            else -> R.color.black
        }
    }

    private fun getLineChartGridColor(): Int{
        return when(ThemeManager.THEME_INDEX){
            ThemeManager.LIGHT_THEME_INDEX -> R.color.black
            else -> R.color.white
        }
    }

    private fun getTodayIcon(): Int{
        return when(ThemeManager.THEME_INDEX){
            ThemeManager.LIGHT_THEME_INDEX -> R.drawable.ic_radio_button_black
            else -> R.drawable.ic_radio_button
        }
    }

    private class MyXAxisValueFormatter(private val values: ArrayList<String>): IAxisValueFormatter{

        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            return values[value.roundToInt()]
        }
    }
}