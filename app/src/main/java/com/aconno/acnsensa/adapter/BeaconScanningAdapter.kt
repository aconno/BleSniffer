package com.aconno.acnsensa.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.beacon.Beacon
import kotlinx.android.synthetic.main.item_scanning_beacon.view.*

class BeaconScanningAdapter(
        private val beaconData: MutableList<Beacon>,
        private val clickListener: ItemClickListener<Beacon>
) : RecyclerView.Adapter<BeaconScanningAdapter.ViewHolder>() {
    fun setBeaconData(beaconData: List<Beacon>) {
        this.beaconData.clear()
        this.beaconData.addAll(beaconData)
        notifyDataSetChanged()
    }

    fun updateBeacon(data: Beacon) {
        val storedData: Beacon? =
            beaconData.find { beaconData -> beaconData.address == data.address }
        if (storedData == null) {
            this.beaconData.add(data)
            notifyItemInserted(this.beaconData.size - 1)
        } else {
            storedData.apply {
                // TODO: Updating stuff
            }
            notifyItemChanged(beaconData.indexOf(storedData))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_scanning_beacon, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return beaconData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(beaconData[position])
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(beaconData: Beacon) {
            view.beacon_address.text = beaconData.address
            view.beacon_time.text = System.currentTimeMillis().toString()
            view.setOnClickListener { clickListener.onItemClick(beaconData) }
        }
    }
}