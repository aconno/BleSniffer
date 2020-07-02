package com.aconno.blesniffer.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.aconno.blesniffer.BleSnifferApplication
import com.aconno.blesniffer.R
import com.aconno.blesniffer.adapter.DeserializedFieldsAdapter
import com.aconno.blesniffer.adapter.DeserializerEditorAdapter
import com.aconno.blesniffer.adapter.inversedCopyOfRangeInclusive
import com.aconno.blesniffer.adapter.toHex
import com.aconno.blesniffer.dagger.editdeserializeractivity.DaggerEditDeserializerActivityComponent
import com.aconno.blesniffer.dagger.editdeserializeractivity.EditDeserializerActivityComponent
import com.aconno.blesniffer.dagger.editdeserializeractivity.EditDeserializerActivityModule
import com.aconno.blesniffer.data.deserializing.ParcelableDeserializer
import com.aconno.blesniffer.domain.byteformatter.ByteArrayFormatter
import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.GeneralDeserializer
import com.aconno.blesniffer.domain.interactor.deserializing.*
import com.aconno.blesniffer.preferences.BleSnifferPreferences
import com.aconno.blesniffer.ui.base.BaseActivity
import com.google.common.io.BaseEncoding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_deserializer.*
import kotlinx.android.synthetic.main.popup_field_list_preview.view.*
import timber.log.Timber
import java.lang.IndexOutOfBoundsException
import javax.inject.Inject


const val RESULT_UPDATED: Int = 0x10
const val RESULT_ADDED: Int = 0x11

class EditDeserializerActivity : BaseActivity() {

    @Inject
    lateinit var addDeserializersUseCase: AddDeserializerUseCase

    @Inject
    lateinit var getDeserializerByFilterUseCase: GetDeserializerByFilterUseCase

    @Inject
    lateinit var getDeserializerByIdUseCase: GetDeserializerByIdUseCase

    @Inject
    lateinit var updateDeserializerUseCase: UpdateDeserializerUseCase

    @Inject
    lateinit var generateSampleDataUseCase: GenerateSampleDataUseCase

    @Inject
    lateinit var preferences: BleSnifferPreferences

    var deserializer: Deserializer = GeneralDeserializer()
        set(value) {
            field = value.apply {
                deserializerEditorAdapter.fieldDeserializers = this.fieldDeserializers
                deserializer_filter_type.setSelection(Deserializer.Type.values().indexOf(
                    Deserializer.Type.valueOf(this.filterType.name)
                ))
                deserializer_filter.editText?.setText(this.filter)
                deserializer_name.editText?.setText(this.name)

                deserializer_sample_data.editText?.setText(bytesToString(sampleData))
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

    fun bytesToString(byteArray : ByteArray) =
        ByteArrayFormatter.getFormatter(preferences.getAdvertisementBytesDisplayMode()).formatBytes(byteArray)

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_deserializer)

        editDeserializerActivityComponent.inject(this)

        custom_toolbar.title = getString(R.string.app_name)
        setSupportActionBar(custom_toolbar)

        deserializer_list.adapter = deserializerEditorAdapter
        deserializer_list.isNestedScrollingEnabled = false
        deserializer_list.layoutManager = LinearLayoutManager(this)

        if(savedInstanceState != null) {
            val parcelableDeserializer = savedInstanceState.get(DESERIALIZER_KEY) as ParcelableDeserializer
            deserializer = GeneralDeserializer(
                parcelableDeserializer.id,
                parcelableDeserializer.name,
                parcelableDeserializer.filter,
                parcelableDeserializer.filterType,
                parcelableDeserializer.fieldDeserializers,
                parcelableDeserializer.sampleData
            )
            existing = savedInstanceState.getBoolean(EXISTING_KEY)
        } else {
            intent.extras?.let { extras ->
                if (extras.getLong("id", -2L) != -2L) {
                    getDeserializerByIdUseCase.execute(extras.getLong("id"))
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
                    val filterContent: String = extras.getString("filter", "")
                    val type: String = extras.getString("type", "")
                    val sampleData = extras.getByteArray("sampleData") ?: byteArrayOf()
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
            } ?: run {
                deserializer = GeneralDeserializer()
            }
        }


        deserializer_filter_type.adapter = ArrayAdapter(
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
            deserializerEditorAdapter.addEmptyValueDeserializer()
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
                                    if (start <= end) rawData.copyOfRange(start, end)
                                    else rawData.inversedCopyOfRangeInclusive(start, end)
                            ).toString()
                        } catch (e: IllegalArgumentException) {
                            getString(R.string.invalid_byte_data)
                        } catch (e: IndexOutOfBoundsException) {
                            getString(R.string.invalid_byte_data)
                        },
                        d.color
                )
            }.let {
                val view = layoutInflater.inflate(R.layout.popup_field_list_preview, findViewById(android.R.id.content), false)

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
                        view.setOnTouchListener { _, _ ->
                            dialog.dismiss()
                            true
                        }
                    }
                    .show()
            }
        }

        generate_sample_data.setOnClickListener {
            generateSampleDataUseCase.execute(updateDeserializerFromInputData())
                .subscribe { sampleData ->
                    deserializer_sample_data.editText?.setText(bytesToString(sampleData))
                }

        }

        deserializer_list.addItemDecoration(DividerItemDecoration(this,
            DividerItemDecoration.VERTICAL))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun saveDeserializer() {
        if (existing) {
            updateDeserializerUseCase.execute(updateDeserializerFromInputData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        setResult(RESULT_UPDATED)
                        finish()
                    },
                    { Timber.e(it) }
                ).also { addDisposable(it) }
        } else {
            addDeserializersUseCase.execute(updateDeserializerFromInputData())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        setResult(RESULT_ADDED)
                        finish()
                    },
                    { Timber.e(it) }
                ).also { addDisposable(it) }
        }
    }

    private fun updateDeserializerFromInputData(): Deserializer {
        return deserializer.apply {
            name = deserializer_name.editText?.text?.toString() ?: name
                ?: getString(R.string.deserializer_default_name)
            filter = deserializer_filter.editText?.text?.toString() ?: filter ?: ""
            filterType = filterType
            sampleData = getSampleDataBytes()
            fieldDeserializers.apply {
                clear()
                addAll(deserializerEditorAdapter.fieldDeserializers)
            }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.deserializer_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int? = item.itemId
        when (id) {
            R.id.action_save -> saveDeserializer()
            android.R.id.home -> finish()
            else -> return false
        }

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val deserializer = updateDeserializerFromInputData()
        outState.putParcelable(DESERIALIZER_KEY,ParcelableDeserializer(deserializer))
        outState.putBoolean(EXISTING_KEY,existing)
    }

    companion object {
        const val DESERIALIZER_KEY = "DESERIALIZER_KEY"
        const val EXISTING_KEY = "EXISTING_KEY"
    }

}