/*
 *
 *  * Created by Murillo Comino on 06/06/20 14:00
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 06/06/20 14:00
 *
 */

package br.com.comino.handlepathoz.utils

import android.content.Context
import android.net.Uri
import br.com.comino.handlepathoz.utils.extension.PathUri.FOLDER_DOWNLOAD

/**
 * Get the value of the column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.
 *
 * @param context
 * @param uri to Query
 * @param column
 * @param selection Optional Filter used in the query
 * @param selectionArgs Optional arguments used in the query
 * @return Value of the column, which is typically a file path or null.
 */
internal fun getPathFromColumn(
    context: Context,
    uri: Uri,
    column: String,
    selection: String? = null,
    selectionArgs: Array<String?>? = null
): String? {
    val projection = arrayOf<String?>(column)
    try {
        context.contentResolver
            .query(uri, projection, selection, selectionArgs, null)
            ?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndexOrThrow(column)
                    return it.getString(index)
                }
            }
    } catch (e: Exception) {
        //TODO handle error
    }
    return null
}

/**
 * Returns subfolder from the main folder to the file location or empty string
 * EXAMPLE:
 * Input uriString = "content://com.android.providers.downloads.documents/document/raw%3%2Fstorage%2Femulated%2F0%2FDownload%2FsubFolder%2FsubFolder2%2Ffile.jpg"
 * Input folderRoot = "Download"
 * Output: subFolder/subFolder2/
 *
 * @param uriString Path file
 * @param folderRoot It is usually "Download"
 */
internal fun getSubFolders(uriString: String, folderRoot: String = FOLDER_DOWNLOAD) =
    uriString
        .replace("%2F", "/")
        .replace("%20", " ")
        .replace("%3A", ":")
        .split("/")
        .run {
            val indexRoot = indexOf(folderRoot)
            if (folderRoot.isNotBlank().and(indexRoot != -1)) {
                subList(indexRoot + 1, lastIndex)
                    .joinToString(separator = "") { "$it/" }
            } else {
                ""
            }
        }