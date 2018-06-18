package com.aconno.blesniffer.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SearchView
import com.aconno.blesniffer.BleSnifferApplication
import com.aconno.blesniffer.BluetoothScanningService
import com.aconno.blesniffer.R
import com.aconno.blesniffer.adapter.LongItemClickListener
import com.aconno.blesniffer.adapter.ScanAnalyzerAdapter
import com.aconno.blesniffer.adapter.ScanRecordListener
import com.aconno.blesniffer.dagger.scananalyzeractivity.DaggerScanAnalyzerActivityComponent
import com.aconno.blesniffer.dagger.scananalyzeractivity.ScanAnalyzerActivityComponent
import com.aconno.blesniffer.dagger.scananalyzeractivity.ScanAnalyzerActivityModule
import com.aconno.blesniffer.domain.interactor.deserializing.GetAllDeserializersUseCase
import com.aconno.blesniffer.domain.model.ScanEvent
import com.aconno.blesniffer.domain.model.ScanResult
import com.aconno.blesniffer.domain.scanning.BluetoothState
import com.aconno.blesniffer.viewmodel.BluetoothScanningViewModel
import com.aconno.blesniffer.viewmodel.BluetoothViewModel
import com.aconno.blesniffer.viewmodel.PermissionViewModel
import com.aconno.blesniffer.viewmodel.ScanResultViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_scan_analyzer.*
import javax.inject.Inject


const val EXTRA_FILTER_MAC: String = "com.acconno.blesniffer.FILTER_MAC"
const val EXTRA_SAMPLE_DATA: String = "com.acconno.blesniffer.SAMPLE_DATA"

class ScanAnalyzerActivity : AppCompatActivity(), PermissionViewModel.PermissionCallbacks, ScanRecordListener, LongItemClickListener<ScanResult> {

    @Inject
    lateinit var bluetoothViewModel: BluetoothViewModel

    @Inject
    lateinit var bluetoothScanningViewModel: BluetoothScanningViewModel

    @Inject
    lateinit var permissionViewModel: PermissionViewModel

    @Inject
    lateinit var scanResultViewModel: ScanResultViewModel

    @Inject
    lateinit var getAllDeserializersUseCase: GetAllDeserializersUseCase

    private val scanAnalyzerAdapter: ScanAnalyzerAdapter by lazy {
        ScanAnalyzerAdapter(this, this)
    }
    private var adapterDataObserver: Observer<ScanResult>? = null

    private var mainMenu: Menu? = null

    private var snackbar: Snackbar? = null
    private var filter: String? = null

    val scanAnalyzerActivityComponent: ScanAnalyzerActivityComponent by lazy {
        val bleSnifferApplication: BleSnifferApplication? = application as? BleSnifferApplication
        DaggerScanAnalyzerActivityComponent.builder()
                .appComponent(bleSnifferApplication?.appComponent)
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
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        scan_list.layoutManager = linearLayoutManager
        scan_list.adapter = scanAnalyzerAdapter
        scan_list.addItemDecoration(DividerItemDecoration(
                this, linearLayoutManager.orientation
        ))
        (scan_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        scan_list.itemAnimator = null

        getAllDeserializersUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { deserializers ->
                    scanAnalyzerAdapter.updateDeserializers(deserializers)
                }
    }

    private fun createAdapterDataObserver(): Observer<ScanResult> {
        return Observer {
            it?.let { result ->
                filter.let {
                    if (it == null || (result.device.macAddress.contains(it, ignoreCase = true) || result.device.name.contains(it, ignoreCase = true))) {
                        scanAnalyzerAdapter.logScan(result)
                    }
                }
            }
        }
    }

    override fun onRecordAdded(size: Int) {
        scan_list.scrollToPosition(size)
    }

    override fun onResume() {
        super.onResume()
        bluetoothScanningViewModel.getResult().observe(this, Observer { handleScanEvent(it) })
        bluetoothViewModel.observeBluetoothState()
        bluetoothViewModel.bluetoothState.observe(this, Observer { onBluetoothStateChange(it) })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        bluetoothViewModel.stopObservingBluetoothState()
        adapterDataObserver?.let {
            scanResultViewModel.getScanResultsLiveData().removeObserver(it)
        }
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

    override fun onLongItemClick(item: ScanResult): Boolean {
        startActivityForResult(Intent(this, DeserializerListActivity::class.java).apply {
            putExtra(EXTRA_FILTER_MAC, item.device.macAddress)
            putExtra(EXTRA_SAMPLE_DATA, item.advertisement.rawData)
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
        if (adapterDataObserver != null) {
            scanResultViewModel.getScanResultsLiveData().removeObservers(this)
        }
        adapterDataObserver = createAdapterDataObserver()
        adapterDataObserver?.apply {
            scanResultViewModel.getScanResultsLiveData().observe(this@ScanAnalyzerActivity, this)
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

        val searchView = mainMenu?.findItem(R.id.search)?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filter = null
                query?.let {
                    filter = if (query.isNotEmpty()) query
                    else null
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Clean this up somehow
                val but = searchView.context.resources.getIdentifier("android:id/search_close_btn", null, null)
                val closeButton = findViewById<ImageView>(but)
                closeButton.setOnClickListener {
                    searchView.clearFocus()
                    searchView.setQuery("", false)
                    filter = null
                }
                filter = null
                newText?.let {
                    filter = if (newText.isNotEmpty()) newText
                    else null
                }
                return false
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
            R.id.action_clear -> {
                scanAnalyzerAdapter.clear()
            }
            R.id.search -> (mainMenu?.findItem(R.id.search)?.actionView as SearchView).performClick()
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