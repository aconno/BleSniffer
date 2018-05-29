package com.aconno.acnsensa.adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.beacon.Beacon
import com.aconno.acnsensa.domain.deserializing.Deserializer
import com.aconno.acnsensa.domain.deserializing.FieldDeserializer
import kotlinx.android.synthetic.main.item_scan_record.view.*
import timber.log.Timber
import java.text.DateFormat
import java.util.*


fun ByteArray.toHex() = this.joinToString(separator = "") { "0x" + it.toInt().and(0xff).toString(16).padStart(2, '0') + " " }
class ScanAnalyzerAdapter(
        private val scanLog: MutableList<MutablePair<Beacon, Int>>,
        private val scanRecordListener: ScanRecordListener,
        private val deserializers: List<Deserializer>
) : RecyclerView.Adapter<ScanAnalyzerAdapter.ViewHolder>() {

    var filter: String = ""
        set(value) {
            field = value
            if (value.isNotEmpty()) {
                filteredList = scanLog.filter { beacon -> beacon.first.address.contains(filter, ignoreCase = true) }.toMutableList()
                notifyDataSetChanged()
            }
        }

    private var filteredList: MutableList<MutablePair<Beacon, Int>> = mutableListOf()

    fun setBeaconData(beaconData: List<Beacon>) {
        this.scanLog.clear()
        this.scanLog.addAll(beaconData.map { MutablePair(it, 1) })
        if (filter.isNotEmpty()) filteredList = scanLog.filter { beacon -> beacon.first.address.contains(filter, ignoreCase = true) }.toMutableList()
        notifyDataSetChanged()
    }

    fun logScan(data: Beacon) {
        Timber.e(data.advertisementData.toHex())

        scanLog.filter { (System.currentTimeMillis() - it.first.lastseen) < 2500 }.forEachIndexed { index, item ->
            //            Timber.e(scanLog.indexOf(item).toString())
            if (item.first.address == data.address) {
                if (item.first.advertisementData.contentEquals(data.advertisementData)) {
                    filteredList.indexOfFirst { it.first.lastseen == item.first.lastseen }.let {
                        if (it == -1) return@let
                        filteredList[it].second++
                        filteredList[it].first.lastseen = data.lastseen
                        notifyItemChanged(it)
                    }
                    item.second++
                    item.first.lastseen = data.lastseen
                    return@logScan
                }
            }
        }
        scanLog.add(0, MutablePair(data, 1))



        if (filter.isEmpty() || data.address.contains(filter)) {
            filteredList.add(0, MutablePair(data, 1))
        }
//        filteredList = scanLog
        notifyItemInserted(0)
        scanRecordListener.onRecordAdded()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_scan_record, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return (if (filter.isNotEmpty()) filteredList else scanLog).size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind((if (filter.isNotEmpty()) filteredList else scanLog)[position])
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {

        }

        fun bind(data: MutablePair<Beacon, Int>) {
            view.time.text = getDateCurrentTimeZone(data.first.lastseen)
            view.repeating.text = "x${data.second}"

            if (view.deserialized_field_list.layoutManager == null) {
                Timber.e("Init")
                view.address.text = data.first.address
                view.name.text = data.first.name
                view.data.text = data.first.advertisementData.toHex()
                deserializers.find { data.first.address.contains(it.filter, ignoreCase = true) }?.let {
                    it.fieldDeserializers.map {
                        val b = 5
                        Triple(
                                it.name,
                                it.type.converter.deserialize(
                                        data.first.advertisementData.copyOfRange(
                                                it.startIndexInclusive, it.endIndexExclusive
                                        )
                                ).toString(),
                                it.color
                        )
                    }.apply {
                        val a = 4
                    }.toMutableList().let {
                        view.deserialized_field_list.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
                        view.deserialized_field_list.adapter = DeserializedFieldsAdapter(it)
                    }
                }
            } else {
                Timber.e("null")
            }
        }
    }
}

fun getDateCurrentTimeZone(timestamp: Long): String {
    try {
        val calendar = Calendar.getInstance()
        val tz = TimeZone.getDefault()
        calendar.timeInMillis = timestamp
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
        val sdf = DateFormat.getDateTimeInstance()
        val currentTimeZone = calendar.time as Date
        return sdf.format(currentTimeZone)
    } catch (e: Exception) {
    }

    return ""
}

class MutablePair<A, B>(
        var first: A,
        var second: B
)