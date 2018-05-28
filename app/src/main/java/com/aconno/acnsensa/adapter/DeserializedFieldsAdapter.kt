package com.aconno.acnsensa.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import kotlinx.android.synthetic.main.item_deserialized_field.view.*

/**
 * @author aconno
 */
class DeserializedFieldsAdapter(
    private val fields: MutableList<Pair<String, String>>
) : RecyclerView.Adapter<DeserializedFieldsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_deserialized_field, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fields.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fields[position])
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(pair: Pair<String, String>) {
            view.name.text = pair.first
            view.value.text = pair.second
        }
    }
}
