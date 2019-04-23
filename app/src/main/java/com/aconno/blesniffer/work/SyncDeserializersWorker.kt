package com.aconno.blesniffer.work

import android.content.Context
import androidx.work.*
import com.aconno.blesniffer.BuildConfig
import com.aconno.blesniffer.domain.interactor.sync.SyncDeserializersUseCase
import com.aconno.blesniffer.work.factory.WorkerCreator
import io.reactivex.Single
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

class SyncDeserializersWorker(
    appContext: Context,
    workerParameters: WorkerParameters,
    private val syncDeserializersUseCase: SyncDeserializersUseCase
) :
    RxWorker(appContext, workerParameters) {

    override fun createWork(): Single<Result> {
        return syncDeserializersUseCase.execute().toSingle {
            Result.success()
        }.onErrorReturn {
            Timber.e(it)
            Result.failure()
        }
    }

    class Creator @Inject constructor(
        private val syncDeserializersUseCaseProvider: Provider<SyncDeserializersUseCase>
    ) : WorkerCreator {
        override fun create(
            appContext: Context,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return SyncDeserializersWorker(
                appContext,
                workerParameters,
                syncDeserializersUseCaseProvider.get()
            )
        }
    }

    companion object {

        private val REPEAT_INTERVAL = if (BuildConfig.DEBUG) 30L else 2L
        private val REPEAT_INTERVAL_TIME_UNIT =
            if (BuildConfig.DEBUG) TimeUnit.SECONDS else TimeUnit.HOURS
        private const val NAME = "SyncDeserializersWorker"
        lateinit var WORKER_ID: UUID

        fun createAndEnqueue() {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<SyncDeserializersWorker>(
                REPEAT_INTERVAL, REPEAT_INTERVAL_TIME_UNIT
            ).setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WORKER_ID = workRequest.id

            WorkManager.getInstance()
                .enqueueUniquePeriodicWork(NAME, ExistingPeriodicWorkPolicy.REPLACE, workRequest)
        }

    }
}