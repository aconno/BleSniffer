package com.aconno.acnsensa.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.deserializing.Deserializer
import kotlinx.android.synthetic.main.item_deserializer.view.*

/**
 * @author aconno
 */
class DeserializerAdapter(
        val deserializers: MutableList<Deserializer>,
        private val clickListener: ItemClickListener<Deserializer>,
        private val longClickListener: LongItemClickListener<Deserializer>
) : RecyclerView.Adapter<DeserializerAdapter.ViewHolder>() {


    fun setDeserializers(deserializers: List<Deserializer>) {
        this.deserializers.clear()
        this.deserializers.addAll(deserializers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_deserializer, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return deserializers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(deserializers[position])
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(deserializer: Deserializer) {
            view.deserializer_name.text = deserializer.name
            view.deserializer_filter_and_type.text = view.context.getString(R.string.deserializer_filter_and_type, deserializer.filterType.name, deserializer.filter)
            view.setOnClickListener { clickListener.onItemClick(deserializer) }
            view.setOnLongClickListener { longClickListener.onLongItemClick(deserializer) }
        }
    }
}
