package com.aconno.blesniffer.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aconno.blesniffer.R
import com.aconno.blesniffer.domain.byteformatter.ByteArrayFormatter
import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.FieldDeserializer
import com.aconno.blesniffer.domain.model.Device
import com.aconno.blesniffer.domain.model.ScanResult
import com.aconno.blesniffer.domain.util.ByteOperations
import kotlinx.android.synthetic.main.item_scan_record.view.*
import timber.log.Timber
import java.lang.IndexOutOfBoundsException
import java.text.SimpleDateFormat
import java.util.*

//TODO (This needs a refactor, this adapter is doing all the business logic)
fun ByteArray.toHex() = this.joinToString(separator = "") {
    "0x" + it.toInt().and(0xff).toString(16).padStart(
            2,
            '0'
    ).toUpperCase() + " "
}

fun ByteArray.inversedCopyOfRangeInclusive(start: Int, end: Int) =
        this.reversedArray().copyOfRange((size - 1) - start, (size - 1) - end + 1)

class ScanAnalyzerAdapter(
        private val scanRecordListener: ScanRecordListener,
        private val longItemClickListener: LongItemClickListener<ScanResult>,
        advertisementDataFormatter : ByteArrayFormatter

) : RecyclerView.Adapter<ScanAnalyzerAdapter.ViewHolder>() {
    val scanLog: MutableList<Item> = mutableListOf()
    private val hashes: MutableMap<Int, Int> = mutableMapOf()

    var advertisementDataFormatter : ByteArrayFormatter = advertisementDataFormatter
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    data class Item(
        val scanResult: ScanResult,
        var occurrences: Int
    )


    var deserializers: MutableList<Deserializer> = mutableListOf()

    var hideMissingSerializer : Boolean = false
        set(value) {
            if(field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    init {
        setHasStableIds(true)
    }

    fun updateDeserializers(items: List<Deserializer>) {
        deserializers.clear()
        deserializers.addAll(items)
    }

    fun logScan(data: ScanResult) {
        hashes[data.hashCode()]?.let { entryIndex ->
            scanLog.getOrNull(entryIndex)?.takeIf {
                (data.timestamp) - (it.scanResult.timestamp) < 2500
            }?.let { item ->
                item.occurrences++
                item.scanResult.timestamp = data.timestamp
                item.scanResult.rssi = data.rssi
                notifyItemChanged(entryIndex, null)
            }
        } ?: run {
            val size = scanLog.size
            hashes[data.hashCode()] = size
            scanLog.add(Item(
                data, 1
            ))
            notifyItemInserted(size)
            scanRecordListener.onRecordAdded(size)
        }
    }

    fun loadScanLog(scanLog : List<MutablePair<ScanResult,Int>>) {
        this.scanLog.clear()
        this.scanLog.addAll(scanLog.map { Item(it.first,it.second) })

        hashes.clear()
        scanLog.forEachIndexed { index, mutablePair ->
            val scanResult = mutablePair.first
            hashes[scanResult.hashCode()] = index
        }

        notifyDataSetChanged()
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

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private var initialized: Boolean = false

        fun bind(scanLog: Item) {
            val device = scanLog.scanResult.device
            val advertisementData = scanLog.scanResult.advertisement.rawData
            val dataHex = advertisementDataFormatter.formatBytes(advertisementData)
            val scanResult = scanLog.scanResult

            view.time.text = formatTimestamp(
                scanLog.scanResult.timestamp,
                longItemClickListener as Context
            )
            view.rssi.text = view.context.getString(
                R.string.rssi_strength,
                scanLog.scanResult.rssi
            )
            view.repeating.text = view.context.getString(
                R.string.repeating_amount, scanLog.occurrences
            )

            if (!initialized) {
                view.address.text = device.macAddress
                view.name.text = device.name
                view.data.text = dataHex
                view.setOnLongClickListener { longItemClickListener.onLongItemClick(scanResult) }

                findDeserializer(device, dataHex)?.let { deserializer ->
                    view.deserializer?.visibility = View.VISIBLE
                    view.deserializer_name.visibility = View.VISIBLE
                    view.deserializer_name.text = deserializer.name

                    view.deserialized_field_list.layoutManager = LinearLayoutManager(
                        view.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )

                    view.deserialized_field_list.adapter = DeserializedFieldsAdapter().also {
                        it.setFields(deserializer.fieldDeserializers.mapNotNull { fieldDeserializer ->
                            getField(fieldDeserializer, advertisementData)
                        })
                    }
                } ?: run {
                    if (hideMissingSerializer) {
                        view.deserializer_name.visibility = View.GONE
                        view.deserializer?.visibility = View.GONE
                    } else {
                        view.deserializer_name.visibility = View.VISIBLE
                        view.deserializer?.visibility = View.VISIBLE
                    }
                }
                initialized = true
            }
        }

        private fun findDeserializer(device: Device, dataHex: String): Deserializer? {
            return deserializers.find {
                when (it.filterType) {
                    Deserializer.Type.MAC -> device.macAddress.matches(it.pattern)
                    Deserializer.Type.DATA -> dataHex.matches(it.pattern) or dataHex.contains(it.pattern)
                    else -> false
                }
            }
        }

        private fun getField(
                fieldDeserializer: FieldDeserializer,
                advertisementData: ByteArray
        ): Triple<String, String, Int>? {

            val deserializedData =
                    deserializeAdvertisementData(fieldDeserializer, advertisementData)

            return if (deserializedData != null)
                Triple(
                        fieldDeserializer.name,
                        deserializedData,
                        fieldDeserializer.color
                )
            else null
        }

        private fun deserializeAdvertisementData(
                fieldDeserializer: FieldDeserializer,
                advertisementData: ByteArray
        ): String? {
            val validData = ByteOperations.isolateMsd(advertisementData)
            val start = fieldDeserializer.startIndexInclusive
            val end = fieldDeserializer.endIndexExclusive
            val size = validData.size

            return if (start > size || end > size) {
                Timber.e("${fieldDeserializer.name}: Error parsing data, Bad indexes")
                null
            } else {
                try {
                    val dataRange = getDataRange(start, end, validData)
                    fieldDeserializer.deserialize(dataRange)
                } catch (e: IllegalArgumentException) {
                    Timber.e("${fieldDeserializer.name}: ${e.message ?: "Error parsing data"}")
                    null
                } catch (e: IndexOutOfBoundsException) {
                    Timber.e("${fieldDeserializer.name}: ${e.message ?: "Error parsing data"}")
                    null
                }
            }
        }

        private fun getDataRange(start: Int, end: Int, validData: ByteArray): ByteArray {
            return if (start <= end) validData.copyOfRange(start, end)
            else validData.inversedCopyOfRangeInclusive(start - 1, end)
        }
    }
}

var sdf: SimpleDateFormat? = null

@Suppress("DEPRECATION")
fun getCurrentLocale(context: Context): Locale =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) context.resources.configuration.locales.get(
                0
        )
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