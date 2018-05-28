package com.aconno.acnsensa.domain.interactor.type

import io.reactivex.Single

interface SingleUseCaseWithTwoParameters<T, in P, in Q> {

    fun execute(parameter: P, parameter2: Q): Single<T>
}