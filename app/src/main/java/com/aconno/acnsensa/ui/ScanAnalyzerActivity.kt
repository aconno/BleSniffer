package com.aconno.acnsensa.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothScanningService
import com.aconno.acnsensa.R
import com.aconno.acnsensa.adapter.LongItemClickListener
import com.aconno.acnsensa.adapter.ScanAnalyzerAdapter
import com.aconno.acnsensa.adapter.ScanRecordListener
import com.aconno.acnsensa.dagger.scananalyzeractivity.DaggerScanAnalyzerActivityComponent
import com.aconno.acnsensa.dagger.scananalyzeractivity.ScanAnalyzerActivityComponent
import com.aconno.acnsensa.dagger.scananalyzeractivity.ScanAnalyzerActivityModule
import com.aconno.acnsensa.domain.BluetoothState
import com.aconno.acnsensa.domain.beacon.Beacon
import com.aconno.acnsensa.domain.interactor.deserializing.GetAllDeserializersUseCase
import com.aconno.acnsensa.domain.model.ScanEvent
import com.aconno.acnsensa.viewmodel.BeaconListViewModel
import com.aconno.acnsensa.viewmodel.BluetoothScanningViewModel
import com.aconno.acnsensa.viewmodel.BluetoothViewModel
import com.aconno.acnsensa.viewmodel.PermissionViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_scan_analyzer.*
import timber.log.Timber
import javax.inject.Inject

const val EXTRA_FILTER_MAC: String = "com.acconno.acnsensa.FILTER_MAC"

class ScanAnalyzerActivity : AppCompatActivity(), PermissionViewModel.PermissionCallbacks, ScanRecordListener, LongItemClickListener<Beacon> {

    @Inject
    lateinit var bluetoothViewModel: BluetoothViewModel

    @Inject
    lateinit var bluetoothScanningViewModel: BluetoothScanningViewModel

    @Inject
    lateinit var permissionViewModel: PermissionViewModel

    @Inject
    lateinit var beaconListViewModel: BeaconListViewModel

    @Inject
    lateinit var getAllDeserializersUseCase: GetAllDeserializersUseCase

    private lateinit var scanAnalyzerAdapter: ScanAnalyzerAdapter

    private var mainMenu: Menu? = null

    private var snackbar: Snackbar? = null

    val scanAnalyzerActivityComponent: ScanAnalyzerActivityComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        DaggerScanAnalyzerActivityComponent.builder()
                .appComponent(acnSensaApplication?.appComponent)
                .scanAnalyzerActivityModule(ScanAnalyzerActivityModule(this))
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_analyzer)

        scanAnalyzerActivityComponent.inject(this)

        snackbar =
                Snackbar.make(scan_analyzer_root, R.string.bt_disabled, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.enable) { bluetoothViewModel.enableBluetooth() }

        snackbar?.setActionTextColor(resources.getColor(R.color.primaryColor))

        custom_toolbar.title = getString(R.string.app_name)
        setSupportActionBar(custom_toolbar)

        invalidateOptionsMenu()

        if (savedInstanceState == null) {
            initViews()
        }
    }


    private fun initViews() {
        getAllDeserializersUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { deserializers ->
                    scanAnalyzerAdapter = ScanAnalyzerAdapter(mutableListOf(), this, deserializers.toMutableList(), this)
                    val linearLayoutManager = LinearLayoutManager(this)
                    scan_list.layoutManager = linearLayoutManager
                    scan_list.adapter = scanAnalyzerAdapter
                    scan_list.addItemDecoration(DividerItemDecoration(
                            this, linearLayoutManager.orientation
                    ))
                    beaconListViewModel.getBeaconLiveData()
                            .observe(this, Observer {
                                it?.let {
                                    scanAnalyzerAdapter.logScan(it)
                                }
                            })
                }
    }

    override fun onRecordAdded() {
        scan_list.smoothScrollToPosition(0)
    }

    override fun onResume() {
        super.onResume()
        bluetoothScanningViewModel.getResult().observe(this, Observer { handleScanEvent(it) })
        bluetoothViewModel.observeBluetoothState()
        bluetoothViewModel.bluetoothState.observe(this, Observer { onBluetoothStateChange(it) })

    }

    override fun onPause() {
        super.onPause()
        bluetoothViewModel.stopObservingBluetoothState()
    }

    private fun onBluetoothStateChange(bluetoothState: BluetoothState?) {
        when (bluetoothState?.state) {
            BluetoothState.BLUETOOTH_OFF -> onBluetoothOff()
            BluetoothState.BLUETOOTH_ON -> onBluetoothOn()
        }
    }

    private fun onBluetoothOff() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.setVisible(false)
        }
        snackbar?.show()
    }

    private fun onBluetoothOn() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.setVisible(true)
        }
        snackbar?.dismiss()
    }

    private fun handleScanEvent(scanEvent: ScanEvent?) {
        val eventType: Int? = scanEvent?.type
        when (eventType) {
            ScanEvent.SCAN_FAILED_ALREADY_STARTED -> onScanFailedAlreadyStarted()
            ScanEvent.SCAN_FAILED -> onScanFailed()
            ScanEvent.SCAN_START -> onScanStart()
            ScanEvent.SCAN_STOP -> onScanStop()
        }
    }

    private fun onScanFailedAlreadyStarted() {
        //Do nothing.
    }

    override fun onLongItemClick(item: Beacon): Boolean {
        startActivityForResult(Intent(this, DeserializerListActivity::class.java).apply {
            putExtra(EXTRA_FILTER_MAC, item.address)
        }, 0x00)
        return true
    }

    private fun onScanFailed() {
        onScanStop()
    }

    private fun onScanStart() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.let {
                it.isChecked = true
                it.setTitle(getString(R.string.stop_scan))
            }
        }
    }

    private fun onScanStop() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.let {
                it.isChecked = false
                it.setTitle(getString(R.string.start_scan))
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        mainMenu = menu
        mainMenu?.clear()
        menuInflater.inflate(R.menu.scanner_menu, menu)

        (mainMenu?.findItem(R.id.search)?.actionView as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    Timber.e(it)
                    scanAnalyzerAdapter.filter = it
                }
                return true
            }
        })

        mainMenu?.findItem(R.id.action_toggle_scan)?.let {
            setScanMenuLabel(it)
            val state = bluetoothViewModel.bluetoothState.value
            when (state?.state) {
                BluetoothState.BLUETOOTH_ON -> it.setVisible(true)
                else -> it.setVisible(false)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id: Int? = item?.itemId
        when (id) {
            R.id.action_toggle_scan -> toggleScan(item)
            R.id.action_start_deserializer_list_activity -> startDeserializerListActivity()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun startDeserializerListActivity() {
        startActivityForResult(Intent(this, DeserializerListActivity::class.java), 0x00)
    }


    private fun toggleScan(item: MenuItem?) {
        item?.let {
            if (item.isChecked) {
                bluetoothScanningViewModel.stopScanning()
            } else {
                permissionViewModel.requestAccessFineLocation()
            }
        }
    }

    private fun setScanMenuLabel(menuItem: MenuItem) {
        if (BluetoothScanningService.isRunning()) {
            menuItem.title = getString(R.string.stop_scan)
            menuItem.isChecked = true
        } else {
            menuItem.title = getString(R.string.start_scan)
            menuItem.isChecked = false
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        permissionViewModel.checkGrantedPermission(grantResults, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        getAllDeserializersUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { deserializers ->
                    scanAnalyzerAdapter.updateDeserializers(deserializers.toMutableList())
                }
    }


    override fun permissionAccepted(actionCode: Int) {
        bluetoothScanningViewModel.startScanning()
        //TODO: Permission accepted
    }

    override fun permissionDenied(actionCode: Int) {
        //TODO: Permission denied
    }

    override fun showRationale(actionCode: Int) {
        //TODO: Show rationale
    }
}