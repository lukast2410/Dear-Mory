package edu.bluejack20_1.dearmory.activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.fragments.TimePickerDialogFragment
import edu.bluejack20_1.dearmory.fragments.TimePickerDialogFragment.TimePickerDialogListener
import kotlinx.android.synthetic.main.activity_expense_income.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
class ExpenseIncomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeManager.setUpTheme())
        setContentView(R.layout.activity_expense_income)
        iv_main_background_expense_income.setImageResource(ThemeManager.setUpBackground())
        initializeToolbar()
        initializeSpinner()
        initializeTimePicker()
        setMoneyInput()
    }

    private fun setMoneyInput() {
        til_money_amount.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateMoneyAmount(til_money_amount.editText?.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun validateMoneyAmount(text: String) {
        if(text.length > 0){
            var amount = text.toInt()
            if(amount <= 0){
                til_money_amount.isErrorEnabled = true
                til_money_amount.error = getString(R.string.input_amount_error)
            }else
                til_money_amount.isErrorEnabled = false
        }else{
            til_money_amount.isErrorEnabled = true
            til_money_amount.error = getString(R.string.input_amount_error)
        }
    }

    private fun initializeTimePicker() {
        val timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).toString()
        tv_time_picker.text = timeNow
        tv_time_picker.setOnClickListener(View.OnClickListener {
            val dialogFragment = TimePickerDialogFragment()
            dialogFragment.show(supportFragmentManager, "timePicker")
            dialogFragment.setListener(object : TimePickerDialogListener {
                @SuppressLint("SetTextI18n")
                override fun onTimePickerDialogTimeSet(hour: Int, minute: Int) {
                    tv_time_picker.text = "${hour}:${minute}"
                }
            })
        })
    }

    private fun initializeSpinner() {
        val adapter = ArrayAdapter<String>(
            this,
            R.layout.custom_spinner,
            resources.getStringArray(R.array.expense_income_type)
        )
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown)
        spinner_expense_income_type.adapter = adapter
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar_expense_income)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        Toast.makeText(this, R.string.add_expense_income, Toast.LENGTH_SHORT).show()
    }
}