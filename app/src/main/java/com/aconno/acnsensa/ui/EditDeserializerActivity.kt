package com.aconno.acnsensa.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.adapter.DeserializerEditorAdapter
import com.aconno.acnsensa.dagger.editdeserializeractivity.DaggerEditDeserializerActivityComponent
import com.aconno.acnsensa.dagger.editdeserializeractivity.EditDeserializerActivityComponent
import com.aconno.acnsensa.dagger.editdeserializeractivity.EditDeserializerActivityModule
import com.aconno.acnsensa.domain.ValueConverter
import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.deserializing.GeneralDeserializer
import com.aconno.acnsensa.domain.interactor.deserializing.AddDeserializerUseCase
import com.aconno.acnsensa.domain.interactor.deserializing.GetDeserializerByIdUseCase
import com.aconno.acnsensa.domain.interactor.deserializing.UpdateDeserializerUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_deserializer.*
import timber.log.Timber
import javax.inject.Inject

class EditDeserializerActivity : AppCompatActivity() {

    private var snackbar: Snackbar? = null

    @Inject
    lateinit var addDeserializersUseCase: AddDeserializerUseCase
    @Inject
    lateinit var getDeserializerByIdUseCase: GetDeserializerByIdUseCase
    @Inject
    lateinit var updateDeserializerUseCase: UpdateDeserializerUseCase

    lateinit var deserializer: Deserializer

    val editDeserializerActivityComponent: EditDeserializerActivityComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        DaggerEditDeserializerActivityComponent.builder()
                .appComponent(acnSensaApplication?.appComponent)
                .editDeserializerActivityModule(EditDeserializerActivityModule(this))
                .build()
    }

    private var existing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_deserializer)

        editDeserializerActivityComponent.inject(this)

        custom_toolbar.title = getString(R.string.app_name)
        setSupportActionBar(custom_toolbar)

        deserializer_list.layoutManager = LinearLayoutManager(this)
        if (intent.extras != null) {
            val filterContent: String = intent.extras.getString("filter", "")
            val type: String = intent.extras.getString("type", "")
            getDeserializerByIdUseCase.execute(filterContent, type)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        deserializer = it
                        deserializer_list.adapter = DeserializerEditorAdapter(deserializer, this)
                        existing = true
                    }, {
                        deserializer = GeneralDeserializer("", Deserializer.Type.MAC, mutableListOf())
                        deserializer_list.adapter = DeserializerEditorAdapter(deserializer, this)
                    })
        } else {
            deserializer = GeneralDeserializer("", Deserializer.Type.MAC, mutableListOf())
            deserializer_list.adapter = DeserializerEditorAdapter(deserializer, this)
        }

        add_value_deserializer_button.setOnClickListener {
            deserializer.valueDeserializers.add(
                    Triple("", Pair(0, 0), ValueConverter.BOOLEAN)
            )
            deserializer_list.adapter.notifyDataSetChanged()
        }

        save.setOnClickListener {
            if(existing) {
                updateDeserializerUseCase.execute(GeneralDeserializer(
                        filter = filter.editText?.text?.toString() ?: "Empty",
                        filterType = deserializer.filterType,
                        valueDeserializers = deserializer.valueDeserializers
                )).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            finish()
                        }, {
                            Timber.e(it)
                        })
            }else {
                addDeserializersUseCase.execute(GeneralDeserializer(
                        filter = filter.editText?.text?.toString() ?: "Empty",
                        filterType = deserializer.filterType,
                        valueDeserializers = deserializer.valueDeserializers
                )).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            finish()
                        }, {
                            Timber.e(it)
                        })
            }
        }
    }
}