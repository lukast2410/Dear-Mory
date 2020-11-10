package edu.bluejack20_1.dearmory.network

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessagingService

class GettingDeviceTokenService: FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        var deviceToken: String? = FirebaseInstanceId.getInstance().getToken()
        if (deviceToken != null) {
            Log.d("device token", deviceToken)
        }
    }
}