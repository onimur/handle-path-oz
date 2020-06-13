/*
 *
 *  * Created by Murillo Comino on 13/06/20 16:54
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 13/06/20 16:54
 *
 */

package br.com.comino.handlepathoz.utils

import android.content.Context
import android.net.Uri

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
        val projection = arrayOf<String?>(column)
        try {
            getCursor(context, uri, projection, selection, selectionArgs)
                ?.use {
                    if (it.moveToFirst()) {
                        val index = it.getColumnIndexOrThrow(column)
                        return it.getString(index)
                    }
                }
        } catch (e: Exception) {
            //TODO handle error
        }
        return ""
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