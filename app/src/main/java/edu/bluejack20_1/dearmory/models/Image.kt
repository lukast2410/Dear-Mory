package edu.bluejack20_1.dearmory.models

class Image() {
    private lateinit var key: String
    private lateinit var imageUrl: String

    init {
        key = ""
        imageUrl = ""
    }

    companion object{
        const val IMAGE = "Image"
    }

    fun getKey(): String{
        return key
    }

    fun getImageUrl(): String{
        return imageUrl
    }

    fun setKey(key: String): Image{
        this.key = key
        return this
    }

    fun setImageUrl(imageUrl: String): Image{
        this.imageUrl = imageUrl
        return this
    }
}