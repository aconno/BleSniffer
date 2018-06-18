package com.aconno.acnsensa.ui

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.adapter.DeserializedFieldsAdapter
import com.aconno.acnsensa.adapter.DeserializerEditorAdapter
import com.aconno.acnsensa.adapter.inversedCopyOfRangeInclusive
import com.aconno.acnsensa.adapter.toHex
import com.aconno.acnsensa.dagger.editdeserializeractivity.DaggerEditDeserializerActivityComponent
import com.aconno.acnsensa.dagger.editdeserializeractivity.EditDeserializerActivityComponent
import com.aconno.acnsensa.dagger.editdeserializeractivity.EditDeserializerActivityModule
import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.deserializing.GeneralDeserializer
import com.aconno.acnsensa.domain.deserializing.GeneralFieldDeserializer
import com.aconno.acnsensa.domain.interactor.deserializing.AddDeserializerUseCase
import com.aconno.acnsensa.domain.interactor.deserializing.GetDeserializerByIdUseCase
import com.aconno.acnsensa.domain.interactor.deserializing.UpdateDeserializerUseCase
import com.google.common.io.BaseEncoding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_deserializer.*
import kotlinx.android.synthetic.main.popup_field_list_preview.view.*
import timber.log.Timber
import javax.inject.Inject


const val RESULT_UPDATED: Int = 0x10
const val RESULT_ADDED: Int = 0x11

class EditDeserializerActivity : AppCompatActivity() {

    @Inject
    lateinit var addDeserializersUseCase: AddDeserializerUseCase
    @Inject
    lateinit var getDeserializerByIdUseCase: GetDeserializerByIdUseCase
    @Inject
    lateinit var updateDeserializerUseCase: UpdateDeserializerUseCase

    var deserializer: Deserializer = GeneralDeserializer()
        set(value) {
            field = value.apply {
                deserializer_list.adapter = DeserializerEditorAdapter(this, this@EditDeserializerActivity).apply {
                    ItemTouchHelper(createItemTouchHelper()).attachToRecyclerView(deserializer_list)
                }
                deserializer_filter_type.setSelection(Deserializer.Type.values().indexOf(
                        Deserializer.Type.valueOf(this.filterType.name)
                ))
                deserializer_filter.editText?.setText(this.filter)
                deserializer_name.editText?.setText(this.name)
                deserializer_sample_data.editText?.setText(if (this.sampleData.isNotEmpty()) this.sampleData.toHex() else "")
            }
        }

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

        custom_toolbar.title = getString(R.string.scanner_app_name)
        setSupportActionBar(custom_toolbar)

        deserializer_list.layoutManager = LinearLayoutManager(this)
        if (intent.extras != null) {
            val filterContent: String = intent.extras.getString("filter", "")
            val type: String = intent.extras.getString("type", "")
            val sampleData = intent.extras.getByteArray("sampleData") ?: byteArrayOf()
            getDeserializerByIdUseCase.execute(filterContent, type)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                deserializer = it
                                existing = true
                            },
                            {
                                deserializer = GeneralDeserializer(
                                        filter = filterContent,
                                        sampleData = sampleData
                                )
                            }
                    )
        } else {
            deserializer = GeneralDeserializer()
        }

        deserializer_filter_type.adapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                Deserializer.Type.values().map { it.name }
        )

        deserializer_filter_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                deserializer_filter_type.setSelection(0)
                deserializer.filterType = Deserializer.Type.values()[0]
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                deserializer.filterType = Deserializer.Type.values()[position]
            }
        }

        add_value_deserializer_button.setOnClickListener {
            deserializer.fieldDeserializers.add(
                    GeneralFieldDeserializer()
            )
            deserializer_list.adapter.notifyDataSetChanged()
        }

        save.setOnClickListener {
            if (existing) {
                updateDeserializerUseCase.execute(updateDeserializerFromInputData()).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    setResult(RESULT_UPDATED)
                                    finish()
                                },
                                {
                                    Timber.e(it)
                                }
                        )
            } else {
                addDeserializersUseCase.execute(updateDeserializerFromInputData()).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    setResult(RESULT_ADDED)
                                    finish()
                                },
                                {
                                    Timber.e(it)
                                }
                        )
            }
        }

        preview.setOnClickListener {
            val rawData = getSampleDataBytes()
            updateDeserializerFromInputData().fieldDeserializers.map { d ->
                val start = d.startIndexInclusive
                val end = d.endIndexExclusive
                val size = rawData.size
                Triple(
                        d.name,
                        if (start > size || end > size) "Bad Indexes"
                        else try {
                            d.type.converter.deserialize(
                                    if (start <= end) rawData.copyOfRange(start, end + 1)
                                    else rawData.inversedCopyOfRangeInclusive(start, end)
                            ).toString()
                        } catch (e: IllegalArgumentException) {
                            "Invalid Byte Data"
                        },
                        d.color
                )
            }.let {

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                val view = layoutInflater.inflate(R.layout.popup_field_list_preview, null)

                val deserializedFieldsAdapter = DeserializedFieldsAdapter()
                view.deserialized_field_list_preview.adapter = deserializedFieldsAdapter
                view.deserialized_field_list_preview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                deserializedFieldsAdapter.setFields(it)
                AlertDialog.Builder(this)
                        .setView(view)
                        .setOnDismissListener {
                            deserializedFieldsAdapter.setFields(mutableListOf())
                            it.dismiss()
                        }
                        .create().also { dialog ->
                            view.setOnTouchListener { v, event ->
                                dialog.dismiss()
                                true
                            }
                        }
                        .show()
            }
        }

        cancel.setOnClickListener {
            finish()
        }
    }

    private fun updateDeserializerFromInputData(): Deserializer {
        return deserializer.apply {
            name = deserializer_name.editText?.text?.toString() ?: name ?: "Unnamed"
            filter = deserializer_filter.editText?.text?.toString() ?: filter ?: ""
            filterType = filterType
            sampleData = getSampleDataBytes()
        }
    }

    private fun getSampleDataBytes(): ByteArray {
        return BaseEncoding.base16().decode(deserializer_sample_data?.editText?.text?.toString()?.replace("0x", "")?.replace(" ", "")
                ?: "")
    }
}