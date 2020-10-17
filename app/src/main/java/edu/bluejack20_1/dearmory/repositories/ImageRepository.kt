package edu.bluejack20_1.dearmory.repositories

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import edu.bluejack20_1.dearmory.models.Image

@RequiresApi(Build.VERSION_CODES.N)
class ImageRepository private constructor() {
    private val refsDB: DatabaseReference = FirebaseDatabase.getInstance().getReference(Image.IMAGE)
    private var imageModels: ArrayList<Image> = ArrayList()
    private var imageLiveData: MutableLiveData<ArrayList<Image>> = MutableLiveData()

    companion object {
        var instance: ImageRepository? = null

        @JvmName("getInstance1")
        fun getInstance(): ImageRepository {
            if (instance == null)
                instance = ImageRepository()
            return instance as ImageRepository
        }
    }

    fun getImages(diaryId: String): MutableLiveData<ArrayList<Image>> {
        imageModels.clear()

        loadImages(diaryId)

        imageLiveData.value = imageModels

        return imageLiveData
    }

    private fun loadImages(diaryId: String) {
        var query: Query = refsDB.child(diaryId)
        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var key = snapshot.key.toString()
                imageModels.add(Image()
                    .setImageUrl(snapshot.child("imageUrl").value.toString())
                    .setKey(key))
                imageLiveData.postValue(imageModels)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {
                imageModels.removeIf { image ->
                    image.getKey() == snapshot.child("key").value.toString() &&
                            image.getImageUrl() == snapshot.child("imageUrl").value.toString()
                }
                imageLiveData.postValue(imageModels)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun createImage(diaryId: String, image: Image) {
        val key = refsDB.push().key as String
        image.setKey(key)
        refsDB.child(diaryId).child(key).setValue(image)
    }

    fun deleteImage(diaryId: String, image: Image) {
        refsDB.child(diaryId).child(image.getKey()).removeValue()
    }
}