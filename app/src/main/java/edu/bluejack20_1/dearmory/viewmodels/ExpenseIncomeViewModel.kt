package edu.bluejack20_1.dearmory.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import edu.bluejack20_1.dearmory.repositories.ExpenseIncomeRepository

class ExpenseIncomeViewModel(private val repository: ExpenseIncomeRepository): ViewModel() {
    private lateinit var expenseIncomeModels: MutableLiveData<ArrayList<ExpenseIncome>>

    fun init(diaryId: String){
        expenseIncomeModels = repository.getExpenseIncomeModels(diaryId)
    }

    fun getExpenseIncomes(): MutableLiveData<ArrayList<ExpenseIncome>> {
        return expenseIncomeModels
    }

    fun createExpenseIncome(diaryId: String, expenseIncome: ExpenseIncome){
        return repository.createExpenseIncome(diaryId, expenseIncome)
    }

    fun updateExpenseIncome(diaryId: String, expenseIncome: ExpenseIncome){
        return repository.updateExpenseIncome(diaryId, expenseIncome)
    }

    fun deleteExpenseIncome(diaryId: String, expenseIncome: ExpenseIncome){
        return repository.deleteExpenseIncome(diaryId, expenseIncome)
    }
}