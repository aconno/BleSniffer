package com.aconno.hexinputlib.formatter

class CompleteMacAddressHexFormatter : MacAddressHexFormatter() {

    override fun format(values: List<Char>): String {
        val valuesToFormat = values.toMutableList()
        for(i in values.size until MAC_ADDRESS_VALUES_NUMBER) {
            valuesToFormat.add('0')
        }
        return super.format(valuesToFormat)
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        val sourceValueIndex = super.locateSourceValue(values, formattedValueIndex)
        if(sourceValueIndex > values.size) {
            return values.size
        }

        return sourceValueIndex
    }

    override fun areValuesDerivableFrom(values: List<Char>, fromValues: List<Char>): Boolean {
        if(fromValues.size > values.size) {
            return false
        }
        if(values.subList(0,fromValues.size) != fromValues) {
            return false
        }
        if(values.subList(fromValues.size, values.size).any { it != '0' }) {
            return false
        }
        return true
    }

    companion object {
        const val MAC_ADDRESS_VALUES_NUMBER = 12
    }
}