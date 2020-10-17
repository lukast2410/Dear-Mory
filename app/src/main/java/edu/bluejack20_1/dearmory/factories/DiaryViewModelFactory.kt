package edu.bluejack20_1.dearmory.factories

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.bluejack20_1.dearmory.repositories.DiaryRepository
import edu.bluejack20_1.dearmory.viewmodels.DiaryViewModel
import java.lang.IllegalArgumentException

class DiaryViewModelFactory(private val repository: DiaryRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DiaryViewModel::class.java))
            return DiaryViewModel(repository) as T
        throw IllegalArgumentException("Unknown View Model")
    }
}