package com.aconno.blesniffer.domain.interactor.type

import io.reactivex.Flowable

/**
 * @author aconno
 */
interface FlowableUseCaseWithParameter<T, in P> {
    fun execute(parameter: P): Flowable<T>
}
