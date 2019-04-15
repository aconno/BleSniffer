package com.aconno.blesniffer.work

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.aconno.blesniffer.domain.interactor.sync.SyncDeserializersUseCase
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class SyncDeserializersWorker(appContext: Context, workerParameters: WorkerParameters) :
    RxWorker(appContext, workerParameters) {
    @Inject
    lateinit var syncDeserializersUseCase: SyncDeserializersUseCase

    override fun createWork(): Single<Result> {
        return syncDeserializersUseCase.execute().toSingle {
            Result.success()
        }.onErrorReturn {
            Timber.e(it)
            Result.failure()
        }
    }
}