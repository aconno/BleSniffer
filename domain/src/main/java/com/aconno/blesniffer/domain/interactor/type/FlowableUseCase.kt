package com.aconno.blesniffer.domain.interactor.type

import io.reactivex.Flowable

interface FlowableUseCase<T> {

    fun execute(): Flowable<T>
}