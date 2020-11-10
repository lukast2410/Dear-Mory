package edu.bluejack20_1.dearmory.network

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.activities.LogInActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    var NOTIFICATION_ID = 1

    override fun onNewToken(token: String) {
        Log.d("token", "$token")
        Log.d("fireNotif", "1")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        generateNotification(remoteMessage.notification?.body, remoteMessage.notification?.title)
        Log.d("fireNotif", "2")
    }

    private fun generateNotification(body: String?, title: String?) {
        Log.d("fireNotif", "3")
        val intent: Intent = Intent(this, LogInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        Log.d("fireNotif", "4")
        val soundUri : Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.journal)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setLights(Color.BLUE, 3000, 3000)
            .setAutoCancel(true)
            .setSound(soundUri)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(NOTIFICATION_ID > 1073741824){
            NOTIFICATION_ID = 0
            Log.d("fireNotif", "5")
        }

        Log.d("fireNotif", "10")
        notificationManager.notify(NOTIFICATION_ID++, notificationBuilder.build())
    }

}