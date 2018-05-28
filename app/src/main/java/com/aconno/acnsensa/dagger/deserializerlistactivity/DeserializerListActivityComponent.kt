package com.aconno.acnsensa.dagger.deserializerlistactivity

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.DeserializerListActivity
import com.aconno.acnsensa.ui.EditDeserializerActivity
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [DeserializerListActivityModule::class])
@DeserializerListActivityScope
interface DeserializerListActivityComponent {
    fun inject(deserializerActivity: DeserializerListActivity)
}