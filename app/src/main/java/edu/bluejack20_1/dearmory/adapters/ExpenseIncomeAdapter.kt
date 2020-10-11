package edu.bluejack20_1.dearmory.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import kotlinx.android.synthetic.main.item_expense_income.view.*

class ExpenseIncomeAdapter(): RecyclerView.Adapter<ExpenseIncomeAdapter.ViewHolder>() {
    private lateinit var expenseIncomeModels: ArrayList<ExpenseIncome>

    constructor(expenseIncomeModels: ArrayList<ExpenseIncome>) : this() {
        this.expenseIncomeModels = expenseIncomeModels
    }

    class ViewHolder : RecyclerView.ViewHolder {
        lateinit var notes: TextView

        constructor(itemView: View): super(itemView){
            notes = itemView.findViewById(R.id.tv_expense_income_notes_item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_expense_income, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setTag(expenseIncomeModels.get(position))
        holder.notes.setText(expenseIncomeModels.get(position).getNotes())
    }

    override fun getItemCount(): Int {
        return expenseIncomeModels.size
    }
}