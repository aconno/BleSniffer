package com.aconno.blesniffer.domain.interactor.type

import io.reactivex.Completable

interface CompletableUseCase {

    fun execute(): Completable
}