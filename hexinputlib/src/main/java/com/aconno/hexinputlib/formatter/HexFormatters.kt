package com.aconno.hexinputlib.formatter

object HexFormatters {

    fun getFormatter(formatterType: FormatterType) : HexFormatter {
        return when(formatterType) {
            FormatterType.PLAIN_HEX_FORMATTER -> PlainByteHexFormatter()
            FormatterType.SINGLE_BYTE_HEX_FORMATTER -> SingleByteHexFormatter()
            FormatterType.BYTE_PAIRS_HEX_FORMATTER -> BytePairsHexFormatter()
            FormatterType.PREFIXED_BYTE_HEX_FORMATTER -> PrefixedByteHexFormatter()
            FormatterType.MAC_ADDRESS_HEX_FORMATTER -> MacAddressHexFormatter()
        }
    }

    fun parse(formattedContent : String) : List<Char> {
        FormatterType.values().forEach {
            val formatter = getFormatter(it)

            val parsedValues = try {
                    formatter.parse(formattedContent)
                } catch (ex : IncompatibleFormatException) {
                    null
                }

            if(parsedValues != null) {
                return parsedValues
            }
        }

        throw IncompatibleFormatException()
    }

    fun getDefaultFormatter(): HexFormatter {
        return SingleByteHexFormatter()
    }

    enum class FormatterType {
        PREFIXED_BYTE_HEX_FORMATTER,SINGLE_BYTE_HEX_FORMATTER,BYTE_PAIRS_HEX_FORMATTER,MAC_ADDRESS_HEX_FORMATTER,PLAIN_HEX_FORMATTER
    }
}