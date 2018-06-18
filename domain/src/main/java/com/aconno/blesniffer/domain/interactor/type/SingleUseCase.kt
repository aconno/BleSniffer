package com.aconno.blesniffer.domain.interactor.type

import io.reactivex.Single

interface SingleUseCase<T> {

    fun execute(): Single<T>
}