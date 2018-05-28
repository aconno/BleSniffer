package com.aconno.acnsensa.ui

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.adapter.BeaconScanningAdapter
import com.aconno.acnsensa.adapter.ItemClickListener
import com.aconno.acnsensa.domain.beacon.Beacon
import com.aconno.acnsensa.viewmodel.BeaconListViewModel
import kotlinx.android.synthetic.main.fragment_beacon_scanning_list.*
import timber.log.Timber
import javax.inject.Inject

class BeaconListFragment : Fragment(), ItemClickListener<Beacon> {

    private lateinit var beaconScanningAdapter: BeaconScanningAdapter

    @Inject
    lateinit var beaconListViewModel: BeaconListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainActivity: MainActivity? = activity as MainActivity
        mainActivity?.mainActivityComponent?.inject(this)
    }

    override fun onResume() {
        super.onResume()
        beaconListViewModel.getBeaconLiveData()
            .observe(this, Observer { displayBeacon(it!!) })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beacon_scanning_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beaconScanningAdapter = BeaconScanningAdapter(mutableListOf(), this)
        beacon_list.adapter = beaconScanningAdapter
    }

    override fun onItemClick(item: Beacon) {
        Timber.e(item.address)
    }

    private fun displayBeacon(values: Beacon) {
        beaconScanningAdapter.updateBeacon(values)
    }
}