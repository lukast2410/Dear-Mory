package edu.bluejack20_1.dearmory.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import edu.bluejack20_1.dearmory.repositories.ExpenseIncomeRepository

class ExpenseIncomeViewModel: ViewModel() {
    private var expenseIncomeModels: MutableLiveData<ArrayList<ExpenseIncome>>? = null

    fun init(diaryId: String){
        if(expenseIncomeModels == null){
            expenseIncomeModels = ExpenseIncomeRepository.getInstance().getExpenseIncomeModels(diaryId)
        }
    }

    fun getExpenseIncomes(): MutableLiveData<ArrayList<ExpenseIncome>> {
        return expenseIncomeModels as MutableLiveData<ArrayList<ExpenseIncome>>
    }
}