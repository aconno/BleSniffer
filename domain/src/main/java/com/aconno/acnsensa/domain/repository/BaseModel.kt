package com.aconno.acnsensa.domain.repository

interface BaseModel<T> {
    fun getKey(): T
}