package com.aconno.hexinputlib.formatter

/**
 * Signals that the format of string being parsed is not compatible with the expected format.
 */
class IncompatibleFormatException(message : String) : Exception(message) {
    constructor() : this("")
}