package edu.bluejack20_1.dearmory.models

import android.net.Uri

class User private constructor() {
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var profilePicture: String
    private var id: String? = null

    companion object{
        var instance: User? = null
        @JvmName("user")
        fun getInstance(): User{
            if(instance == null){
                instance = User()
            }
            return instance as User
        }
    }

    fun getName(): String {
        return name
    }

    fun setName(newName: String) {
        this.name = newName
    }

    fun getEmail(): String {
        return email
    }

    fun setEmail(newEmail: String) {
        this.email = newEmail
    }

    fun getProfilePicture(): String {
        return profilePicture
    }

    fun setProfilePicture(newProfileId: Uri?) {
        this.profilePicture = newProfileId.toString()
    }

    fun getId(): String? {
        return id
    }

    fun setId(newId: String) {
        this.id = newId
    }
}