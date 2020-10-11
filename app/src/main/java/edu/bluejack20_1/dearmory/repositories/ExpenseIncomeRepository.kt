package edu.bluejack20_1.dearmory.repositories

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.models.ExpenseIncome

class ExpenseIncomeRepository {
    private var expenseIncomeModels: ArrayList<ExpenseIncome> = ArrayList()
    private var expenseIncomeModel: MutableLiveData<ArrayList<ExpenseIncome>> = MutableLiveData()

    companion object{
        var instance: ExpenseIncomeRepository? = null
        @JvmName("getInstance1")
        fun getInstance(): ExpenseIncomeRepository{
            if(instance == null)
                instance = ExpenseIncomeRepository()
            return instance as ExpenseIncomeRepository
        }
    }

    fun getExpenseIncomeModels(diaryId: String): MutableLiveData<ArrayList<ExpenseIncome>>{
        if(expenseIncomeModels.size == 0)
            loadExpenseIncomeModels(diaryId)

        expenseIncomeModel.value = expenseIncomeModels

        return expenseIncomeModel
    }

    private fun loadExpenseIncomeModels(diaryId: String) {
        var refsDB: DatabaseReference = FirebaseDatabase.getInstance().getReference()
        var query: Query = refsDB.child("ExpenseIncome").child(diaryId)
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children){
                    expenseIncomeModels.add(ExpenseIncome()
                        .setId(data.child("id").getValue().toString())
                        .setNotes(data.child("notes").getValue().toString())
                        .setTime(data.child("time").getValue().toString())
                        .setAmount(data.child("amount").getValue().toString().toInt()))
                }
                expenseIncomeModel.postValue(expenseIncomeModels)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}