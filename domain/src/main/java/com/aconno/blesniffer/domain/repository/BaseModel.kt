package com.aconno.blesniffer.domain.repository

interface BaseModel<T> {
    fun getKey(): T
}