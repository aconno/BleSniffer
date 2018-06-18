package com.aconno.blesniffer.dagger.deserializerlistactivity

import com.aconno.blesniffer.dagger.application.AppComponent
import com.aconno.blesniffer.ui.DeserializerListActivity
import com.aconno.blesniffer.ui.EditDeserializerActivity
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [DeserializerListActivityModule::class])
@DeserializerListActivityScope
interface DeserializerListActivityComponent {
    fun inject(deserializerActivity: DeserializerListActivity)
}