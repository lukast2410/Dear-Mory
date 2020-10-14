package edu.bluejack20_1.dearmory.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack20_1.dearmory.models.Image
import edu.bluejack20_1.dearmory.repositories.ImageRepository

class ImageViewModel(private val repository: ImageRepository): ViewModel() {
    private lateinit var imageModels: MutableLiveData<Map<String, Image>>

    fun init(diaryId: String){
        imageModels = repository.getImages(diaryId)
    }

    fun createImage(diaryId: String, image: Image){
        repository.createImage(diaryId, image)
    }

    fun deleteImage(diaryId: String, image: Image){
        repository.deleteImage(diaryId, image)
    }
}