package com.aconno.acnsensa.dagger.editdeserializeractivity

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.EditDeserializerActivity
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [EditDeserializerActivityModule::class])
@EditDeserializerActivityScope
interface EditDeserializerActivityComponent {
    fun inject(mainActivity: EditDeserializerActivity)
}