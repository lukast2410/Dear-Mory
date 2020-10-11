package edu.bluejack20_1.dearmory.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import edu.bluejack20_1.dearmory.R

class NotificationHelper(base: Context) : ContextWrapper(base) {

    private var channel1ID = "channel 1 ID"
    private var channel1Name = "channel 1 name"
    private var mManager: NotificationManager? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(){
        val channel = NotificationChannel(channel1ID, channel1Name, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lightColor = (R.color.colorAccent)
        channel.lockscreenVisibility = (Notification.VISIBILITY_PRIVATE)

        getManager().createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager{
        if(mManager == null){
            mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return mManager as NotificationManager
    }

    fun getChannelNotification(title: String, message: String): NotificationCompat.Builder{
        return NotificationCompat.Builder(applicationContext, channel1ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.journal)
    }
}