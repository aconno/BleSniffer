package com.aconno.blesniffer.dagger.editdeserializeractivity

import com.aconno.blesniffer.dagger.application.AppComponent
import com.aconno.blesniffer.ui.EditDeserializerActivity
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [EditDeserializerActivityModule::class])
@EditDeserializerActivityScope
interface EditDeserializerActivityComponent {
    fun inject(mainActivity: EditDeserializerActivity)
}