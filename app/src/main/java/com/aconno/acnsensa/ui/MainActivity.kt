package com.aconno.acnsensa.ui

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.R
import com.aconno.acnsensa.dagger.DaggerMainActivityComponent
import com.aconno.acnsensa.dagger.MainActivityComponent
import com.aconno.acnsensa.dagger.MainActivityModule
import com.aconno.acnsensa.domain.model.ScanEvent
import com.aconno.acnsensa.viewmodel.BluetoothScanningViewModel
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var bluetoothScanningViewModel: BluetoothScanningViewModel

    private var mainMenu: Menu? = null

    val mainActivityComponent: MainActivityComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        DaggerMainActivityComponent.builder()
            .appComponent(acnSensaApplication?.appComponent)
            .mainActivityModule(MainActivityModule(this))
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityComponent.inject(this)
        custom_toolbar.title = getString(R.string.app_name)
        setSupportActionBar(custom_toolbar)

        invalidateOptionsMenu()

        if (savedInstanceState == null) {
            addFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        bluetoothScanningViewModel.getResult().observe(this, Observer { handleScanEvent(it) })
    }

    private fun handleScanEvent(scanEvent: ScanEvent?) {
        Timber.d("Handle scan event ${scanEvent?.message}")
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

    private fun onScanFailed() {
        onScanStop()
    }

    private fun onScanStart() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.let {
                it.isChecked = true
                it.setTitle(getString(R.string.stop_scanning))
            }
        }
    }

    private fun onScanStop() {
        mainMenu?.let {
            val menuItem: MenuItem? = it.findItem(R.id.action_toggle_scan)
            menuItem?.let {
                it.isChecked = false
                it.setTitle(getString(R.string.scan))
            }
        }
    }

    private fun addFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(activity_container.id, SensorListFragment())
        transaction.commit()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        mainMenu = menu
        mainMenu?.clear()
        menuInflater.inflate(R.menu.main_menu, menu)

        handleScanEvent(bluetoothScanningViewModel.getResult().value)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id: Int? = item?.itemId
        when (id) {
            R.id.action_toggle_scan -> toggleScan(item)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun toggleScan(item: MenuItem?) {
        item?.let {
            if (item.isChecked) {
                bluetoothScanningViewModel.stopScanning()
            } else {
                bluetoothScanningViewModel.startScanning()
            }
        }
    }
}
