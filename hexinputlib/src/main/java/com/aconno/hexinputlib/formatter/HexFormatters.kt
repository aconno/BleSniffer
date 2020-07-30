package com.aconno.hexinputlib.formatter

object HexFormatters {

    fun getFormatter(formatterType: FormatterType) : HexFormatter {
        TODO()
    }

    fun parse(formattedContent : String) : List<Char> {
        TODO()
    }

    fun getDefaultFormatter(): HexFormatter {
        return SingleByteHexFormatter()
    }

    enum class FormatterType {
        PREFIXED_BYTE_HEX_FORMATTER,SINGLE_BYTE_HEX_FORMATTER,BYTE_PAIRS_HEX_FORMATTER,MAC_ADDRESS_HEX_FORMATTER
    }
}