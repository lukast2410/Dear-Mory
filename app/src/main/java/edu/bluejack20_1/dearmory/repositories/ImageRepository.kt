package edu.bluejack20_1.dearmory.repositories

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack20_1.dearmory.models.Image

class ImageRepository private constructor(){
    private val refsDB: DatabaseReference = FirebaseDatabase.getInstance().getReference(Image.IMAGE)
    private var imageModels: Map<String, Image> = HashMap()
    private var imageLiveData: MutableLiveData<Map<String, Image>> = MutableLiveData()

    companion object{
        var instance: ImageRepository? = null
        @JvmName("getInstance1")
        fun getInstance(): ImageRepository{
            if(instance == null)
                instance = ImageRepository()
            return instance as ImageRepository
        }
    }

    fun getImages(diaryId: String): MutableLiveData<Map<String, Image>>{
        loadImages(diaryId)

        imageLiveData.value = imageModels

        return imageLiveData
    }

    private fun loadImages(diaryId: String){

    }

    fun createImage(diaryId: String, image: Image){
        val key = refsDB.push().key as String
        image.setKey(key)
        refsDB.child(diaryId).child(key).setValue(image)
    }

    fun deleteImage(diaryId: String, image: Image){
        refsDB.child(diaryId).child(image.getKey()).removeValue()
    }
}