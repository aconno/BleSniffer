package com.aconno.blesniffer

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.aconno.blesniffer.device.notification.AlertNotificationReceiver
import com.aconno.blesniffer.device.notification.IntentProvider
import com.aconno.blesniffer.device.notification.NotificationFactory
import com.aconno.blesniffer.ui.ScanAnalyzerActivity

class IntentProviderImpl : IntentProvider {

    override fun getBleSnifferContentIntent(context: Context): PendingIntent {
        val contentIntent = Intent(context, ScanAnalyzerActivity::class.java)
        contentIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(
                context,
                0,
                contentIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun getAlertNotificationContentIntent(context: Context): PendingIntent {
        val contentIntent = Intent(context, ScanAnalyzerActivity::class.java)
        contentIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val requestCode = 0
        return PendingIntent.getActivity(
                context,
                requestCode,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun getAlertNotificationDeleteIntent(context: Context): PendingIntent {
        val outcome = Intent(context, AlertNotificationReceiver::class.java)

        outcome.action = AlertNotificationReceiver.DISMISS
        outcome.putExtra(
                NotificationFactory.ALERT_NOTIFICATION_NAME,
                NotificationFactory.ALERT_NOTIFICATION_ID
        )

        val requestCode = 0
        val flags = 0
        return PendingIntent.getBroadcast(context, requestCode, outcome, flags)
    }
}