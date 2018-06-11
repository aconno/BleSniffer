package com.aconno.acnsensa.dagger.scananalyzeractivity

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.device.permissons.PermissionActionFactory
import com.aconno.acnsensa.domain.deserializing.DeserializerRepository
import com.aconno.acnsensa.domain.interactor.deserializing.GetAllDeserializersUseCase
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.scanning.Bluetooth
import com.aconno.acnsensa.ui.ScanAnalyzerActivity
import com.aconno.acnsensa.viewmodel.BluetoothScanningViewModel
import com.aconno.acnsensa.viewmodel.BluetoothViewModel
import com.aconno.acnsensa.viewmodel.PermissionViewModel
import com.aconno.acnsensa.viewmodel.ScanResultViewModel
import com.aconno.acnsensa.viewmodel.factory.BluetoothScanningViewModelFactory
import com.aconno.acnsensa.viewmodel.factory.BluetoothViewModelFactory
import com.aconno.acnsensa.viewmodel.factory.ScanResultViewModelFactory
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
            acnSensaApplication: AcnSensaApplication
    ) = BluetoothScanningViewModelFactory(
            bluetooth,
            acnSensaApplication
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