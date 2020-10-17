package edu.bluejack20_1.dearmory.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import edu.bluejack20_1.dearmory.repositories.DiaryRepository

class DiaryViewModel(private val repository: DiaryRepository): ViewModel() {
    private lateinit var diaryModels: MutableLiveData<ArrayList<Diary>>
    private lateinit var totalModels: MutableLiveData<ArrayList<HashMap<String, ExpenseIncome>>>

    fun init(userId: String){
        diaryModels = repository.getDiaries(userId)
        totalModels = repository.getTotals()
    }

    fun getDiaries(): MutableLiveData<ArrayList<Diary>>{
        return diaryModels
    }

    fun getTotals(): MutableLiveData<ArrayList<HashMap<String, ExpenseIncome>>>{
        return totalModels
    }

    fun getDiary(userId: String): MutableLiveData<Diary>{
        return repository.getDiary(userId)
    }

    fun saveDiary(userId: String, diary: Diary){
        repository.saveDiary(userId, diary)
    }
}