package edu.bluejack20_1.dearmory.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.factories.ExpenseIncomeViewModelFactory
import edu.bluejack20_1.dearmory.fragments.TimePickerDialogFragment
import edu.bluejack20_1.dearmory.fragments.TimePickerDialogFragment.TimePickerDialogListener
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import edu.bluejack20_1.dearmory.repositories.ExpenseIncomeRepository
import edu.bluejack20_1.dearmory.viewmodels.ExpenseIncomeViewModel
import kotlinx.android.synthetic.main.activity_expense_income.*
import org.w3c.dom.Text
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class ExpenseIncomeActivity : AppCompatActivity() {
    private val EXPENSE_TYPE = "Expense"
    private val INCOME_TYPE = "Income"
    private lateinit var actType: String
    private lateinit var diaryId: String
    private lateinit var expenseIncome: ExpenseIncome
    private lateinit var expenseIncomeType: String
    private lateinit var expenseIncomeViewModel: ExpenseIncomeViewModel
    private lateinit var dialog: Dialog
    private lateinit var confirmDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeManager.setUpTheme())
        setContentView(R.layout.activity_expense_income)
        iv_main_background_expense_income.setImageResource(ThemeManager.setUpBackground())
        getExtraFromLastActivity()
        initializeViewModel()
        initializeToolbar()
        initializeTypePopUp()
        initializeTimePicker()
        initializeDeleteConfirmation()
        setMoneyInput()
        saveExpenseIncome()
    }

    private fun initializeViewModel() {
        val factory = ExpenseIncomeViewModelFactory(ExpenseIncomeRepository.getInstance())
        expenseIncomeViewModel = ViewModelProviders.of(this, factory).get(ExpenseIncomeViewModel::class.java)
    }

    private fun saveExpenseIncome() {
        btn_save_expense_income.setOnClickListener{
            val check = validateMoneyAmount(til_money_amount.editText?.text.toString(), true)
            if(check){
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
        val notes = til_expense_income_notes.editText?.text.toString()
        var amount = til_money_amount.editText?.text.toString().toLong()
        Log.d("checkviewamount", amount.toString())
        if(expenseIncomeType == EXPENSE_TYPE)
            amount *= (-1)
        Log.d("checkviewamount", amount.toString())
        Log.d("checkviewtype", expenseIncomeType)
        expenseIncome.setNotes(notes)
        expenseIncome.setAmount(amount)
        expenseIncomeViewModel.updateExpenseIncome(diaryId, expenseIncome)
        Toast.makeText(applicationContext, getString(R.string.success_save_expense_income), Toast.LENGTH_SHORT).show()
    }

    private fun createExpenseIncome() {
        val notes = til_expense_income_notes.editText?.text.toString()
        var amount = til_money_amount.editText?.text.toString().toLong()
        if(expenseIncomeType == EXPENSE_TYPE)
            amount *= (-1)
        expenseIncome.setAmount(amount)
            .setNotes(notes)

        expenseIncomeViewModel.createExpenseIncome(diaryId, expenseIncome)
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
            if(text.length > 18){
                til_money_amount.isErrorEnabled = true
                til_money_amount.error = getString(R.string.input_amount_too_big)
                if(showToast)
                    Toast.makeText(applicationContext, getString(R.string.input_amount_too_big), Toast.LENGTH_SHORT).show()
                return false
            }
            val amount = text.toLong()
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
        ll_time_picker_container.setOnClickListener(View.OnClickListener {
            var dialogFragment: TimePickerDialogFragment
            val time = LocalTime.parse(expenseIncome.getTime(), DateTimeFormatter.ofPattern("HH:mm")) as LocalTime
            dialogFragment = TimePickerDialogFragment(time.hour, time.minute)
            dialogFragment.show(supportFragmentManager, "timePicker")
            dialogFragment.setListener(object : TimePickerDialogListener {
                @SuppressLint("SetTextI18n")
                override fun onTimePickerDialogTimeSet(hour: Int, minute: Int) {
                    var finalHour: String = hour.toString()
                    var finalMinute: String = minute.toString()
                    if(hour < 10)
                        finalHour = "0${hour}"
                    if(minute < 10)
                        finalMinute = "0${minute}"
                    tv_time_picker.text = "$finalHour:$finalMinute"
                    expenseIncome.setTime("$finalHour:$finalMinute")
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

    private fun initializeDeleteConfirmation(){
        confirmDialog = Dialog(this)
        confirmDialog.setContentView(R.layout.confirmation_dialog)
        confirmDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogMessage = confirmDialog.findViewById(R.id.tv_confirmation_message) as TextView
        val confirmYes = confirmDialog.findViewById(R.id.btn_confirm_yes) as Button
        val confirmNo = confirmDialog.findViewById(R.id.btn_confirm_no) as Button
        dialogMessage.text = getString(R.string.confirm_delete_expense_income)
        confirmYes.setOnClickListener(View.OnClickListener {
            expenseIncomeViewModel.deleteExpenseIncome(diaryId, expenseIncome)
            finish()
        })
        confirmNo.setOnClickListener(View.OnClickListener {
            confirmDialog.dismiss()
        })
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar_expense_income)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun getExtraFromLastActivity() {
        actType = intent.getStringExtra(ExpenseIncome.GO_TO_EXPENSE_INCOME).toString()
        diaryId = intent.getStringExtra(Diary.DIARY_ID).toString()
        val timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).toString()
        if(actType == ExpenseIncome.UPDATE_EXPENSE_INCOME){
            expenseIncome = intent.getSerializableExtra(ExpenseIncome.EXPENSE_INCOME) as ExpenseIncome
            setExpenseIncomeData()
        }else{
            expenseIncome = ExpenseIncome()
            expenseIncome.setTime(timeNow)
            tv_time_picker.text = timeNow
        }
    }

    private fun setExpenseIncomeData() {
        if (expenseIncome.getAmount().toLong() < 0){
            til_expense_income_type.editText?.setText(R.string.expense)
            til_money_amount.editText?.setText((expenseIncome.getAmount().toLong() * -1).toString())
            expenseIncomeType = EXPENSE_TYPE
        }else{
            til_expense_income_type.editText?.setText(R.string.income)
            til_money_amount.editText?.setText(expenseIncome.getAmount().toString())
            expenseIncomeType = INCOME_TYPE
        }
        til_expense_income_notes.editText?.setText(expenseIncome.getNotes())
        tv_time_picker.text = expenseIncome.getTime()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(actType == ExpenseIncome.UPDATE_EXPENSE_INCOME){
            menuInflater.inflate(R.menu.menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.toolbar_delete_menu){
            confirmDialog.show()
        }
        return super.onOptionsItemSelected(item)
    }
}