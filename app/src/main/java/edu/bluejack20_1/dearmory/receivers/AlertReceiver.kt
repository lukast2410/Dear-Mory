package edu.bluejack20_1.dearmory.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.Notification
import androidx.core.app.NotificationCompat
import edu.bluejack20_1.dearmory.helpers.NotificationHelper

class AlertReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val notificationHelper: NotificationHelper? = p0?.let { NotificationHelper(it) }
        val nb : NotificationCompat.Builder? = notificationHelper?.getChannelNotification("1", "asd")
        if (nb != null) {
            notificationHelper.getManager().notify(1, nb.build())
        }
    }

}