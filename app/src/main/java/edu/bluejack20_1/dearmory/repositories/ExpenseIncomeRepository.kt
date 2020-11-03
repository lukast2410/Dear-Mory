package edu.bluejack20_1.dearmory.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.models.ExpenseIncome

class ExpenseIncomeRepository private constructor() {
    private val refsDB: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private lateinit var expenseIncomeModels: ArrayList<ExpenseIncome>
    private var expenseIncomeLiveData: MutableLiveData<ArrayList<ExpenseIncome>> = MutableLiveData()

    companion object {
        var instance: ExpenseIncomeRepository? = null

        @JvmName("getInstance1")
        fun getInstance(): ExpenseIncomeRepository {
            if (instance == null)
                instance = ExpenseIncomeRepository()
            return instance as ExpenseIncomeRepository
        }
    }

    fun getExpenseIncomeModels(diaryId: String): MutableLiveData<ArrayList<ExpenseIncome>> {
        expenseIncomeModels = ArrayList()
        loadExpenseIncomeModels(diaryId)

        expenseIncomeLiveData.value = expenseIncomeModels

        return expenseIncomeLiveData
    }

    private fun loadExpenseIncomeModels(diaryId: String) {
        val query: Query =
            refsDB.child(ExpenseIncome.EXPENSE_INCOME).child(diaryId).orderByChild("time")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (expenseIncomeModels.size > 0)
                    expenseIncomeModels.clear()
                if (snapshot.exists()) {
                    for (data: DataSnapshot in snapshot.children) {
                        expenseIncomeModels.add(
                            ExpenseIncome()
                                .setId(data.child("id").value.toString())
                                .setNotes(data.child("notes").value.toString())
                                .setTime(data.child("time").value.toString())
                                .setAmount(data.child("amount").value.toString().toLong())
                        )
                    }
                }
                expenseIncomeLiveData.postValue(expenseIncomeModels)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun createExpenseIncome(diaryId: String, expenseIncome: ExpenseIncome) {
        val id = refsDB.child(diaryId).push().key.toString()
        expenseIncome.setId(id)
        refsDB.child(ExpenseIncome.EXPENSE_INCOME)
            .child(diaryId)
            .child(id)
            .setValue(expenseIncome)
    }

    fun updateExpenseIncome(diaryId: String, expenseIncome: ExpenseIncome) {
        refsDB.child(ExpenseIncome.EXPENSE_INCOME)
            .child(diaryId)
            .child(expenseIncome.getId())
            .setValue(expenseIncome)
    }

    fun deleteExpenseIncome(diaryId: String, expenseIncome: ExpenseIncome) {
        refsDB.child(ExpenseIncome.EXPENSE_INCOME)
            .child(diaryId)
            .child(expenseIncome.getId())
            .removeValue()
    }
}