package com.aconno.acnsensa.dagger.mainactivity

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.BeaconListFragment
import com.aconno.acnsensa.ui.MainActivity
import com.aconno.acnsensa.ui.devices.SavedDevicesFragment
import com.aconno.acnsensa.ui.sensors.SensorListFragment
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [MainActivityModule::class])
@MainActivityScope
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(sensorListFragment: SensorListFragment)

    fun inject(savedDevicesFragment: SavedDevicesFragment)
}