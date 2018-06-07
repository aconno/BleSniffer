package com.aconno.acnsensa.adapter

import android.content.Context
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.beacon.Beacon
import com.aconno.acnsensa.domain.deserializing.Deserializer
import kotlinx.android.synthetic.main.item_scan_record.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


fun ByteArray.toHex() = this.joinToString(separator = "") { "0x" + it.toInt().and(0xff).toString(16).padStart(2, '0') + " " }
fun ByteArray.inversedCopyOfRange(start: Int, end: Int) = this.reversedArray().copyOfRange((size - 1) - start, (size - 1) - end)
class ScanAnalyzerAdapter(
        private val scanRecordListener: ScanRecordListener,
        private val longItemClickListener: LongItemClickListener<Beacon>
) : RecyclerView.Adapter<ScanAnalyzerAdapter.ViewHolder>() {
    val scanLog: MutableList<MutablePair<Beacon, Int>> = mutableListOf()
    private val hashes: MutableMap<Int, Pair<Int, MutablePair<Beacon, Int>>> = mutableMapOf()
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

    fun setBeaconData(beaconData: List<Beacon>) {
        this.scanLog.clear()
        this.hashes.clear()
        this.hashes.putAll(beaconData.mapIndexed { i, it -> Pair(i, Pair(it.hashCode(), MutablePair(it, 1))) })
        this.scanLog.addAll(beaconData.map { MutablePair(it, 1) })
        notifyDataSetChanged()
    }

    fun logScan(data: Beacon) {
        val hashEntry = hashes[data.hashCode()]
        if (hashEntry != null) {
            val (index, beaconPair) = hashEntry
            if (data.lastseen - beaconPair.first.lastseen < 2500) {
                beaconPair.second++
                beaconPair.first.lastseen = data.lastseen
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
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int = position

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private var initialized = false

        fun bind(data: MutablePair<Beacon, Int>) {
            view.time.text = formatTimestamp(data.first.lastseen, longItemClickListener as Context)
            view.repeating.text = "x${data.second}"

            if (!initialized) {
                val dataHex = data.first.advertisementData.toHex()

                view.setOnLongClickListener { longItemClickListener.onLongItemClick(data.first) }
                view.address.text = data.first.address
                Timber.e(data.first.name)
                view.name.text = data.first.name
                view.data.text = dataHex

                with(data.first) {
                    deserializers.find {
                        when (it.filterType) {
                            Deserializer.Type.MAC -> address.matches(it.pattern)
                            Deserializer.Type.DATA -> dataHex.matches(it.pattern)
                            else -> false
                        }
                    }?.let {
                        view.deserializer_name.text = it.name
                        it.fieldDeserializers.map { d ->
                            val start = d.startIndexInclusive
                            val end = d.endIndexExclusive
                            val size = advertisementData.size
                            Triple(
                                    d.name,
                                    if (start > size || end > size) "Bad Indexes"
                                    else d.type.converter.deserialize(
                                            if (start <= end) advertisementData.copyOfRange(start, end)
                                            else advertisementData.inversedCopyOfRange(start, end)
                                    ).toString(),
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

fun getCurrentLocale(context: Context): Locale =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) context.resources.configuration.locales.get(0)
        else context.resources.configuration.locale

fun formatTimestamp(timestamp: Long, context: Context): String =
        (sdf ?: run {
            sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", getCurrentLocale(context))
            sdf
        })?.format(Date(timestamp)) ?: "Invalid Timestamp"


class MutablePair<A, B>(
        var first: A,
        var second: B
)