package edu.bluejack20_1.dearmory.receivers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.activities.LogInActivity
import edu.bluejack20_1.dearmory.notifications.NotificationChannelApp

class AlertReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, p1: Intent?) {

        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val repeatingIntent: Intent = Intent(context, LogInActivity::class.java)
        repeatingIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 100, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context)
            .setContentIntent(pendingIntent)
            .setSmallIcon(android.R.drawable.arrow_up_float)
            .setContentTitle("Title here")
            .setContentText("Text here")
            .setAutoCancel(true)
        Log.d("asd", "10")
        notificationManager.notify(100, builder.build())
        Log.d("asd", "11")

        val label = p1?.getSerializableExtra("label").toString()

        var notification : Notification
        if(p1?.getSerializableExtra("vibrate").toString() == "on"){
            notification = context.let {
                NotificationCompat.Builder(it, NotificationChannelApp.channel_1_id)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.journal)
                    .setContentTitle(label)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                    .setLights(Color.BLUE, 3000, 3000)
                    .build()
            }!!
        }else{
            notification = context.let {
                NotificationCompat.Builder(it, NotificationChannelApp.channel_1_id)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.journal)
                    .setContentTitle(label)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setLights(Color.BLUE, 3000, 3000)
                    .build()
            }!!
        }

        notificationManager.notify(1, notification)

//        val notificationHelper: NotificationHelper? = context?.let { NotificationHelper(it) }
//        val nb : NotificationCompat.Builder? = notificationHelper?.getChannelNotification("1", "asd")
//        if (nb != null) {
//            notificationHelper.getManager().notify(1, nb.build())
//        }
    }

}