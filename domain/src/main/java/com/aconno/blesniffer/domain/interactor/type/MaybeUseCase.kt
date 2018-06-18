package com.aconno.blesniffer.domain.interactor.type

import io.reactivex.Maybe

interface MaybeUseCase<T> {

    fun execute(): Maybe<T>
}