package com.aconno.hexinputlib.formatter

/**
 *  An object containing util methods for working with the set of hex formatters contained in this library.
 */
object HexFormatters {

    /**
     * Returns an instance of [HexFormatter] that formats values in a way specified by [formatterType]
     * parameter.
     *
     * @param formatterType type of formatter that should be returned
     *
     * @return instance of [HexFormatter] that formats values in a way specified by [formatterType] parameter
     *
     */
    fun getFormatter(formatterType: FormatterType) : HexFormatter {
        return when(formatterType) {
            FormatterType.PLAIN_VALUES_HEX_FORMATTER -> PlainValuesHexFormatter()
            FormatterType.PLAIN_BYTES_HEX_FORMATTER -> PlainByteHexFormatter()
            FormatterType.SINGLE_BYTE_HEX_FORMATTER -> SingleByteHexFormatter()
            FormatterType.BYTE_PAIRS_HEX_FORMATTER -> BytePairsHexFormatter()
            FormatterType.PREFIXED_BYTE_HEX_FORMATTER -> PrefixedByteHexFormatter()
            FormatterType.MAC_ADDRESS_HEX_FORMATTER -> MacAddressHexFormatter()
            FormatterType.COMPLETE_MAC_ADDRESS_HEX_FORMATTER -> CompleteMacAddressHexFormatter()
        }
    }

    /**
     * Parses the [formattedContent]. The format of [formattedContent] has to be compatible with
     * one of the formatters contained in the library. This means that this method can not be used
     * to parse content formatted using some custom formatter. To parse content formatted using
     * a custom formatter, use [HexFormatter.parse] method of that formatter.
     *
     * @param formattedContent content formatted using one of the formatters contained in this library
     *
     * @return parsed hex values
     *
     * @throws IncompatibleFormatException if [formattedContent] is not compatible with any of the
     *      formatters contained in the library
     */
    fun parse(formattedContent : String) : List<Char> {
        FormatterType.values().forEach {
            val formatter = getFormatter(it)

            val parsedValues = formatter.parse(formattedContent)
            if(parsedValues != null) {
                return parsedValues
            }
        }

        throw IncompatibleFormatException()
    }

    /**
     * Returns a default [HexFormatter]
     *
     * @return a default [HexFormatter]
     */
    fun getDefaultFormatter(): HexFormatter {
        return SingleByteHexFormatter()
    }

    /**
     * An enum describing type of [HexFormatter]
     */
    enum class FormatterType {
        PREFIXED_BYTE_HEX_FORMATTER,
        SINGLE_BYTE_HEX_FORMATTER,
        BYTE_PAIRS_HEX_FORMATTER,
        MAC_ADDRESS_HEX_FORMATTER,
        PLAIN_VALUES_HEX_FORMATTER,
        PLAIN_BYTES_HEX_FORMATTER,
        COMPLETE_MAC_ADDRESS_HEX_FORMATTER
    }
}