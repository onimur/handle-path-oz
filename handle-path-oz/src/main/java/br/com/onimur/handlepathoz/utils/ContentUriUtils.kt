/*
 * Created by Murillo Comino on 22/06/20 17:50
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 18/06/20 20:56
 */

package br.com.onimur.handlepathoz.utils

import android.content.ContentResolver
import android.net.Uri
import br.com.onimur.handlepathoz.utils.extension.logE

internal object ContentUriUtils {
    /**
     * Get the value of the column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param uri to Query
     * @param column
     * @param selection Optional Filter used in the query
     * @param selectionArgs Optional arguments used in the query
     * @return Value of the column, which is typically a file path or null.
     */
    fun getPathFromColumn(
        contentResolver: ContentResolver,
        uri: Uri,
        column: String,
        selection: String? = null,
        selectionArgs: Array<String?>? = null
    ): String {
        var path = ""
        val projection = arrayOf<String?>(column)
        try {
            getCursor(contentResolver, uri, projection, selection, selectionArgs)
                ?.use {
                    if (it.moveToFirst()) {
                        val index = it.getColumnIndexOrThrow(column)
                        path = it.getString(index)
                    }
                }
        } catch (e: Exception) {
            e.message?.let {
                //Checks whether the exception message does not contain the following string.
                if (!it.contains("column '$column' does not exist")) {
                    throw e
                }
            }
            logE("Error on gePathFromColumn: $column - ${e.message}")

        } finally {
            return path
        }
    }

    /**
     * Helper for get cursor
     *
     */
    fun getCursor(
        contentResolver: ContentResolver,
        uri: Uri,
        projection: Array<String?>? = null,
        selection: String? = null,
        selectionArgs: Array<String?>? = null
    ) =
        contentResolver.query(uri, projection, selection, selectionArgs, null)
}