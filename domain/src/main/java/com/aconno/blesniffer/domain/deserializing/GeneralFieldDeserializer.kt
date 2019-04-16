package com.aconno.blesniffer.domain.deserializing

import com.aconno.blesniffer.domain.ValueConverter
import com.udojava.evalex.Expression
import timber.log.Timber

data class GeneralFieldDeserializer(
    override var name: String = "",
    override var startIndexInclusive: Int = 0,
    override var endIndexExclusive: Int = 0,
    override var type: ValueConverter = ValueConverter.BOOLEAN,
    override var color: Int = -3407872,
    override var formula: String? = null
) : FieldDeserializer {

    override fun deserialize(data: ByteArray): String {
        val value = type.converter.deserialize(data)
        val stringValue = value.toString()
        return if (value !is Number || formula == null) {
            stringValue
        } else {
            try {
                val expression = Expression(formula)
                expression.with("x", stringValue).eval().toString()
            } catch (e: Expression.ExpressionException) {
                Timber.e("Error calculating expression: ${e.message}")
                stringValue
            }
        }
    }
}