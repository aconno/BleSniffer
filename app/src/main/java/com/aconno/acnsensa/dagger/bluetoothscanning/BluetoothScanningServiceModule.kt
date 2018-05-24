package com.aconno.acnsensa.dagger.bluetoothscanning

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothScanningService
import com.aconno.acnsensa.BluetoothScanningServiceReceiver
import com.aconno.acnsensa.R
import com.aconno.acnsensa.data.mqtt.GoogleCloudPublisher
import com.aconno.acnsensa.device.notification.IntentProvider
import com.aconno.acnsensa.device.notification.NotificationFactory
import com.aconno.acnsensa.device.storage.FileStorageImpl
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import com.aconno.acnsensa.domain.ifttt.NotificationDisplay
import com.aconno.acnsensa.domain.ifttt.TextToSpeechPlayer
import com.aconno.acnsensa.domain.ifttt.outcome.*
import com.aconno.acnsensa.domain.interactor.LogReadingUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.GetAllEnabledGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.InputToOutcomesUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.ReadingToInputUseCase
import com.aconno.acnsensa.domain.interactor.mqtt.CloseConnectionUseCase
import com.aconno.acnsensa.domain.interactor.mqtt.PublishReadingsUseCase
import com.aconno.acnsensa.domain.interactor.repository.RecordSensorValuesUseCase
import com.aconno.acnsensa.domain.interactor.repository.SensorValuesToReadingsUseCase
import com.aconno.acnsensa.domain.repository.InMemoryRepository
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
    fun provideRecordSensorValuesUseCase(
        inMemoryRepository: InMemoryRepository
    ): RecordSensorValuesUseCase {
        return RecordSensorValuesUseCase(inMemoryRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideSensorValuesToReadingsUseCase(): SensorValuesToReadingsUseCase {
        return SensorValuesToReadingsUseCase()

    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideLogReadingsUseCase(): LogReadingUseCase {
        return LogReadingUseCase(FileStorageImpl(bluetoothScanningService))
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideReadingToInputUseCase(): ReadingToInputUseCase {
        return ReadingToInputUseCase()
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideHandleInputUseCase(actionsRepository: ActionsRepository): InputToOutcomesUseCase {
        return InputToOutcomesUseCase(actionsRepository)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideRunOutcomeUseCase(
        notificationDisplay: NotificationDisplay,
        smsSender: SmsSender,
        textToSpeechPlayer: TextToSpeechPlayer,
        vibrator: Vibrator
    ): RunOutcomeUseCase {
        val notificationOutcomeExecutor = NotificationOutcomeExecutor(notificationDisplay)
        val smsOutcomeExecutor = SmsOutcomeExecutor(smsSender)
        val textToSpeechOutcomeExecutor = TextToSpeechOutcomeExecutor(textToSpeechPlayer)
        val vibrationOutcomeExecutor = VibrationOutcomeExecutor(vibrator)
        val outcomeExecutorSelector = OutcomeExecutorSelector(
            notificationOutcomeExecutor,
            smsOutcomeExecutor,
            textToSpeechOutcomeExecutor,
            vibrationOutcomeExecutor
        )

        return RunOutcomeUseCase(outcomeExecutorSelector)
    }

    @Provides
    @BluetoothScanningServiceScope
    fun provideGetAllEnabledGooglePublishUseCase(googlePublishRepository: GooglePublishRepository): GetAllEnabledGooglePublishUseCase {
        return GetAllEnabledGooglePublishUseCase(googlePublishRepository)
    }
}