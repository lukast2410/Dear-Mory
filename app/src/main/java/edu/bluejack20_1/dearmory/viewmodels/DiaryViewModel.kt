package edu.bluejack20_1.dearmory.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.repositories.DiaryRepository

class DiaryViewModel(private val repository: DiaryRepository): ViewModel() {

    fun getDiary(userId: String): MutableLiveData<Diary>{
        return repository.getDiary(userId)
    }

    fun saveDiary(userId: String, diary: Diary){
        repository.saveDiary(userId, diary)
    }
}