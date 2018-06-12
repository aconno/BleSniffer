package com.aconno.acnsensa.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.adapter.DeserializerAdapter
import com.aconno.acnsensa.adapter.ItemClickListener
import com.aconno.acnsensa.adapter.LongItemClickListener
import com.aconno.acnsensa.dagger.deserializerlistactivity.DaggerDeserializerListActivityComponent
import com.aconno.acnsensa.dagger.deserializerlistactivity.DeserializerListActivityComponent
import com.aconno.acnsensa.dagger.deserializerlistactivity.DeserializerListActivityModule
import com.aconno.acnsensa.device.PathUtils
import com.aconno.acnsensa.device.storage.DeserializerFileStorage
import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.interactor.deserializing.*
import com.aconno.acnsensa.model.AcnSensaPermission
import com.aconno.acnsensa.viewmodel.PermissionViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_deserializer_list.*
import kotlinx.android.synthetic.main.dialog_input_text.view.*
import timber.log.Timber
import java.util.regex.Pattern
import javax.inject.Inject


const val REQUEST_CODE_EDIT: Int = 0x00
const val REQUEST_CODE_EDIT_QUIT_ON_RESULT: Int = 0x01
const val REQUEST_CODE_OPEN_FILE: Int = 0x02

class DeserializerListActivity : AppCompatActivity(), ItemClickListener<Deserializer>, LongItemClickListener<Deserializer>, PermissionViewModel.PermissionCallbacks {

    private var snackbar: Snackbar? = null
    private val deserializerAdapter: DeserializerAdapter = DeserializerAdapter(mutableListOf(), this, this)

    @Inject
    lateinit var getAllDeserializersUseCase: GetAllDeserializersUseCase
    @Inject
    lateinit var addDeserializerUseCase: AddDeserializerUseCase
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

    val editDeserializerActivityComponent: DeserializerListActivityComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        DaggerDeserializerListActivityComponent.builder()
                .appComponent(acnSensaApplication?.appComponent)
                .deserializerListActivityModule(DeserializerListActivityModule(this))
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deserializer_list)

        editDeserializerActivityComponent.inject(this)

        custom_toolbar.title = getString(R.string.scanner_app_name)
        deserializer_list.layoutManager = LinearLayoutManager(this)
        deserializer_list.adapter = deserializerAdapter
        setSupportActionBar(custom_toolbar)
        permissionViewModel.requestWriteExternalStoragePermission()

        updateDeserializers()

        add_deserializer.setOnClickListener {
            startEditActivity("", Deserializer.Type.MAC)
        }

        intent.extras?.let {
            if (it.containsKey(EXTRA_FILTER_MAC)) {
                startEditActivity(it.getString(EXTRA_FILTER_MAC), Deserializer.Type.MAC, true)
            }
        }
    }

    override fun onItemClick(item: Deserializer) {
        startEditActivity(item.filter, item.filterType)
    }

    private fun startEditActivity(filter: String, type: String, quitOnResult: Boolean = false) {
        startActivityForResult(Intent(this, EditDeserializerActivity::class.java).apply {
            putExtra("filter", filter)
            putExtra("type", type)
        }, if (quitOnResult) REQUEST_CODE_EDIT_QUIT_ON_RESULT else REQUEST_CODE_EDIT)
    }

    private fun startEditActivity(filter: String,
                                  type: Deserializer.Type,
                                  quitOnResult: Boolean = false) =
            startEditActivity(filter, type.name, quitOnResult)

    override fun onLongItemClick(item: Deserializer): Boolean {
        AlertDialog.Builder(this)
                .setTitle(R.string.deserializer_actions_title)
                .setItems(R.array.deserializer_actions, { dialog, which ->
                    when (which) {
                        0 -> showDeleteItemDialog(item)
                        1 -> showExportItemDialog(item)
                    }
                    dialog.dismiss()
                }).create().show()
        return true
    }

    private fun showExportItemDialog(
            item: Deserializer,
            defaultFileName: String = item.filter.replace(Pattern.compile("[\\\\/:*?\"<>|]").toRegex(), "") + ".json"
    ) {
        val view: View = layoutInflater.inflate(R.layout.dialog_input_text, null)
        val textInput: EditText = view.text_input.editText ?: return
        textInput.hint = defaultFileName
        AlertDialog.Builder(this)
                .setMessage("Export item: " + item.filter)
                .setView(view)
                .setPositiveButton("Export") { dialog, _ ->
                    deserializerFileStorage.storeItem(item,
                            if (textInput.text.isEmpty()) defaultFileName
                            else textInput.text.toString()
                    ).let {
                        Toast.makeText(this, "Successfully exported file to: $it", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }

    private fun showDeleteItemDialog(item: Deserializer) {
        AlertDialog.Builder(this)
                .setMessage("Delete deserializer?")
                .setPositiveButton("Delete") { dialog, _ ->
                    deleteDeserializerUseCase.execute(item)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                updateDeserializers()
                                Toast.makeText(this, "Deserializer removed!", Toast.LENGTH_SHORT).show()
                            }
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
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
            R.id.action_export_all -> showExportAllDeserializersDialog()
            R.id.action_remove_all -> showRemoveAllDeserializersDialog()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showRemoveAllDeserializersDialog() {
        AlertDialog.Builder(this)
                .setMessage("Remove all deserializers?")
                .setPositiveButton("Remove") { dialog, which ->
                    getAllDeserializersUseCase.execute()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                deleteDeserializersUseCase.execute(it)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe {
                                            updateDeserializers()
                                            Toast.makeText(this@DeserializerListActivity, "${it.size} deserializers removed!", Toast.LENGTH_SHORT).show()
                                        }
                            }, {
                                Toast.makeText(this@DeserializerListActivity, "An error occurred while getting all the deserializers to be removed...", Toast.LENGTH_LONG).show()
                            })
                }
                .setNegativeButton("Cancel") { _, _ -> }
                .show()
    }

    private fun openFileDialog() {
        startActivityForResult(PathUtils.createGetContentIntent(), REQUEST_CODE_OPEN_FILE)
    }

    private fun showExportAllDeserializersDialog() {
        if (deserializerAdapter.deserializers.isEmpty()) {
            Toast.makeText(this, "No deserializers to export!", Toast.LENGTH_SHORT).show()
            return
        }
        val view: View = layoutInflater.inflate(R.layout.dialog_input_text, null)
        val textInput: EditText = view.text_input.editText ?: return
        textInput.hint = "all.json"
        AlertDialog.Builder(this)
                .setMessage("Export all deserializers")
                .setView(view)
                .setPositiveButton("Export") { dialog, _ ->
                    deserializerFileStorage.storeItems(deserializerAdapter.deserializers,
                            if (textInput.text.isEmpty()) "all.json"
                            else textInput.text.toString()
                    ).let {
                        Toast.makeText(this, "Successfully exported file to: $it", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }


    override fun permissionAccepted(actionCode: Int) {
    }

    override fun permissionDenied(actionCode: Int) {
        when (actionCode) {
            AcnSensaPermission.WRITE_EXTERNAL_STORAGE_CODE -> {
                Toast.makeText(
                        this,
                        "Permission denied, you will not be able to use any exporting/importing features...",
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
            REQUEST_CODE_EDIT -> updateDeserializers()
            REQUEST_CODE_EDIT_QUIT_ON_RESULT -> finish()
            REQUEST_CODE_OPEN_FILE -> {
                if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(this, "ERROR CODE 1: RESULT_OK != true", Toast.LENGTH_LONG).show()
                    Crashlytics.logException(Exception("Result: $resultCode"))
                    return
                }
                if (data == null) {
                    Toast.makeText(this, "ERROR CODE 2: Data == null", Toast.LENGTH_LONG).show()
                    Crashlytics.logException(Exception("Data == null"))
                } else {
                    if (data.data == null) {
                        Toast.makeText(this, "ERROR CODE 3: Data.data == null", Toast.LENGTH_LONG).show()
                        Crashlytics.logException(Exception("data.data == null"))
                    }
                }
                data?.data?.let {
                    val path = PathUtils.getPath(this, it)
                    if (path != null) {
                        deserializerFileStorage.readItems(path).subscribe({ list ->
                            Toast.makeText(this, "Loaded file with ${list.size} deserializer definitions!", Toast.LENGTH_LONG).show()
                            addDeserializersUseCase.execute(list)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe {
                                        Toast.makeText(this, "Loaded ${list.size} definitions!", Toast.LENGTH_LONG).show()
                                        updateDeserializers()
                                    }
                        }, {
                            if (it.message?.contains("Permission denied") == true && !permissionViewModel.hasSelfPermission(AcnSensaPermission.READ_EXTERNAL_STORAGE)) {
                                permissionViewModel.checkRequestAndRun(AcnSensaPermission.WRITE_EXTERNAL_STORAGE, {
                                    onActivityResult(requestCode, resultCode, data)
                                }, {
                                    runOnUiThread {
                                        Timber.e("Permission Denied")
                                        Toast.makeText(this, "Permission denied, unable to load items. Please grant the permission.", Toast.LENGTH_LONG).show()
                                    }
                                })
                            } else {
                                Toast.makeText(this, "ERROR CODE 4: There was an error reading the file.", Toast.LENGTH_LONG).show()
                                Crashlytics.logException(it)
                            }
                        })
                    } else {
                        Toast.makeText(this, "ERROR CODE 5: Path for uri $it is null", Toast.LENGTH_LONG).show()
                        Crashlytics.logException(Exception("Path for uri $it is null"))
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        permissionViewModel.checkGrantedPermission(grantResults, requestCode)
    }

}