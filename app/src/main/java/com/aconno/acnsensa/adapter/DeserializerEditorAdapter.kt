package com.aconno.acnsensa.adapter

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.aconno.acnsensa.R
import com.aconno.acnsensa.domain.ValueConverter
import com.aconno.acnsensa.domain.deserializing.Deserializer
import kotlinx.android.synthetic.main.item_deserializer_field.view.*

class DeserializerEditorAdapter(private val deserializer: Deserializer)
    : RecyclerView.Adapter<DeserializerEditorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_deserializer_field, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return deserializer.valueDeserializers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(deserializer.valueDeserializers[position])
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.name.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s == null || s.isEmpty()) return
                    deserializer.valueDeserializers.add(
                        adapterPosition,
                        deserializer.valueDeserializers[adapterPosition].copy(
                            first = s.toString()
                        ).apply {
                            deserializer.valueDeserializers.removeAt(adapterPosition)
                        }
                    )
                }
            })
            view.start.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s == null || s.isEmpty()) return
                    deserializer.valueDeserializers.add(
                        adapterPosition,
                        deserializer.valueDeserializers[adapterPosition].copy(
                            second = deserializer.valueDeserializers[adapterPosition].second.copy(
                                first = Integer.parseInt(s.toString())
                            )
                        ).apply {
                            deserializer.valueDeserializers.removeAt(adapterPosition)
                        }
                    )
                }
            })
            view.end.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s == null || s.isEmpty()) return
                    deserializer.valueDeserializers.add(
                        adapterPosition,
                        deserializer.valueDeserializers[adapterPosition].copy(
                            second = deserializer.valueDeserializers[adapterPosition].second.copy(
                                second = Integer.parseInt(s.toString())
                            )
                        ).apply {
                            deserializer.valueDeserializers.removeAt(adapterPosition)
                        }
                    )
                }
            })
            view.converter_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    deserializer.valueDeserializers.add(
                        adapterPosition,
                        deserializer.valueDeserializers[adapterPosition].copy(
                            third = ValueConverter.values()[position]
                        ).apply {
                            deserializer.valueDeserializers.removeAt(adapterPosition)
                        }
                    )
                }

            }
            view.converter_type.adapter = ArrayAdapter<String>(
                view.context,
                android.R.layout.simple_spinner_dropdown_item,
                ValueConverter.values().map { it.name }
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            view.remove.setOnClickListener {
                deserializer.valueDeserializers.removeAt(adapterPosition)
                notifyDataSetChanged()
            }
        }

        fun bind(valueDeserializer: Triple<String, Pair<Int, Int>, ValueConverter>) {
            view.name.editText?.setText(valueDeserializer.first)
            view.start.editText?.setText(valueDeserializer.second.first.toString())
            view.end.editText?.setText(valueDeserializer.second.second.toString())
            view.converter_type.setSelection(
                (view.converter_type.adapter as ArrayAdapter<String>).getPosition(
                    valueDeserializer.third.name
                )
            )
        }
    }
}