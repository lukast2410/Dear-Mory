package edu.bluejack20_1.dearmory.notifications

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class NotificationChannelApp : Application() {

    companion object{
        public final var channel_1_id : String = "channel1"
        public final var channel_2_id : String = "channel2"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var channel1 : NotificationChannel = NotificationChannel(
                channel_1_id, "channel_1", NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "This is channel 1"

            var channel2 : NotificationChannel = NotificationChannel(
                channel_2_id, "channel_2", NotificationManager.IMPORTANCE_LOW
            )
            channel2.description = "This is channel 2"

            var notifManager : NotificationManager = getSystemService(NotificationManager::class.java)
            notifManager.createNotificationChannel(channel1)
//            notifManager.createNotificationChannel(channel2)
        }
    }
}