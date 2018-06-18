package com.aconno.blesniffer.dagger.scananalyzeractivity

import android.arch.lifecycle.ViewModelProviders
import com.aconno.blesniffer.BleSnifferApplication
import com.aconno.blesniffer.BluetoothStateReceiver
import com.aconno.blesniffer.device.permissons.PermissionActionFactory
import com.aconno.blesniffer.domain.deserializing.DeserializerRepository
import com.aconno.blesniffer.domain.interactor.deserializing.GetAllDeserializersUseCase
import com.aconno.blesniffer.domain.model.ScanResult
import com.aconno.blesniffer.domain.scanning.Bluetooth
import com.aconno.blesniffer.ui.ScanAnalyzerActivity
import com.aconno.blesniffer.viewmodel.BluetoothScanningViewModel
import com.aconno.blesniffer.viewmodel.BluetoothViewModel
import com.aconno.blesniffer.viewmodel.PermissionViewModel
import com.aconno.blesniffer.viewmodel.ScanResultViewModel
import com.aconno.blesniffer.viewmodel.factory.BluetoothScanningViewModelFactory
import com.aconno.blesniffer.viewmodel.factory.BluetoothViewModelFactory
import com.aconno.blesniffer.viewmodel.factory.ScanResultViewModelFactory
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable

/**
 * @author aconno
 */
@Module
class ScanAnalyzerActivityModule(private val scanAnalyzerActivity: ScanAnalyzerActivity) {

    @Provides
    @ScanAnalyzerActivityScope
    fun provideBeaconListViewModel(scanResultViewModelFactory: ScanResultViewModelFactory): ScanResultViewModel =
            ViewModelProviders.of(scanAnalyzerActivity, scanResultViewModelFactory)
                    .get(ScanResultViewModel::class.java)

    @Provides
    @ScanAnalyzerActivityScope
    fun provideBeaconListViewModelFactory(
            data: Flowable<ScanResult>
    ) = ScanResultViewModelFactory(data)

    @Provides
    @ScanAnalyzerActivityScope
    fun provideBluetoothScanningViewModel(
            bluetoothScanningViewModelFactory: BluetoothScanningViewModelFactory
    ) = ViewModelProviders.of(scanAnalyzerActivity, bluetoothScanningViewModelFactory)
            .get(BluetoothScanningViewModel::class.java)

    @Provides
    @ScanAnalyzerActivityScope
    fun provideBluetoothScanningViewModelFactory(
            bluetooth: Bluetooth,
            bleSnifferApplication: BleSnifferApplication
    ) = BluetoothScanningViewModelFactory(
            bluetooth,
            bleSnifferApplication
    )

    @Provides
    @ScanAnalyzerActivityScope
    fun provideScanAnalyzerActivity() = scanAnalyzerActivity

    @Provides
    @ScanAnalyzerActivityScope
    fun providePermissionsViewModel(): PermissionViewModel {
        val permissionAction = PermissionActionFactory.getPermissionAction(scanAnalyzerActivity)
        return PermissionViewModel(permissionAction, scanAnalyzerActivity)
    }

    @Provides
    @ScanAnalyzerActivityScope
    fun provideBluetoothViewModelFactory(
            bluetooth: Bluetooth,
            bluetoothStateReceiver: BluetoothStateReceiver
    ) =
            BluetoothViewModelFactory(bluetooth, bluetoothStateReceiver, scanAnalyzerActivity.application)

    @Provides
    @ScanAnalyzerActivityScope
    fun provideBluetoothViewModel(bluetoothViewModelFactory: BluetoothViewModelFactory) =
            ViewModelProviders.of(
                    scanAnalyzerActivity,
                    bluetoothViewModelFactory
            ).get(BluetoothViewModel::class.java)

    @Provides
    @ScanAnalyzerActivityScope
    fun provideGetAllDeserializersUseCase(deserializerRepository: DeserializerRepository): GetAllDeserializersUseCase {
        return GetAllDeserializersUseCase(deserializerRepository)
    }
}