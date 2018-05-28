package com.aconno.acnsensa.adapter

import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.view.View


abstract class SortedAdapter<T : String>(
        private val comparator: Comparator<T>
) : RecyclerView.Adapter<SortedAdapter.ViewHolder<T>>() {
    private val list: SortedList<T> = SortedList(/*String::class.java*/null, object : SortedList.Callback<T>() {

        override fun onInserted(position: Int, count: Int) {
            this@SortedAdapter.notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            this@SortedAdapter.notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            this@SortedAdapter.notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int) {
            this@SortedAdapter.notifyItemRangeChanged(position, count)
        }

        override fun compare(a: T, b: T): Int {
            return comparator.compare(a, b)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem.equals(newItem)
        }

        override fun areItemsTheSame(item1: T, item2: T): Boolean {
            return false
        }
    })

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size()

    sealed class ViewHolder<in T>(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T)
    }
}