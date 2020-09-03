package com.aconno.blesniffer.ui

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.aconno.blesniffer.BleSnifferApplication
import com.aconno.blesniffer.BluetoothScanner
import com.aconno.blesniffer.R
import com.aconno.blesniffer.adapter.LongItemClickListener
import com.aconno.blesniffer.adapter.ScanAnalyzerAdapter
import com.aconno.blesniffer.adapter.ScanRecordListener
import com.aconno.blesniffer.dagger.scananalyzeractivity.DaggerScanAnalyzerActivityComponent
import com.aconno.blesniffer.dagger.scananalyzeractivity.ScanAnalyzerActivityComponent
import com.aconno.blesniffer.dagger.scananalyzeractivity.ScanAnalyzerActivityModule
import com.aconno.blesniffer.domain.advertisementfilter.ManufacturerDataAdvertisementFilter
import com.aconno.blesniffer.domain.byteformatter.ByteArrayFormatter
import com.aconno.blesniffer.domain.deserializing.DeserializerFinder
import com.aconno.blesniffer.domain.interactor.deserializing.GetAllDeserializersUseCase
import com.aconno.blesniffer.domain.model.ScanEvent
import com.aconno.blesniffer.domain.model.ScanResult
import com.aconno.blesniffer.domain.scanning.BluetoothState
import com.aconno.blesniffer.preferences.BleSnifferPreferences
import com.aconno.blesniffer.viewmodel.BluetoothScanningViewModel
import com.aconno.blesniffer.viewmodel.BluetoothViewModel
import com.aconno.blesniffer.viewmodel.PermissionViewModel
import com.aconno.blesniffer.viewmodel.ScanResultViewModel
import com.aconno.blesniffer.work.SyncDeserializersWorker
import com.aconno.hexinputlib.formatter.HexFormatters
import com.aconno.hexinputlib.handleBackPressedWithHexKeyboardInContentView
import com.aconno.hexinputlib.setContentViewWithHexKeyboardAutoAdded
import com.aconno.hexinputlib.ui.editor.HexEditText
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_scan_analyzer.*
import kotlinx.android.synthetic.main.activity_scan_analyzer.custom_toolbar
import timber.log.Timber
import javax.inject.Inject


class ScanAnalyzerActivity : AppCompatActivity(), PermissionViewModel.PermissionCallbacks,
    ScanRecordListener, LongItemClickListener<ScanResult> {
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

    private lateinit var disposable: Disposable

    private lateinit var scanResultObserver: Observer<ScanResult>

    private var lastObserverCreateTime: Long = 0

    private var mainMenu: Menu? = null

    private var snackbar: Snackbar? = null

    private var filter: String? = null

    @Inject
    lateinit var deserializerFinder : DeserializerFinder

    private val scanAnalyzerAdapter: ScanAnalyzerAdapter by lazy {
        val byteArrayFormatter = ByteArrayFormatter.getFormatter(preferences.getAdvertisementBytesDisplayMode())
        ScanAnalyzerAdapter(this, this, byteArrayFormatter, getAdvertisementDataFilter(), deserializerFinder)
    }

    @Inject
    lateinit var preferences: BleSnifferPreferences

    private val scanAnalyzerActivityComponent: ScanAnalyzerActivityComponent by lazy {
        val bleSnifferApplication: BleSnifferApplication? = application as? BleSnifferApplication
        DaggerScanAnalyzerActivityComponent.builder()
            .appComponent(bleSnifferApplication?.appComponent)
            .scanAnalyzerActivityModule(ScanAnalyzerActivityModule(this))
            .build()
    }

    private var attachScrollToTop = true

    //this flag indicates that user requested scan start and has not yet requested scan stop
    private var shouldBeScanning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWithHexKeyboardAutoAdded(R.layout.activity_scan_analyzer)
        scanAnalyzerActivityComponent.inject(this)

        initScanResultObserver()

        createSnackbar()

        invalidateOptionsMenu()

        setScanStatus()

        initViews()

        getAllDeserializers {
            loadLogs(savedInstanceState)
            scan_list.scrollToPosition(scanAnalyzerAdapter.itemCount - 1)
        }

        savedInstanceState?.let {
            shouldBeScanning = it.getBoolean(SHOULD_BE_SCANNING_KEY)
        }

    }

    override fun onBackPressed() {
        handleBackPressedWithHexKeyboardInContentView()
    }

    override fun onResume() {
        super.onResume()
        if (BluetoothScanner.isRunning()) onScanStart()
        else onScanStop()
        bluetoothScanningViewModel.getResult().observe(this, Observer { handleScanEvent(it) })
        bluetoothViewModel.observeBluetoothState()
        bluetoothViewModel.bluetoothState.observe(this, Observer { onBluetoothStateChange(it) })
        Timber.e("Observer Added")
        scanResultViewModel.getScanResultsLiveData().observe(this, scanResultObserver)
        getSyncDeserializersLiveData().observe(this, Observer {
            onWorkStateChanged(it)
        })

        scanAnalyzerAdapter.advertisementDataFormatter =
            ByteArrayFormatter.getFormatter(preferences.getAdvertisementBytesDisplayMode())
        scanAnalyzerAdapter.advertisementDataFilter = getAdvertisementDataFilter()
    }

    private fun getAdvertisementDataFilter() = when {
        preferences.isShowOnlyManufacturerData() -> ManufacturerDataAdvertisementFilter()
        else -> null
    }

    override fun onPause() {
        super.onPause()
        bluetoothViewModel.stopObservingBluetoothState()
        Timber.e("Observer Removed")
        scanResultViewModel.getScanResultsLiveData().removeObserver(scanResultObserver)
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
        getAllDeserializers()
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun initScanResultObserver() {
        scanResultObserver = Observer {
            it?.let { result ->
                filter.let { filter ->
                    if (filter == null || ((result.device.macAddress.contains(
                            filter,
                            ignoreCase = true
                        ) || result.device.name.contains(
                            filter,
                            ignoreCase = true
                        ) && result.timestamp >= lastObserverCreateTime))
                    ) {
                        scanAnalyzerAdapter.logScan(result)
                    }
                }
            }
        }
    }

    private fun createSnackbar() {
        snackbar =
            Snackbar.make(scan_analyzer_root, R.string.bt_disabled, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.enable) { bluetoothViewModel.enableBluetooth() }

        snackbar?.setActionTextColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.primaryColor
            )
        )
    }

    private fun setScanStatus() {
        if (BluetoothScanner.isRunning()) onScanStart()
        else onScanStop()
    }


    private fun initViews() {
        setSupportActionBar(custom_toolbar)
        custom_toolbar.title = getString(R.string.app_name)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        scan_list.layoutManager = linearLayoutManager
        scan_list.adapter = scanAnalyzerAdapter
        scan_list.addItemDecoration(
            DividerItemDecoration(
                this, linearLayoutManager.orientation
            )
        )
        (scan_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
        scan_list.itemAnimator = null

        scanAnalyzerAdapter.hideMissingSerializer = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        scan_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(!recyclerView.canScrollVertically(-1)) {
                    attachScrollToTop = true
                }

                if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    attachScrollToTop = false
                }
            }

        })

        advertisement_filter_layout?.let { initSearchView(it as ViewGroup) }

    }

    override fun onRecordAdded(size: Int) {
       if(attachScrollToTop) {
           scan_list.scrollToPosition(size)
       }
    }

    private fun getSyncDeserializersLiveData(): LiveData<WorkInfo> {
        return WorkManager.getInstance().getWorkInfoByIdLiveData(SyncDeserializersWorker.WORKER_ID)
    }

    private fun onWorkStateChanged(workInfo: WorkInfo) {
        Timber.d(workInfo.state.name)
        if (workInfo.state == WorkInfo.State.ENQUEUED) {
            getAllDeserializers()
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
        startScan()
    }

    override fun onLongItemClick(item: ScanResult): Boolean {
        startActivityForResult(Intent(this, DeserializerListActivity::class.java).apply {
            putExtra(EXTRA_FILTER_MAC, item.device.macAddress)
            putExtra(EXTRA_SAMPLE_DATA, item.advertisement.rawData)
        }, 0x00)
        return true
    }

    private fun onScanFailed() {
        Timber.e("Failed scan")
        onScanStop()
    }

    private fun onScanStart() {
        startScan()

        if(preferences.isKeepScreenOn()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun startScan() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.let {
                it.isChecked = true
                it.setTitle(getString(R.string.stop_scan))
            }
        }
        Timber.e("Observer Removed")
        scanResultViewModel.getScanResultsLiveData().removeObserver(scanResultObserver)
        Timber.e("Observer Added")
        scanResultViewModel.getScanResultsLiveData().observe(this, scanResultObserver)
    }


    private fun onScanStop() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.let {
                it.isChecked = false
                it.setTitle(getString(R.string.start_scan))
            }
        }
        Timber.e("Observer Removed")
        scanResultViewModel.getScanResultsLiveData().removeObservers(this)

        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        mainMenu = menu
        mainMenu?.clear()
        menuInflater.inflate(R.menu.scanner_menu, menu)

        val searchMenuItem = mainMenu?.findItem(R.id.search)
        searchMenuItem?.isVisible = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        searchMenuItem?.icon?.colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY)

        val searchView = searchMenuItem?.actionView as ViewGroup
        initSearchView(searchView)

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

    private fun initSearchView(searchView: ViewGroup) {
        val advertisementFilterByMacLayout = searchView.findViewById<TextInputLayout>(R.id.advertisement_filter_by_mac_layout)
        val advertisementFilterByMac = advertisementFilterByMacLayout.editText as HexEditText
        advertisementFilterByMac.setFormatter(HexFormatters.getFormatter(HexFormatters.FormatterType.MAC_ADDRESS_HEX_FORMATTER))

        val advertisementFilterByNameLayout = searchView.findViewById<TextInputLayout>(R.id.advertisement_filter_by_name_layout)
        val advertisementFilterByName = advertisementFilterByNameLayout.editText as EditText

        val onFilterChanged = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filter = s?.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        advertisementFilterByMac.addTextChangedListener(onFilterChanged)
        advertisementFilterByName.addTextChangedListener(onFilterChanged)

        val advertisementFilterType = searchView.findViewById<Spinner>(R.id.advertisement_filter_type)
        advertisementFilterType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            AdvertisementFilterType.values().map { resources.getString(it.stringResourceId) }
        )
        advertisementFilterType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                advertisementFilterType.setSelection(0)
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val filterType = AdvertisementFilterType.values()[position]
                if(filterType == AdvertisementFilterType.MAC) {
                    advertisementFilterByMacLayout.visibility = View.VISIBLE
                    advertisementFilterByNameLayout.visibility = View.GONE

                    filter = advertisementFilterByMac.text.toString()
                } else {
                    advertisementFilterByMacLayout.visibility = View.GONE
                    advertisementFilterByNameLayout.visibility = View.VISIBLE

                    filter = advertisementFilterByName.text.toString()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id: Int? = item?.itemId
        when (id) {
            R.id.action_toggle_scan -> toggleScan(item)
            R.id.action_start_deserializer_list_activity -> startDeserializerListActivity()
            R.id.action_clear -> {
                scanAnalyzerAdapter.clear()
            }
            R.id.action_start_settings_activity -> startActivity(Intent(this,
                SettingsActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }


    private fun startDeserializerListActivity() {
        startActivityForResult(Intent(this, DeserializerListActivity::class.java), 0x00)
    }

    private fun toggleScan(item: MenuItem?) {
        item?.let {
            if (item.isChecked) {
                shouldBeScanning = false
                bluetoothScanningViewModel.stopScanning()
            } else {
                shouldBeScanning = true
                permissionViewModel.requestAccessFineLocation()
            }
        }
    }

    private fun setScanMenuLabel(menuItem: MenuItem) {
        if (BluetoothScanner.isRunning()) {
            menuItem.title = getString(R.string.stop_scan)
            menuItem.isChecked = true
        } else {
            menuItem.title = getString(R.string.start_scan)
            menuItem.isChecked = false
        }
    }

    private fun getAllDeserializers(onDeserializersLoaded : (() -> Unit)? = null) {
        disposable = getAllDeserializersUseCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { deserializers ->
                scanAnalyzerAdapter.updateDeserializers(deserializers.toMutableList())
                if (onDeserializersLoaded != null) {
                    onDeserializersLoaded()
                }
            }
    }

    private fun loadLogs(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            scanAnalyzerAdapter.loadScanLog(scanLogSavedState ?: mutableListOf())
            scanLogSavedState = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        scanLogSavedState = scanAnalyzerAdapter.scanLog
        outState.putBoolean(SHOULD_BE_SCANNING_KEY,shouldBeScanning)
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

    override fun onStop() {
        super.onStop()
        bluetoothScanningViewModel.stopScanning()
    }

    override fun onStart() {
        super.onStart()
        if(shouldBeScanning) {
            bluetoothScanningViewModel.startScanning()
        }
    }

    companion object {
        const val EXTRA_FILTER_MAC: String = "com.acconno.blesniffer.FILTER_MAC"
        const val EXTRA_SAMPLE_DATA: String = "com.acconno.blesniffer.SAMPLE_DATA"
        const val SHOULD_BE_SCANNING_KEY : String = "SHOULD_BE_SCANNING_KEY"

        var scanLogSavedState : MutableList<ScanAnalyzerAdapter.Item>? = null
    }

    enum class AdvertisementFilterType(val stringResourceId : Int) {
        MAC(R.string.mac_address), DEVICE_NAME(R.string.device_name)
    }
}