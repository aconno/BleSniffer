package com.aconno.blesniffer.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.aconno.blesniffer.R
import com.aconno.blesniffer.domain.ValueConverter
import com.aconno.blesniffer.domain.deserializing.FieldDeserializer
import com.aconno.blesniffer.domain.deserializing.GeneralFieldDeserializer
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import kotlinx.android.synthetic.main.item_deserializer_field.view.*

fun String.strip() = this.replace("\\s", "")

class DeserializerEditorAdapter(
    private val activity: AppCompatActivity
) : RecyclerView.Adapter<DeserializerEditorAdapter.ViewHolder>() {
    var fieldDeserializers: MutableList<FieldDeserializer> = mutableListOf()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    // TODO: Lower amount of null assertions

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_deserializer_field, parent, false
        )
        return ViewHolder(view)
    }

    fun addEmptyValueDeserializer() {
        fieldDeserializers.add(GeneralFieldDeserializer().apply {
            fieldDeserializers.maxBy {
                it.endIndexExclusive
            }?.let {
                this.startIndexInclusive = it.endIndexExclusive
                this.endIndexExclusive = this.startIndexInclusive + this.type.converter.length
            }
        })
        notifyItemInserted(fieldDeserializers.size - 1)
    }

    override fun getItemCount(): Int {
        return fieldDeserializers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(fieldDeserializers[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private fun showColorPickerDialog() {
            ColorPickerDialog
                .newBuilder()
                .setPresets(view.context.resources.getIntArray(R.array.picker_colors))
                .setColor(view.context.resources.getColor(android.R.color.holo_red_dark))
                .setAllowCustom(false)
                .setAllowPresets(true)
                .create().apply {
                    setColorPickerDialogListener(object : ColorPickerDialogListener {
                        override fun onDialogDismissed(dialogId: Int) {

                        }

                        override fun onColorSelected(dialogId: Int, color: Int) {
                            fieldDeserializers[adapterPosition].color = color
                            this@ViewHolder.view.color_button?.setBackgroundColor(color)
                        }
                    })
                }.show(activity.supportFragmentManager, "color")
        }

        private val converterTypeAdapter: ArrayAdapter<String> by lazy {
            ArrayAdapter(
                view.context,
                android.R.layout.simple_spinner_dropdown_item,
                ValueConverter.values().map { it.name }
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }

        init {
            registerTextWatchers()

            view.spinner_converter_type.adapter = converterTypeAdapter
            view.spinner_converter_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, id: Long) {
                    ValueConverter.values()[position].let { valueConverter ->
                        fieldDeserializers[adapterPosition].type = valueConverter

                        if (valueConverter.converter.length == -1) {
                            view.til_end.isEnabled = true
                        } else {
                            view.til_end.isEnabled = false


                            val value = Integer.parseInt(
                                view.til_start.editText?.text?.toString()?.takeIf {
                                    it.isNotEmpty()
                                } ?: "0"
                            )

                            (value + valueConverter.converter.length).let { endIndexExclusive ->
                                view.til_end.editText?.setText(endIndexExclusive.toString())
                                fieldDeserializers[adapterPosition].endIndexExclusive = endIndexExclusive
                            }
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
            view.btn_remove.setOnClickListener {
                fieldDeserializers.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
            view.color_button.setOnClickListener {
                showColorPickerDialog()
            }
        }

        private fun registerTextWatchers() {
            view.til_name.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    fieldDeserializers[adapterPosition].name = s.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            view.til_start.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    view.til_start.error = if (s.toString().isBlank()) {
                        "Defaulting to 0"
                    } else null

                    val value = Integer.parseInt(s.toString().takeIf { it.isNotEmpty() } ?: "0")
                    fieldDeserializers[adapterPosition].startIndexInclusive = value

                    if (!view.til_end.isEnabled) {
                        val endValue = (value + fieldDeserializers[adapterPosition].type.converter.length)
                        view.til_end.editText?.setText(endValue.toString())
                        fieldDeserializers[adapterPosition].endIndexExclusive = endValue
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            view.til_end.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!view.til_end.isEnabled) return
                    view.til_end.error = if (s.toString().isBlank()) {
                        "Defaulting to 0"
                    } else null

                    val value = Integer.parseInt(s.toString().takeIf { it.isNotEmpty() } ?: "0")
                    fieldDeserializers[adapterPosition].endIndexExclusive = value
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        fun bind(fieldDeserializer: FieldDeserializer) {
            view.til_name.editText?.setText(fieldDeserializer.name)
            view.color_button.setBackgroundColor(fieldDeserializer.color)
            view.spinner_converter_type.setSelection(converterTypeAdapter.getPosition(fieldDeserializer.type.name))
            view.til_start.editText?.setText(fieldDeserializer.startIndexInclusive.toString())
            view.til_end.isEnabled = fieldDeserializer.type.converter.length == -1
            view.til_end.editText?.setText(fieldDeserializer.endIndexExclusive.toString())
        }
    }

    fun createItemTouchHelper(): ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val oldPos = viewHolder.adapterPosition
                val newPos = target.adapterPosition
                val item = fieldDeserializers[oldPos]
                fieldDeserializers.removeAt(oldPos)
                fieldDeserializers.add(newPos, item)
                notifyItemMoved(oldPos, newPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val index = viewHolder.adapterPosition
                fieldDeserializers.removeAt(index)
                notifyItemRemoved(index)
            }
        }

}