package com.aconno.blesniffer.adapter

interface ItemClickListener<in T> {
    fun onItemClick(item: T)
}