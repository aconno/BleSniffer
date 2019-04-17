package com.aconno.blesniffer.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.aconno.blesniffer.BleSnifferApplication
import com.aconno.blesniffer.R
import com.aconno.blesniffer.adapter.DeserializedFieldsAdapter
import com.aconno.blesniffer.adapter.DeserializerEditorAdapter
import com.aconno.blesniffer.adapter.inversedCopyOfRangeInclusive
import com.aconno.blesniffer.adapter.toHex
import com.aconno.blesniffer.dagger.editdeserializeractivity.DaggerEditDeserializerActivityComponent
import com.aconno.blesniffer.dagger.editdeserializeractivity.EditDeserializerActivityComponent
import com.aconno.blesniffer.dagger.editdeserializeractivity.EditDeserializerActivityModule
import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.GeneralDeserializer
import com.aconno.blesniffer.domain.deserializing.GeneralFieldDeserializer
import com.aconno.blesniffer.domain.interactor.deserializing.AddDeserializerUseCase
import com.aconno.blesniffer.domain.interactor.deserializing.GetDeserializerByFilterUseCase
import com.aconno.blesniffer.domain.interactor.deserializing.GetDeserializerByIdUseCase
import com.aconno.blesniffer.domain.interactor.deserializing.UpdateDeserializerUseCase
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
    lateinit var getDeserializerByFilterUseCase: GetDeserializerByFilterUseCase
    @Inject
    lateinit var getDeserializerByIdUseCase: GetDeserializerByIdUseCase
    @Inject
    lateinit var updateDeserializerUseCase: UpdateDeserializerUseCase

    var deserializer: Deserializer = GeneralDeserializer()
        set(value) {
            field = value.apply {
                deserializerEditorAdapter.deserializer = this
                deserializer_filter_type.setSelection(Deserializer.Type.values().indexOf(
                        Deserializer.Type.valueOf(this.filterType.name)
                ))
                deserializer_filter.editText?.setText(this.filter)
                deserializer_name.editText?.setText(this.name)
                deserializer_sample_data.editText?.setText(if (this.sampleData.isNotEmpty()) this.sampleData.toHex() else "")
            }
        }

    val editDeserializerActivityComponent: EditDeserializerActivityComponent by lazy {
        val bleSnifferApplication: BleSnifferApplication? = application as? BleSnifferApplication
        DaggerEditDeserializerActivityComponent.builder()
                .appComponent(bleSnifferApplication?.appComponent)
                .editDeserializerActivityModule(EditDeserializerActivityModule(this))
                .build()
    }

    val deserializerEditorAdapter: DeserializerEditorAdapter by lazy {
        DeserializerEditorAdapter(this@EditDeserializerActivity).apply {
            ItemTouchHelper(createItemTouchHelper()).attachToRecyclerView(deserializer_list)
        }
    }

    private var existing: Boolean = false

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_deserializer)

        editDeserializerActivityComponent.inject(this)

        custom_toolbar.title = getString(R.string.app_name)
        setSupportActionBar(custom_toolbar)

        deserializer_list.adapter = deserializerEditorAdapter
        deserializer_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        if (intent.extras != null) {
            if (intent.extras.getLong("id", -2L) != -2L) {
                getDeserializerByIdUseCase.execute(intent.extras.getLong("id"))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    deserializer = it
                                    existing = true
                                },
                                {
                                    deserializer = GeneralDeserializer()
                                }
                        )
            } else {
                val filterContent: String = intent.extras.getString("filter", "")
                val type: String = intent.extras.getString("type", "")
                val sampleData = intent.extras.getByteArray("sampleData") ?: byteArrayOf()
                getDeserializerByFilterUseCase.execute(filterContent, type)
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
            }
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
            deserializer_list.adapter?.notifyDataSetChanged()
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
                                { Timber.e(it) }
                        )
            } else {
                addDeserializersUseCase.execute(updateDeserializerFromInputData()).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    setResult(RESULT_ADDED)
                                    finish()
                                },
                                { Timber.e(it) }
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
                        if (start > size || end > size) getString(R.string.bad_indexes)
                        else try {
                            d.type.converter.deserialize(
                                    if (start <= end) rawData.copyOfRange(start, end + 1)
                                    else rawData.inversedCopyOfRangeInclusive(start, end)
                            ).toString()
                        } catch (e: IllegalArgumentException) {
                            getString(R.string.invalid_byte_data)
                        },
                        d.color
                )
            }.let {
                val view = layoutInflater.inflate(R.layout.popup_field_list_preview, findViewById(android.R.id.content), false)

                val deserializedFieldsAdapter = DeserializedFieldsAdapter()
                view.deserialized_field_list_preview.adapter = deserializedFieldsAdapter
                view.deserialized_field_list_preview.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
                deserializedFieldsAdapter.setFields(it)
                AlertDialog.Builder(this)
                        .setView(view)
                        .setOnDismissListener {
                            deserializedFieldsAdapter.setFields(mutableListOf())
                            it.dismiss()
                        }
                        .create().also { dialog ->
                            view.setOnTouchListener { _, _ ->
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
            name = deserializer_name.editText?.text?.toString() ?: name ?: getString(R.string.deserializer_default_name)
            filter = deserializer_filter.editText?.text?.toString() ?: filter ?: ""
            filterType = filterType
            sampleData = getSampleDataBytes()
        }
    }

    private fun getSampleDataBytes(): ByteArray {
        return try {
            BaseEncoding.base16().decode(deserializer_sample_data?.editText?.text?.toString()
                    ?.replace("0x", "")?.replace(" ", "") ?: "")
        } catch (e: Exception) {
            byteArrayOf()
        }
    }
}