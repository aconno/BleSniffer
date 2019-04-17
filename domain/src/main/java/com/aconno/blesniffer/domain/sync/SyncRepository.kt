package com.aconno.blesniffer.domain.sync

import io.reactivex.Completable

interface SyncRepository {

    fun syncDeserializers(): Completable
}