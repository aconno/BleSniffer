package com.aconno.blesniffer.domain.interactor.bluetooth

import com.aconno.blesniffer.domain.interactor.type.MaybeUseCaseWithTwoParameters
import com.aconno.blesniffer.domain.model.ScanResult
import io.reactivex.Maybe

class FilterByMacAddressUseCase : MaybeUseCaseWithTwoParameters<ScanResult, ScanResult, String> {

    override fun execute(firstParameter: ScanResult, secondParameter: String): Maybe<ScanResult> {
        return if (firstParameter.device.macAddress == secondParameter) {
            Maybe.just(firstParameter)
        } else {
            Maybe.empty()
        }
    }
}