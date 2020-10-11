package edu.bluejack20_1.dearmory.models

class ExpenseIncome {
    private lateinit var expenseIncomeId: String
    private lateinit var expenseIncomeNotes: String
    private lateinit var expenseIncomeAmount: Number
    private lateinit var expenseIncomeTime: String

    companion object{
        val GO_TO_EXPENSE_INCOME = "goToExpenseIncome"
        val ADD_EXPENSE_INCOME = "add expense income"
        val UPDATE_EXPENSE_INCOME = "update expense income"
        val EXPENSE_INCOME_ID = "expenseIncomeId"
    }

    fun setId(id: String): ExpenseIncome{
        expenseIncomeId = id
        return this
    }

    fun setNotes(notes: String): ExpenseIncome{
        expenseIncomeNotes = notes
        return this
    }

    fun setAmount(amount: Number): ExpenseIncome{
        expenseIncomeAmount = amount
        return this
    }

    fun setTime(time: String): ExpenseIncome{
        expenseIncomeTime = time
        return this
    }

    fun getId(): String{
        return expenseIncomeId
    }

    fun getNotes(): String{
        return expenseIncomeNotes
    }

    fun getAmount(): Number{
        return expenseIncomeAmount
    }

    fun getTime(): String{
        return expenseIncomeTime
    }
}