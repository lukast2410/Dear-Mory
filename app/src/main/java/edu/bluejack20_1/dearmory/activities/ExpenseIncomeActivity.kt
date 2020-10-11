package edu.bluejack20_1.dearmory.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.fragments.TimePickerDialogFragment
import edu.bluejack20_1.dearmory.fragments.TimePickerDialogFragment.TimePickerDialogListener
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import kotlinx.android.synthetic.main.activity_expense_income.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
class ExpenseIncomeActivity : AppCompatActivity() {
    private val EXPENSE_TYPE = "Expense"
    private val INCOME_TYPE = "Income"
    private lateinit var actType: String
    private lateinit var diaryId: String
    private lateinit var expenseIncome: ExpenseIncome
    private lateinit var expenseIncomeType: String
    private lateinit var dialog: Dialog
    private lateinit var refsDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeManager.setUpTheme())
        setContentView(R.layout.activity_expense_income)
        iv_main_background_expense_income.setImageResource(ThemeManager.setUpBackground())
        getExtraFromLastActivity()
        initializeToolbar()
        initializeTypePopUp()
        initializeTimePicker()
        setMoneyInput()
        saveExpenseIncome()
    }

    private fun saveExpenseIncome() {
        btn_save_expense_income.setOnClickListener{
            val check = validateMoneyAmount(til_money_amount.editText?.text.toString(), true)
            if(check){
                refsDB = FirebaseDatabase.getInstance().getReference("ExpenseIncome").child(diaryId)
                if(actType == ExpenseIncome.ADD_EXPENSE_INCOME){
                    createExpenseIncome()
                }else if(actType == ExpenseIncome.UPDATE_EXPENSE_INCOME){
                    updateExpenseIncome()
                }
                finish()
            }
        }
    }

    private fun updateExpenseIncome() {

    }

    private fun createExpenseIncome() {
        val notes = til_expense_income_notes.editText?.text.toString()
        var amount = til_money_amount.editText?.text.toString().toInt()
        val id: String = refsDB.push().key.toString()
        if(expenseIncomeType == EXPENSE_TYPE)
            amount *= (-1)
        expenseIncome.setId(id)
            .setAmount(amount)
            .setNotes(notes)

        refsDB.child(id).setValue(expenseIncome)
        Toast.makeText(applicationContext, getString(R.string.success_save_expense_income), Toast.LENGTH_SHORT).show()
    }

    private fun setMoneyInput() {
        til_money_amount.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateMoneyAmount(til_money_amount.editText?.text.toString(), false)
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun validateMoneyAmount(text: String, showToast: Boolean): Boolean {
        if(text.isNotBlank()){
            val amount = text.toInt()
            return if(amount <= 0){
                til_money_amount.isErrorEnabled = true
                til_money_amount.error = getString(R.string.input_amount_error)
                if(showToast)
                    Toast.makeText(applicationContext, getString(R.string.input_amount_error), Toast.LENGTH_SHORT).show()
                false
            }else{
                til_money_amount.isErrorEnabled = false
                true
            }
        }else{
            til_money_amount.isErrorEnabled = true
            til_money_amount.error = getString(R.string.input_amount_empty)
            if(showToast)
                Toast.makeText(applicationContext, getString(R.string.input_amount_empty), Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private fun initializeTimePicker() {
        val timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).toString()
        expenseIncome.setTime(timeNow)
        tv_time_picker.text = timeNow
        ll_time_picker_container.setOnClickListener(View.OnClickListener {
            val dialogFragment = TimePickerDialogFragment()
            dialogFragment.show(supportFragmentManager, "timePicker")
            dialogFragment.setListener(object : TimePickerDialogListener {
                @SuppressLint("SetTextI18n")
                override fun onTimePickerDialogTimeSet(hour: Int, minute: Int) {
                    tv_time_picker.text = "${hour}:${minute}"
                    expenseIncome.setTime("${hour}:${minute}")
                }
            })
        })
    }

    private fun initializeTypePopUp() {
        expenseIncomeType = EXPENSE_TYPE
        dialog = Dialog(this)
        dialog.setContentView(R.layout.expense_income_type_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val selectExpense = dialog.findViewById(R.id.select_expense_type) as TextView
        val selectIncome = dialog.findViewById(R.id.select_income_type) as TextView
        til_expense_income_type.setOnClickListener{ dialog.show() }
        et_expense_income_type.setOnClickListener { dialog.show() }
        selectExpense.setOnClickListener {
            til_expense_income_type.editText?.setText(R.string.expense)
            expenseIncomeType = EXPENSE_TYPE
            dialog.dismiss()
        }
        selectIncome.setOnClickListener {
            til_expense_income_type.editText?.setText(R.string.income)
            expenseIncomeType = INCOME_TYPE
            dialog.dismiss()
        }
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar_expense_income)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun getExtraFromLastActivity() {
        actType = intent.getStringExtra(ExpenseIncome.GO_TO_EXPENSE_INCOME).toString()
        diaryId = intent.getStringExtra(Diary.DIARY_ID).toString()
        expenseIncome = ExpenseIncome()
        if(intent.getStringExtra(ExpenseIncome.EXPENSE_INCOME_ID).toString().isNotBlank()){
            expenseIncome.setId(intent.getStringExtra(ExpenseIncome.EXPENSE_INCOME_ID).toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(actType.equals(ExpenseIncome.UPDATE_EXPENSE_INCOME)){
            menuInflater.inflate(R.menu.menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.toolbar_delete_menu){
            //delete
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}