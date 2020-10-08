package com.example.dearmory.activities

import android.app.Activity
import android.app.AlertDialog
import com.example.dearmory.R

class LoadingSignIn {

    private var activity: Activity
    private lateinit var alertDialog: AlertDialog

    constructor(myActivity: Activity){
        activity = myActivity
    }

    fun startLoading(){
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.activity_log_in, null))
        builder.setCancelable(false)

        alertDialog = builder.create()
        alertDialog.show()
    }

    fun dismissDialog(){
        alertDialog.dismiss()
    }
}