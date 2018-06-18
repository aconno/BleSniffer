package com.aconno.blesniffer.adapter

import android.content.Context
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.blesniffer.R
import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.model.ScanResult
import kotlinx.android.synthetic.main.item_scan_record.view.*
import java.text.SimpleDateFormat
import java.util.*


fun ByteArray.toHex() = this.joinToString(separator = "") { "0x" + it.toInt().and(0xff).toString(16).padStart(2, '0').toUpperCase() + " " }
fun ByteArray.inversedCopyOfRangeInclusive(start: Int, end: Int) = this.reversedArray().copyOfRange((size - 1) - start, (size - 1) - end + 1)
class ScanAnalyzerAdapter(
        private val scanRecordListener: ScanRecordListener,
        private val longItemClickListener: LongItemClickListener<ScanResult>
) : RecyclerView.Adapter<ScanAnalyzerAdapter.ViewHolder>() {
    val scanLog: MutableList<MutablePair<ScanResult, Int>> = mutableListOf()
    private val hashes: MutableMap<Int, Pair<Int, MutablePair<ScanResult, Int>>> = mutableMapOf()
    var deserializers: MutableList<Deserializer> = mutableListOf()

    init {
        setHasStableIds(true)
    }

    var filter: String = ""
        set(value) {
            field = value
            if (value.isNotEmpty()) {
                notifyDataSetChanged()
            }
        }


    fun updateDeserializers(items: List<Deserializer>) {
        deserializers.clear()
        deserializers.addAll(items)
    }

    fun setBeaconData(beaconData: List<ScanResult>) {
        this.scanLog.clear()
        this.hashes.clear()
        this.hashes.putAll(beaconData.mapIndexed { i, it -> Pair(i, Pair(it.hashCode(), MutablePair(it, 1))) })
        this.scanLog.addAll(beaconData.map { MutablePair(it, 1) })
        notifyDataSetChanged()
    }

    fun logScan(data: ScanResult) {
        val hashEntry = hashes[data.hashCode()]
        if (hashEntry != null) {
            val (index, beaconPair) = hashEntry
            if ((data.timestamp) - (beaconPair.first.timestamp) < 2500) {
                beaconPair.second++
                beaconPair.first.timestamp = data.timestamp
                beaconPair.first.rssi = data.rssi
                notifyItemChanged(index, null)
                return
            }
        }
        val pair = MutablePair(data, 1)
        val size = scanLog.size
        hashes[data.hashCode()] = Pair(size, pair)
        scanLog.add(pair)
        notifyItemInserted(size)
        scanRecordListener.onRecordAdded(size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_scan_record, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return scanLog.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scanLog[position])
    }

    override fun getItemId(position: Int): Long {
        return idCounterStart + position.toLong()
    }

    override fun getItemViewType(position: Int): Int = idCounterStart + position

    private var idCounterStart: Int = 0

    fun clear() {
        idCounterStart += scanLog.size
        hashes.clear()
        scanLog.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private var initialized = false

        fun bind(data: MutablePair<ScanResult, Int>) {
            view.time.text = formatTimestamp(data.first.timestamp, longItemClickListener as Context)
            view.rssi.text = view.context.getString(R.string.rssi_strength, data.first.rssi)
            view.repeating.text = view.context.getString(R.string.repeating_amount, data.second)

            if (!initialized) {
                val device = data.first.device
                val advertisementData = data.first.advertisement.rawData
                val dataHex = advertisementData.toHex()

                view.setOnLongClickListener { longItemClickListener.onLongItemClick(data.first) }
                view.address.text = device.macAddress
                view.name.text = device.name
                view.data.text = dataHex

                with(data.first) {
                    deserializers.find {
                        when (it.filterType) {
                            Deserializer.Type.MAC -> device.macAddress.matches(it.pattern)
                            Deserializer.Type.DATA -> dataHex.matches(it.pattern)
                            else -> false
                        }
                    }?.let {
                        view.deserializer_name.text = it.name
                        it.fieldDeserializers.map { d ->
                            val start = d.startIndexInclusive
                            val end = d.endIndexExclusive
                            val size = advertisement.rawData.size
                            Triple(
                                    d.name,
                                    if (start > size || end > size) view.context.getString(R.string.bad_indexes)
                                    else try {
                                        d.type.converter.deserialize(
                                                if (start <= end) advertisementData.copyOfRange(start, end + 1)
                                                else advertisementData.inversedCopyOfRangeInclusive(start, end)
                                        ).toString()
                                    } catch (e: IllegalArgumentException) {
                                        view.context.getString(R.string.invalid_byte_data)
                                    },
                                    d.color
                            )
                        }.let {
                            val deserializedFieldsAdapter = DeserializedFieldsAdapter()
                            view.deserialized_field_list.adapter = deserializedFieldsAdapter
                            view.deserialized_field_list.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
                            deserializedFieldsAdapter.setFields(it)
                        }
                    }
                }
            }
            initialized = true
        }
    }
}

var sdf: SimpleDateFormat? = null

@Suppress("DEPRECATION")
fun getCurrentLocale(context: Context): Locale =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) context.resources.configuration.locales.get(0)
        else context.resources.configuration.locale

fun formatTimestamp(timestamp: Long, context: Context): String =
        (sdf ?: run {
            sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", getCurrentLocale(context))
            sdf
        })?.format(Date(timestamp)) ?: context.getString(R.string.invalid_timestamp)


class MutablePair<A, B>(
        var first: A,
        var second: B
)