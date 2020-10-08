package com.example.dearmory.models

import android.net.Uri

class User {
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var profilePicture: String
    private lateinit var id: String

    constructor(){

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

    fun getId(): String {
        return id
    }

    fun setId(newId: String) {
        this.id = newId
    }
}