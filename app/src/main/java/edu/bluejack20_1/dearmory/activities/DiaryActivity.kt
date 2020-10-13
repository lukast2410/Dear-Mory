package edu.bluejack20_1.dearmory.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.adapters.ExpenseIncomeAdapter
import edu.bluejack20_1.dearmory.factories.DiaryViewModelFactory
import edu.bluejack20_1.dearmory.factories.ExpenseIncomeViewModelFactory
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import edu.bluejack20_1.dearmory.repositories.DiaryRepository
import edu.bluejack20_1.dearmory.repositories.ExpenseIncomeRepository
import edu.bluejack20_1.dearmory.viewmodels.DiaryViewModel
import edu.bluejack20_1.dearmory.viewmodels.ExpenseIncomeViewModel
import kotlinx.android.synthetic.main.activity_app.iv_main_background
import kotlinx.android.synthetic.main.activity_diary.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
class DiaryActivity : AppCompatActivity(), ExpenseIncomeAdapter.ExpenseIncomeListener{
    private lateinit var dialog: Dialog
    private lateinit var refsDB: DatabaseReference
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var diary: Diary
    private lateinit var userId: String
    private lateinit var expenseIncomeAdapter: ExpenseIncomeAdapter
    private lateinit var expenseIncomeViewModel: ExpenseIncomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeManager.setUpTheme())
        setContentView(R.layout.activity_diary)
        iv_main_background.setImageResource(ThemeManager.setUpBackground())
        userId = GoogleSignIn.getLastSignedInAccount(applicationContext)?.id.toString()
        initializeDiary()
        initializeToolbar()
        initializePopUpEditMood()
    }

    private fun initializeDiary() {
        var success = false
        val factory = DiaryViewModelFactory(DiaryRepository.getInstance())
        diaryViewModel = ViewModelProviders.of(this, factory).get(DiaryViewModel::class.java)
        diaryViewModel.getDiary(userId).observe(this, Observer {d ->
            if(!d.getId().equals("false") && !success){
                diary = d
                setEditDiaryText()
                initializeExpenseIncomeRecyclerView()
                success = true
            }
        })
    }

    private fun initializeExpenseIncomeRecyclerView() {
        Log.d("checkview", diary.getMood())
        recycler_expense_income.setHasFixedSize(true)
        recycler_expense_income.layoutManager = LinearLayoutManager(this)
        val factory = ExpenseIncomeViewModelFactory(ExpenseIncomeRepository.getInstance())
        expenseIncomeViewModel = ViewModelProviders.of(this, factory).get(ExpenseIncomeViewModel::class.java)
        expenseIncomeViewModel.init(diary.getId())
        expenseIncomeAdapter = ExpenseIncomeAdapter(expenseIncomeViewModel.getExpenseIncomes()?.value!!, diary.getMood(), this)
        expenseIncomeViewModel.getExpenseIncomes().observe(this,
            Observer<ArrayList<ExpenseIncome>>{
                expenseIncomeAdapter.setDiaryMood(diary.getMood())
                expenseIncomeAdapter.notifyDataSetChanged()
                recycler_expense_income.adapter = expenseIncomeAdapter
            })
    }

    private fun setEditDiaryText() {
        et_edit_diary_text.setText(diary.getText())
        et_edit_diary_text.setSelection(et_edit_diary_text.length())
        et_edit_diary_text.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                diary.setText(et_edit_diary_text.text.toString().trim())
                diaryViewModel.saveDiary(userId, diary)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun initializePopUpEditMood() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.edit_mood_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val angryMood = dialog.findViewById(R.id.angry_mood) as ImageView
        val happyMood = dialog.findViewById(R.id.happy_mood) as ImageView
        val sadMood = dialog.findViewById(R.id.sad_mood) as ImageView
        fab_edit_mood.setOnClickListener { dialog.show() }
        angryMood.setOnClickListener {
            diary.setMood(Diary.ANGRY_MOOD)
            expenseIncomeAdapter.setDiaryMood(diary.getMood())
            expenseIncomeViewModel.reloadExpenseIncomes()
            diaryViewModel.saveDiary(userId, diary)
            Toast.makeText(applicationContext, "Angry Mood", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        happyMood.setOnClickListener {
            diary.setMood(Diary.HAPPY_MOOD)
            expenseIncomeAdapter.setDiaryMood(diary.getMood())
            expenseIncomeViewModel.reloadExpenseIncomes()
            diaryViewModel.saveDiary(userId, diary)
            Toast.makeText(applicationContext, "Happy Mood", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        sadMood.setOnClickListener {
            diary.setMood(Diary.SAD_MOOD)
            expenseIncomeAdapter.setDiaryMood(diary.getMood())
            expenseIncomeViewModel.reloadExpenseIncomes()
            diaryViewModel.saveDiary(userId, diary)
            Toast.makeText(applicationContext, "Sad Mood", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        fab_add_expense_income.setOnClickListener {
            val intent = Intent(applicationContext, ExpenseIncomeActivity::class.java)
            intent.putExtra(Diary.DIARY_ID, diary.getId())
            intent.putExtra(ExpenseIncome.GO_TO_EXPENSE_INCOME, ExpenseIncome.ADD_EXPENSE_INCOME)
            startActivity(intent)
        }
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar_diary)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onExpenseIncomeClick(position: Int) {
        Log.d("checkviewclick", "clicked")
        var temp = expenseIncomeViewModel.getExpenseIncomes().value?.get(position) as ExpenseIncome
        var intent: Intent = Intent(this, ExpenseIncomeActivity::class.java)
        intent.putExtra(ExpenseIncome.GO_TO_EXPENSE_INCOME, ExpenseIncome.UPDATE_EXPENSE_INCOME)
        intent.putExtra(Diary.DIARY_ID, diary.getId())
        intent.putExtra(ExpenseIncome.EXPENSE_INCOME, temp)
        startActivity(intent)
    }
}
