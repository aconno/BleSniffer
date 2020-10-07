package com.aconno.blesniffer.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.aconno.blesniffer.BleSnifferApplication
import com.aconno.blesniffer.R
import com.aconno.blesniffer.adapter.DeserializerAdapter
import com.aconno.blesniffer.adapter.ItemClickListener
import com.aconno.blesniffer.adapter.LongItemClickListener
import com.aconno.blesniffer.dagger.deserializerlistactivity.DaggerDeserializerListActivityComponent
import com.aconno.blesniffer.dagger.deserializerlistactivity.DeserializerListActivityComponent
import com.aconno.blesniffer.dagger.deserializerlistactivity.DeserializerListActivityModule
import com.aconno.blesniffer.device.storage.DeserializerFileStorage
import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.interactor.deserializing.AddDeserializersUseCase
import com.aconno.blesniffer.domain.interactor.deserializing.DeleteDeserializerUseCase
import com.aconno.blesniffer.domain.interactor.deserializing.DeleteDeserializersUseCase
import com.aconno.blesniffer.domain.interactor.deserializing.GetAllDeserializersUseCase
import com.aconno.blesniffer.getHexFormatterForAdvertisementBytesDisplayMode
import com.aconno.blesniffer.model.BleSnifferPermission
import com.aconno.blesniffer.preferences.BleSnifferPreferences
import com.aconno.blesniffer.viewmodel.PermissionViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_deserializer_list.*
import timber.log.Timber
import java.io.FileNotFoundException
import java.util.regex.Pattern
import javax.inject.Inject


const val REQUEST_CODE_EDIT: Int = 0x00
const val REQUEST_CODE_EDIT_QUIT_ON_RESULT: Int = 0x01
const val REQUEST_CODE_OPEN_FILE: Int = 0x02
const val REQUEST_CODE_SELECT_EXPORT_FILE_PATH: Int = 0x03
const val DEFAULT_EXPORT_FILE_NAME = "all.json"

class DeserializerListActivity : AppCompatActivity(), ItemClickListener<Deserializer>,
    LongItemClickListener<Deserializer>, PermissionViewModel.PermissionCallbacks {

    private lateinit var deserializerAdapter: DeserializerAdapter

    @Inject
    lateinit var getAllDeserializersUseCase: GetAllDeserializersUseCase

    @Inject
    lateinit var addDeserializersUseCase: AddDeserializersUseCase

    @Inject
    lateinit var deleteDeserializerUseCase: DeleteDeserializerUseCase

    @Inject
    lateinit var deleteDeserializersUseCase: DeleteDeserializersUseCase

    @Inject
    lateinit var permissionViewModel: PermissionViewModel

    @Inject
    lateinit var deserializerFileStorage: DeserializerFileStorage

    @Inject
    lateinit var preferences: BleSnifferPreferences

    val editDeserializerActivityComponent: DeserializerListActivityComponent by lazy {
        val bleSnifferApplication: BleSnifferApplication? = application as? BleSnifferApplication
        DaggerDeserializerListActivityComponent.builder()
            .appComponent(bleSnifferApplication?.appComponent)
            .deserializerListActivityModule(DeserializerListActivityModule(this))
            .build()
    }

    private var deserializersToExport: List<Deserializer>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deserializer_list)

        editDeserializerActivityComponent.inject(this)

        val dataFilterFormatter =
            getHexFormatterForAdvertisementBytesDisplayMode(preferences.getAdvertisementBytesDisplayMode())
        deserializerAdapter = DeserializerAdapter(mutableListOf(), this, this, dataFilterFormatter)

        custom_toolbar.title = getString(R.string.app_name)
        deserializer_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        deserializer_list.adapter = deserializerAdapter
        setSupportActionBar(custom_toolbar)
        permissionViewModel.requestWriteExternalStoragePermission()

        updateDeserializers()

        fab_add_deserializer.setOnClickListener {
            startEditActivity()
        }

        intent.extras?.let {
            if (it.containsKey(ScanAnalyzerActivity.EXTRA_FILTER_MAC)) {
                startEditActivity(
                    it.getString(ScanAnalyzerActivity.EXTRA_FILTER_MAC, null),
                    Deserializer.Type.MAC,
                    it.getByteArray(ScanAnalyzerActivity.EXTRA_SAMPLE_DATA)
                        ?: byteArrayOf(),
                    true
                )
            }
        }
    }

    override fun onItemClick(item: Deserializer) {
        startEditActivity(item.id)
    }

    private fun startEditActivity(
        filter: String,
        type: String,
        sampleData: ByteArray = byteArrayOf(),
        quitOnResult: Boolean = false
    ) {
        startActivityForResult(Intent(this, EditDeserializerActivity::class.java).apply {
            putExtra("filter", filter)
            putExtra("type", type)
            putExtra("sampleData", sampleData)
        }, if (quitOnResult) REQUEST_CODE_EDIT_QUIT_ON_RESULT else REQUEST_CODE_EDIT)
    }

    private fun startEditActivity(id: Long? = -1) {
        startActivityForResult(Intent(this, EditDeserializerActivity::class.java).apply {
            putExtra("id", id)
        }, REQUEST_CODE_EDIT)
    }

    private fun startEditActivity(
        filter: String,
        type: Deserializer.Type,
        sampleData: ByteArray = byteArrayOf(),
        quitOnResult: Boolean = false
    ) =
        startEditActivity(filter, type.name, sampleData, quitOnResult)

    override fun onLongItemClick(item: Deserializer): Boolean {
        AlertDialog.Builder(this)
            .setTitle(R.string.deserializer_actions_title)
            .setItems(R.array.deserializer_actions) { dialog, which ->
                when (which) {
                    0 -> showRemoveItemDialog(item)
                    1 -> {
                        deserializersToExport = listOf(item)
                        startSelectExportFilePathActivity(
                            generateDefaultExportFileNameForDeserializer(item)
                        )
                    }
                }
                dialog.dismiss()
            }.create().show()
        return true
    }

    private fun generateDefaultExportFileNameForDeserializer(deserializer: Deserializer): String {
        return deserializer.name.replace(Pattern.compile("[\\\\/:*?\"<>|]").toRegex(), "") + ".json"
    }

    private fun showRemoveItemDialog(item: Deserializer) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.remove_deserializer))
            .setPositiveButton(getString(R.string.remove)) { dialog, _ ->
                deleteDeserializerUseCase.execute(item)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        updateDeserializers()
                        Toast.makeText(this, R.string.deserializer_removed, Toast.LENGTH_SHORT)
                            .show()
                    }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun updateDeserializers() {
        getAllDeserializersUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(deserializerAdapter::setDeserializers)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.deserializer_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id: Int? = item?.itemId
        when (id) {
            R.id.action_import -> openFileDialog()
            R.id.action_export_all -> {
                deserializersToExport = deserializerAdapter.deserializers
                startSelectExportFilePathActivity(DEFAULT_EXPORT_FILE_NAME)
            }
            R.id.action_remove_all -> showRemoveAllDeserializersDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showRemoveAllDeserializersDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.remove_all_deserializers)
            .setPositiveButton(R.string.remove) { _, _ ->
                getAllDeserializersUseCase.execute()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        deleteDeserializersUseCase.execute(it)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                updateDeserializers()
                                Toast.makeText(
                                    this@DeserializerListActivity,
                                    getString(R.string.x_deserializers_removed, it.size),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }, {
                        Toast.makeText(
                            this@DeserializerListActivity,
                            R.string.error_removing_deserializers,
                            Toast.LENGTH_LONG
                        ).show()
                    })
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun openFileDialog() {
        startActivityForResult(createGetContentIntent(), REQUEST_CODE_OPEN_FILE)
    }

    private fun createGetContentIntent(): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        return intent
    }

    private fun startSelectExportFilePathActivity(defaultFileName: String) {
        val exportIntent: Intent = Intent().apply {
            type = "text/*"
            action = Intent.ACTION_CREATE_DOCUMENT
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, defaultFileName)
        }

        startActivityForResult(exportIntent, REQUEST_CODE_SELECT_EXPORT_FILE_PATH)
    }

    private fun exportDeserializers(exportFileUri: Uri) {
        deserializersToExport?.let { deserializers ->
            permissionViewModel.checkRequestAndRunIfGranted(
                BleSnifferPermission.WRITE_EXTERNAL_STORAGE
            ) {
                deserializerFileStorage.storeItems(
                    deserializers,
                    exportFileUri
                ).let {
                    Toast.makeText(
                        this,
                        getString(R.string.file_successfully_exported),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    override fun permissionAccepted(actionCode: Int) {
    }

    override fun permissionDenied(actionCode: Int) {
        when (actionCode) {
            BleSnifferPermission.WRITE_EXTERNAL_STORAGE_CODE -> {
                Toast.makeText(
                    this,
                    R.string.permission_denied_export_import,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun showRationale(actionCode: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_EDIT -> {
                updateDeserializers()
                when (resultCode) {
                    RESULT_UPDATED -> Toast.makeText(
                        applicationContext,
                        R.string.updated_deserializer,
                        Toast.LENGTH_LONG
                    ).show()
                    RESULT_ADDED -> Toast.makeText(
                        applicationContext,
                        R.string.created_deserializer,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            REQUEST_CODE_EDIT_QUIT_ON_RESULT -> {
                when (resultCode) {
                    RESULT_UPDATED -> Toast.makeText(
                        applicationContext,
                        R.string.updated_deserializer,
                        Toast.LENGTH_LONG
                    ).show()
                    RESULT_ADDED -> Toast.makeText(
                        applicationContext,
                        R.string.created_deserializer,
                        Toast.LENGTH_LONG
                    ).show()
                }
                finish()
            }
            REQUEST_CODE_OPEN_FILE -> {
                if (resultCode != Activity.RESULT_OK) {
                    FirebaseCrashlytics.getInstance()
                        .recordException(Exception("Result: $resultCode"))
                    return
                }
                if (data == null) {
                    FirebaseCrashlytics.getInstance().recordException(Exception("Data == null"))
                } else {
                    if (data.data == null) {
                        FirebaseCrashlytics.getInstance()
                            .recordException(Exception("data.data == null"))
                    }
                }
                data?.data?.let {
                    try {
                        this.contentResolver.openInputStream(it)?.use {
                            deserializerFileStorage.readItems(it).subscribe({ list ->
                                Toast.makeText(
                                    this,
                                    getString(
                                        R.string.loaded_file_with_x_deserializer_definitions,
                                        list.size
                                    ),
                                    Toast.LENGTH_LONG
                                ).show()
                                addDeserializersUseCase.execute(list)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        Toast.makeText(
                                            this,
                                            getString(
                                                R.string.imported_x_deserializer_definitions,
                                                list.size
                                            ),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        updateDeserializers()
                                    }
                            }, {
                                if (it.message?.contains("Permission denied") == true && !permissionViewModel.hasSelfPermission(
                                        BleSnifferPermission.READ_EXTERNAL_STORAGE
                                    )
                                ) {
                                    permissionViewModel.checkRequestAndRun(
                                        BleSnifferPermission.WRITE_EXTERNAL_STORAGE,
                                        {
                                            onActivityResult(requestCode, resultCode, data)
                                        },
                                        {
                                            runOnUiThread {
                                                Toast.makeText(
                                                    this,
                                                    R.string.permission_denied_loading_items,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                openPermissionSettingsScreen()
                                            }
                                        })
                                } else {
                                    Timber.e(it)
                                    FirebaseCrashlytics.getInstance().recordException(it)
                                }
                            })
                        }
                    } catch (e: FileNotFoundException) {
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                getString(R.string.file_not_found),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
            REQUEST_CODE_SELECT_EXPORT_FILE_PATH -> {
                data?.data?.let { exportDeserializers(it) }
            }
        }
    }

    private fun openPermissionSettingsScreen() {
        val settings =
            Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        settings.addCategory(Intent.CATEGORY_DEFAULT)
        settings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(settings)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionViewModel.checkGrantedPermission(grantResults, requestCode)
    }

}