package com.aconno.acnsensa.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
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
import com.aconno.acnsensa.domain.deserializing.FieldDeserializer
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.item_deserializer_field.view.*

class DeserializerEditorAdapter(
        private val deserializer: Deserializer,
        private val activity: Activity
) : RecyclerView.Adapter<DeserializerEditorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_deserializer_field, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return deserializer.fieldDeserializers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(deserializer.fieldDeserializers[position])
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), ColorPickerDialogListener {

        init {
            view.name.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s == null || s.isEmpty()) return
                    deserializer.fieldDeserializers[adapterPosition].name = s.toString()
                }
            })
            view.start.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s == null || s.isEmpty()) return
                    deserializer.fieldDeserializers[adapterPosition]
                            .startIndexInclusive = Integer.parseInt(s.toString())
                }
            })
            view.end.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s == null || s.isEmpty()) return
                    deserializer.fieldDeserializers[adapterPosition]
                            .endIndexExclusive = Integer.parseInt(s.toString())
                }
            })
            view.converter_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    deserializer.fieldDeserializers[adapterPosition]
                            .type = ValueConverter.values()[position]
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
                deserializer.fieldDeserializers.removeAt(adapterPosition)
                notifyDataSetChanged()
            }
            view.color_button.setOnClickListener {
                ColorPickerDialog
                        .newBuilder()
                        .setPresets(view.context.resources.getIntArray(R.array.picker_colors))
                        .setColor(view.context.resources.getColor(android.R.color.holo_red_dark))
                        .setAllowCustom(false)
                        .setAllowPresets(true)
                        .create().apply {
                            setColorPickerDialogListener(this@ViewHolder)
                        }.show(activity.fragmentManager, "color")
            }
        }

        override fun onDialogDismissed(dialogId: Int) {
        }

        override fun onColorSelected(dialogId: Int, color: Int) {
            deserializer.fieldDeserializers[adapterPosition].color = color
            view.color_button.setBackgroundColor(color)
        }

        fun bind(fieldDeserializer: FieldDeserializer) {
            view.name.editText?.setText(fieldDeserializer.name)
            deserializer.fieldDeserializers[adapterPosition]
            view.start.editText?.setText(fieldDeserializer.startIndexInclusive.toString())
            view.end.editText?.setText(fieldDeserializer.endIndexExclusive.toString())
            view.converter_type.setSelection(
                    (view.converter_type.adapter as ArrayAdapter<String>).getPosition(
                            fieldDeserializer.type.name
                    )
            )
            view.color_button.setBackgroundColor(fieldDeserializer.color)
        }
    }

    fun createItemTouchHelper(): ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    val oldPos = viewHolder.adapterPosition
                    val newPos = target.adapterPosition
                    val item = deserializer.fieldDeserializers[oldPos]
                    deserializer.fieldDeserializers.removeAt(oldPos)
                    deserializer.fieldDeserializers.add(newPos, item)
                    notifyItemMoved(oldPos, newPos)
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    deserializer.fieldDeserializers.removeAt(viewHolder.adapterPosition)
                }
            }
}