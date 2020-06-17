/*
 * Created by Murillo Comino on 17/06/20 13:18
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 15/06/20 16:48
 */

package br.com.comino.handlepathoz.utils

import android.content.Context
import android.net.Uri
import br.com.comino.handlepathoz.utils.extension.logE

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
        context: Context,
        uri: Uri,
        column: String,
        selection: String? = null,
        selectionArgs: Array<String?>? = null
    ): String {
        var path = ""
        val projection = arrayOf<String?>(column)
        try {
            getCursor(context, uri, projection, selection, selectionArgs)
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
        context: Context,
        uri: Uri,
        projection: Array<String?>? = null,
        selection: String? = null,
        selectionArgs: Array<String?>? = null
    ) =
        context.contentResolver
            .query(uri, projection, selection, selectionArgs, null)
}