package com.aconno.blesniffer.dagger.bluetoothscanning

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.aconno.blesniffer.*
import com.aconno.blesniffer.device.notification.IntentProvider
import com.aconno.blesniffer.device.notification.NotificationFactory
import dagger.Module
import dagger.Provides

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
            bleSnifferApplication: BleSnifferApplication,
            intentProvider: IntentProvider
    ): Notification {
        val notificationFactory = NotificationFactory()
        val title = bluetoothScanningService.getString(R.string.service_notification_title)
        val content = bluetoothScanningService.getString(R.string.service_notification_content)
        return notificationFactory.makeForegroundServiceNotification(
                bluetoothScanningService,
                intentProvider.getBleSnifferContentIntent(bleSnifferApplication),
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
    fun provideBluetoothScanningReceiverIntentFilter() = IntentFilter("com.aconno.blesniffer.STOP")

    @Provides
    @BluetoothScanningServiceScope
    fun provideIntentProvider(): IntentProvider {
        return IntentProviderImpl()
    }
}