package com.aconno.blesniffer.dagger.work

import com.aconno.blesniffer.work.SyncDeserializersWorker
import com.aconno.blesniffer.work.factory.WorkerCreator
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(SyncDeserializersWorker::class)
    fun bindSyncDeserializersWorker(creator: SyncDeserializersWorker.Creator): WorkerCreator
}