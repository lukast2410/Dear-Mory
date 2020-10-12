package edu.bluejack20_1.dearmory.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.bluejack20_1.dearmory.repositories.ExpenseIncomeRepository
import edu.bluejack20_1.dearmory.viewmodels.ExpenseIncomeViewModel
import java.lang.IllegalArgumentException

class ExpenseIncomeViewModelFactory(private val repository: ExpenseIncomeRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ExpenseIncomeViewModel::class.java))
            return ExpenseIncomeViewModel(repository) as T
        throw IllegalArgumentException("Unknown View Model Class")
    }
}