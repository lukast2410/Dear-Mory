package edu.bluejack20_1.dearmory.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack20_1.dearmory.models.Image
import edu.bluejack20_1.dearmory.repositories.ImageRepository

@RequiresApi(Build.VERSION_CODES.N)
class ImageViewModel(private val repository: ImageRepository): ViewModel() {
    private lateinit var imageModels: MutableLiveData<ArrayList<Image>>

    fun init(diaryId: String){
        imageModels = repository.getImages(diaryId)
    }

    fun getImages(): MutableLiveData<ArrayList<Image>>{
        return imageModels
    }

    fun createImage(diaryId: String, image: Image){
        repository.createImage(diaryId, image)
    }

    fun deleteImage(diaryId: String, image: Image){
        repository.deleteImage(diaryId, image)
    }
}