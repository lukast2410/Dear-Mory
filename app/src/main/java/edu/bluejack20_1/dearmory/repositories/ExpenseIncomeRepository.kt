package edu.bluejack20_1.dearmory.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.models.ExpenseIncome

class ExpenseIncomeRepository private constructor(){
    private var expenseIncomeModels: ArrayList<ExpenseIncome> = ArrayList()
    private var expenseIncomeLiveData: MutableLiveData<ArrayList<ExpenseIncome>> = MutableLiveData()

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

        expenseIncomeLiveData.value = expenseIncomeModels

        return expenseIncomeLiveData
    }

    private fun loadExpenseIncomeModels(diaryId: String) {
        val refsDB: DatabaseReference = FirebaseDatabase.getInstance().getReference()
        val query: Query = refsDB.child("ExpenseIncome").child(diaryId)
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(expenseIncomeModels.size > 0)
                    expenseIncomeModels.removeAll(expenseIncomeModels)
                for (data: DataSnapshot in snapshot.children){
                    expenseIncomeModels.add(ExpenseIncome()
                        .setId(data.child("id").getValue().toString())
                        .setNotes(data.child("notes").getValue().toString())
                        .setTime(data.child("time").getValue().toString())
                        .setAmount(data.child("amount").getValue().toString().toLong()))
                }
                expenseIncomeLiveData.postValue(expenseIncomeModels)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}