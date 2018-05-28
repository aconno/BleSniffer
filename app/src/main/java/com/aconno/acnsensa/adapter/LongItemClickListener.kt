package com.aconno.acnsensa.adapter

interface LongItemClickListener<in T> {
    fun onLongItemClick(item: T): Boolean
}