package com.aconno.blesniffer.domain.format

/**
 * @author aconno
 */
data class ByteFormat(
    val startIndexInclusive: Int,
    val endIndexExclusive: Int,
    val isReversed: Boolean,
    val targetType: String
)