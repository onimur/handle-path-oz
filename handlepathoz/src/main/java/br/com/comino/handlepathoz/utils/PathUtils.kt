/*
 *
 *  * Created by Murillo Comino on 07/06/20 11:13
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 07/06/20 11:12
 *
 */

package br.com.comino.handlepathoz.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import br.com.comino.handlepathoz.utils.extension.*
import br.com.comino.handlepathoz.utils.extension.PathUri.COLUMN_DATA
import br.com.comino.handlepathoz.utils.extension.PathUri.COLUMN_DISPLAY_NAME
import br.com.comino.handlepathoz.utils.extension.PathUri.FOLDER_DOWNLOAD
import java.io.File

internal object PathUtils {

    private lateinit var context: Context
    private lateinit var uri: Uri

    /**
     * Method responsible for retrieving the file path, for previous API of KitKat and later.
     *
     * @return path of file
     */
    fun getRealPathFromUri(context: Context, uri: Uri): String {
        this.context = context
        this.uri = uri

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        return if (isKitKat) getPathAboveKitKat()
        else getPathBelowKitKat()
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     */
    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private fun getPathAboveKitKat(): String {
        //Document Provider
        return when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                when {
                    uri.isExternalStorageDocument -> externalStorageDocument()
                    uri.isRawDownloadsDocument -> rawDownloadsDocument()
                    uri.isDownloadsDocument -> downloadsDocument()
                    uri.isMediaDocument -> mediaDocument()
                    else -> TODO("Throw Exception Document Provider")
                }
            }
            // MediaStore (and general)
            uri.isMediaStore -> {
                if (uri.isGooglePhotosUri) googlePhotosUri()
                    ?: TODO("Throw Exception to GooglePhotos")
                else {
                    TODO("Throw Exception MediaStore")
                }
            }
            uri.isFile -> uri.path ?: TODO("Throw Exception Files Path")

            else -> TODO("Throw Unknown Exception")

        }
    }

    private fun getPathBelowKitKat(): String {
        val projection = arrayOf<String?>(COLUMN_DATA)
        try {
            val loader = CursorLoader(context, uri, projection, null, null, null)
            loader.loadInBackground()?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndexOrThrow(COLUMN_DATA)
                    return it.getString(index)
                }
            }
        } catch (e: Exception) {
            //TODO handle error
        }
        return ""
    }

    /**
     * Method for googlePhotos
     *
     */
    private fun googlePhotosUri(): String? {
        val path = getPathFromColumn(context, uri, COLUMN_DATA)
        // Return the remote address
        return if (path.isNotBlank()) {
            uri.lastPathSegment ?: path
        } else {
            null
        }
    }

    /**
     * Method for external document
     *
     */
    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private fun externalStorageDocument(): String {
        val docId = DocumentsContract.getDocumentId(uri)
        val split: Array<String?> = docId.split(":").toTypedArray()
        val type = split[0]
        return if ("primary".equals(type, ignoreCase = true)) {
            if (split.size > 1) {
                "${Environment.getExternalStorageDirectory()}/${split[1]}"
            } else {
                "${Environment.getExternalStorageDirectory()}/"
            }
        } else {
            "storage/${docId.replace(":", "/")}"
        }
    }

    /**
     * Method for rawDownloadDocument
     *
     */
    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")
    private fun rawDownloadsDocument(): String {
        val fileName = getPathFromColumn(context, uri, COLUMN_DISPLAY_NAME)
        val subFolderName = getSubFolders(uri.toString())
        return if (fileName.isNotBlank()) {
            "${Environment.getExternalStorageDirectory()}/$FOLDER_DOWNLOAD/$subFolderName$fileName"
        } else {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"),
                id.toLong()
            )
            getPathFromColumn(context, contentUri, COLUMN_DATA)
        }
    }

    /**
     * Method for downloadsDocument
     *
     */
    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private fun downloadsDocument(): String {
        val fileName = getPathFromColumn(context, uri, COLUMN_DISPLAY_NAME)
        val subFolderName = getSubFolders(uri.toString())
        if (fileName.isNotBlank()) {
            return "${Environment.getExternalStorageDirectory()}/$FOLDER_DOWNLOAD/$subFolderName$fileName"
        }
        var id = DocumentsContract.getDocumentId(uri)
        if (id.startsWith("raw:")) {
            id = id.replaceFirst("raw:".toRegex(), "")
            val file = File(id)
            if (file.exists()) return id
        } else if (id.startsWith("raw%3A%2F")) {
            id = id.replaceFirst("raw%3A%2F".toRegex(), "")
            val file = File(id)
            if (file.exists()) return id
        }
        val contentUri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"),
            id.toLong()
        )
        return getPathFromColumn(context, contentUri, COLUMN_DATA)
    }

    /**
     * Method for MediaDocument
     *
     */
    @SuppressLint("NewApi")
    private fun mediaDocument(): String {
        val docId = DocumentsContract.getDocumentId(uri)
        val split: Array<String?> = docId.split(":").toTypedArray()
        val contentUri: Uri =
            when (split[0]) {
                "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                //Todo test
                else -> MediaStore.Files.getContentUri(docId)
            }
        val selection = "_id=?"
        val selectionArgs = arrayOf(split[1])
        return getPathFromColumn(
            context,
            contentUri,
            COLUMN_DATA,
            selection,
            selectionArgs
        )
    }

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
    private fun getPathFromColumn(
        context: Context,
        uri: Uri,
        column: String,
        selection: String? = null,
        selectionArgs: Array<String?>? = null
    ): String {
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
        return ""
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
    fun getSubFolders(uriString: String, folderRoot: String = FOLDER_DOWNLOAD) =
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
}
