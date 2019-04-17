package com.aconno.blesniffer.work.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface WorkerCreator {
    fun create(appContext: Context, workerParameters: WorkerParameters): ListenableWorker
}