package com.aconno.blesniffer.adapter

interface LongItemClickListener<in T> {
    fun onLongItemClick(item: T): Boolean
}