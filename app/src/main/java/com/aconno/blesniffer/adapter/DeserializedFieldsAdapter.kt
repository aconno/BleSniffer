package com.aconno.blesniffer.adapter

import android.content.res.ColorStateList
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.blesniffer.R
import kotlinx.android.synthetic.main.item_deserialized_field.view.*
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat


/**
 * @author aconno
 */
class DeserializedFieldsAdapter(
    val fields: MutableList<Triple<String, String, Int>> = mutableListOf()
) : RecyclerView.Adapter<DeserializedFieldsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_deserialized_field, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fields.size
    }

    fun setFields(fields: List<Triple<String, String, Int>>) {
        this.fields.clear()
        this.fields.addAll(fields)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fields[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(triple: Triple<String, String, Int>) {
            view.til_name.text = triple.first
            view.value.text = triple.second
            val textColor = getTextColor(triple.third)

            view.til_name.setTextColor(textColor)
            view.value.setTextColor(textColor)

            view.backgroundTintList = ColorStateList.valueOf(triple.third)
        }

        private fun isColorDark(color: Int): Boolean {
            val darkness =
                1 - (RED_MULTIPLIER * Color.red(color) +
                        GREEN_MULTIPLIER * Color.green(color) +
                        BLUE_MULTIPLIER * Color.blue(color)) / 255
            return darkness < DARKNESS_INDEX
        }

        @ColorInt
        private fun getTextColor(backgroundColor: Int): Int {
            val textColor =
                if (isColorDark(backgroundColor)) R.color.primaryTextColor
                else R.color.secondaryTextColor

            return ContextCompat.getColor(view.context, textColor)
        }
    }

    companion object {
        private const val RED_MULTIPLIER = 0.299
        private const val GREEN_MULTIPLIER = 0.587
        private const val BLUE_MULTIPLIER = 0.114
        private const val DARKNESS_INDEX = 0.5

    }
}
