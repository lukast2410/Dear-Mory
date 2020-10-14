package edu.bluejack20_1.dearmory.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.bluejack20_1.dearmory.repositories.ImageRepository
import edu.bluejack20_1.dearmory.viewmodels.ImageViewModel
import java.lang.IllegalArgumentException

class ImageViewModelFactory(private val repository: ImageRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ImageRepository::class.java))
            return ImageViewModel(repository) as T
        throw IllegalArgumentException("Unknown View Model Class")
    }
}