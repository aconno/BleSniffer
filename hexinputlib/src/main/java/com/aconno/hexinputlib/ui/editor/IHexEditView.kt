package com.aconno.hexinputlib.ui.editor

/**
 * An interface that represents an input view specialised for input of hexadecimal content. Contains
 * methods for updating the content and for setting and getting the selection of part of the content.
 */
interface IHexEditView {
    /**
     * Updates this view's content to the content specified by parameter [text].
     *
     * @param text new content that is to be set
     */
    fun updateContent(text : String)

    /**
     * Gets the content of this view.
     *
     * @return this view's content
     */
    fun getContent() : String

    /**
     * Sets selection of the view's content.
     *
     * @param start selection start index
     * @param end selection end index (exclusive)
     */
    fun setSelection(start : Int, end : Int)

    /**
     * Gets selection start index.
     *
     * @return selection start index
     */
    fun getSelectionStart() : Int

    /**
     * Gets selection end index.
     *
     * @return selection end index (exclusive)
     */
    fun getSelectionEnd() : Int
}