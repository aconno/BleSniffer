package com.aconno.blesniffer.domain.interactor.sync

import com.aconno.blesniffer.domain.interactor.type.CompletableUseCase
import com.aconno.blesniffer.domain.sync.SyncRepository
import io.reactivex.Completable

class SyncDeserializersUseCase(private val syncRepository: SyncRepository): CompletableUseCase {

    override fun execute(): Completable {
        return syncRepository.syncDeserializers()
    }
}