package com.aconno.acnsensa.dagger.bluetoothscanning

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.aconno.acnsensa.*
import com.aconno.acnsensa.device.notification.IntentProvider
import com.aconno.acnsensa.device.notification.NotificationFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author aconno
 */
@Module
class BluetoothScanningServiceModule(
        private val bluetoothScanningService: BluetoothScanningService
) {

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningService() = bluetoothScanningService

    @Provides
    @BluetoothScanningServiceScope
    fun provideNotification(
            acnSensaApplication: AcnSensaApplication,
            intentProvider: IntentProvider
    ): Notification {
        val notificationFactory = NotificationFactory()
        val title = bluetoothScanningService.getString(R.string.service_notification_title)
        val content = bluetoothScanningService.getString(R.string.service_notification_content)
        return notificationFactory.makeForegroundServiceNotification(
                bluetoothScanningService,
                intentProvider.getAcnSensaContentIntent(acnSensaApplication),
                title,
                content
        )
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningServiceReceiver(): BroadcastReceiver =
            BluetoothScanningServiceReceiver(bluetoothScanningService)

    @Provides
    @BluetoothScanningServiceScope
    fun provideBluetoothScanningReceiverIntentFilter() = IntentFilter("com.aconno.acnsensa.STOP")

    @Provides
    @BluetoothScanningServiceScope
    fun provideIntentProvider(): IntentProvider {
        return IntentProviderImpl()
    }
}