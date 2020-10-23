package edu.bluejack20_1.dearmory.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.activities.DiaryActivity
import edu.bluejack20_1.dearmory.adapters.DiaryAdapter
import edu.bluejack20_1.dearmory.factories.DiaryViewModelFactory
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import edu.bluejack20_1.dearmory.models.Image
import edu.bluejack20_1.dearmory.repositories.DiaryRepository
import edu.bluejack20_1.dearmory.viewmodels.DiaryViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.channels.consumesAll
import java.time.LocalDate
import java.util.function.Predicate

@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment(), DiaryAdapter.DiaryClickListener {
    private lateinit var userId: String
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var diariesAdapter: DiaryAdapter
    private var month: Int = 0
    private var year: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = GoogleSignIn.getLastSignedInAccount(context)?.id.toString()
        initializeDate()
        initializeButton()
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
        diariesAdapter = DiaryAdapter(diaryViewModel.getDiaries().value!!, diaryViewModel.getTotals().value!!, this)
        diaryViewModel.getDiaries().observe(viewLifecycleOwner, Observer<ArrayList<Diary>> {
            diariesAdapter.notifyDataSetChanged()
            rv_diaries_container.adapter = diariesAdapter
        })
        diaryViewModel.getTotals().observe(viewLifecycleOwner, Observer<ArrayList<HashMap<String, ExpenseIncome>>> {
            diariesAdapter.notifyDataSetChanged()
            rv_diaries_container.adapter = diariesAdapter
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
            initializeDate()
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
            DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                month = i2 + 1
                var tempMonth = "$month"
                var tempDay = "$i3"
                if(month < 10)
                    tempMonth = "0$month"
                if(i3 < 10)
                    tempDay = "0$i3"
                var date = "$i-$tempMonth-$tempDay"
                var intent = Intent(context, DiaryActivity::class.java)
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra(Diary.SEND_DIARY_TYPE, Diary.WRITE_DIARY)
                intent.putExtra(Diary.DATE_DIARY, date)
                startActivity(intent)
            }
            ,year,month,day) }
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
}