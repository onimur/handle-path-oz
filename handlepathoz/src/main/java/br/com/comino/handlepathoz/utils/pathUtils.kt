/*
 *
 *  * Created by Murillo Comino on 12/06/20 16:50
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 12/06/20 16:49
 *
 */

package br.com.comino.handlepathoz.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.loader.content.CursorLoader
import br.com.comino.handlepathoz.utils.extension.*
import br.com.comino.handlepathoz.utils.extension.PathUri.FOLDER_DOWNLOAD
import kotlinx.coroutines.*
import java.io.*

/**
 * Get a file path from a Uri. This will get the the path for Storage Access
 * Framework Documents, as well as the _data field for the MediaStore and
 * other file-based ContentProviders.
 *
 */
@SuppressLint("NewApi")
@Suppress("DEPRECATION")
internal fun getPathAboveKitKat(context: Context, uri: Uri): String {
    //Document Provider
    return when {
        DocumentsContract.isDocumentUri(context, uri) -> {
            when {
                uri.isExternalStorageDocument -> externalStorageDocument(uri)
                uri.isRawDownloadsDocument -> rawDownloadsDocument(context, uri)
                uri.isDownloadsDocument -> downloadsDocument(context, uri)
                uri.isMediaDocument -> mediaDocument(context, uri)
                else -> ""
            }
        }
        // MediaStore (and general)
        uri.isMediaStore -> {
            if (uri.isGooglePhotosUri) googlePhotosUri(context, uri)
                ?: TODO("Throw Exception to GooglePhotos")
            else {
                TODO("Throw Exception MediaStore")
            }
        }
        uri.isFile -> uri.path ?: TODO("Throw Exception Files Path")

        else -> TODO("Throw Unknown Exception")

    }
}

internal fun getPathBelowKitKat(context: Context, uri: Uri): String {
    val projection = arrayOf<String?>(PathUri.COLUMN_DATA)
    try {
        val loader = CursorLoader(context, uri, projection, null, null, null)
        loader.loadInBackground()?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndexOrThrow(PathUri.COLUMN_DATA)
                return it.getString(index)
            }
        }
    } catch (e: Exception) {
        //TODO handle error
    }
    return ""
}

/**
 *  Method that downloads the file to an internal folder at the root of the project.
 *  For cases where the file has an unknown provider, cloud files and for users using
 *  third-party file explorer api.
 *
 * @param uri of the file
 * @return new path string
 */
internal fun downloadFile(
    context: Context,
    uri: Uri,
    coroutineScope: CoroutineScope
): String {
    lateinit var pathPlusName: String
    lateinit var inputStream: InputStream
    val folder: File? = context.getExternalFilesDir("Temp")
    try {
        /**
         * TODO
         *have any bug in this line. When the job is canceled on this line,
         *it takes a long time to perform the cancellation, why?
         *I will try with @see [kotlin.io.use]
         */
        inputStream = context.contentResolver.openInputStream(uri)!!
    } catch (e: FileNotFoundException) {
    }
    try {
        pathPlusName = "${folder.toString()}/${getFileName(context, uri)}"
        val file = File(pathPlusName)
        val outputStream = FileOutputStream(file)
        copyFile(inputStream, outputStream, file, coroutineScope)
    } catch (e: IOException) {
    }
    return pathPlusName
}

private fun copyFile(
    input: InputStream,
    output: OutputStream,
    file: File,
    coroutineScope: CoroutineScope
) {
    runBlocking {
        val buffer = ByteArray(1024)
        var read: Int = input.read(buffer)
        delay(10)
        while (read != -1) {
            if (coroutineScope.isActive) {
                output.write(buffer, 0, read)
                read = input.read(buffer)
            } else {
                delay(2)
                val path = file.name
                val deleted = file.deleteRecursively()
                logE("Task was canceled and the $path file was deleted: $deleted")
                throw CancellationException()
            }
        }
    }
}

internal fun getFileName(context: Context, uri: Uri): String? {
    var result: String? = null

    uri.scheme?.let {
        if (it == "content") {
            getCursor(context, uri)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    result =
                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            result = result?.substring(cut.plus(1))
        }
    }
    return result
}

/**
 * Method for googlePhotos
 *
 */
private fun googlePhotosUri(context: Context, uri: Uri): String? {
    val path = getPathFromColumn(context, uri, PathUri.COLUMN_DATA)
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
private fun externalStorageDocument(uri: Uri): String {
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
private fun rawDownloadsDocument(context: Context, uri: Uri): String {
    val fileName = getPathFromColumn(context, uri, PathUri.COLUMN_DISPLAY_NAME)
    val subFolderName = getSubFolders(uri.toString())
    return if (fileName.isNotBlank()) {
        "${Environment.getExternalStorageDirectory()}/$FOLDER_DOWNLOAD/$subFolderName$fileName"
    } else {
        val id = DocumentsContract.getDocumentId(uri)
        val contentUri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"),
            id.toLong()
        )
        getPathFromColumn(context, contentUri, PathUri.COLUMN_DATA)
    }
}

/**
 * Method for downloadsDocument
 *
 */
@SuppressLint("NewApi")
@Suppress("DEPRECATION")
private fun downloadsDocument(context: Context, uri: Uri): String {
    val fileName = getPathFromColumn(context, uri, PathUri.COLUMN_DISPLAY_NAME)
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
    return getPathFromColumn(context, contentUri, PathUri.COLUMN_DATA)
}

/**
 * Method for MediaDocument
 *
 */
@SuppressLint("NewApi")
private fun mediaDocument(context: Context, uri: Uri): String {
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
        PathUri.COLUMN_DATA,
        selection,
        selectionArgs
    )
}

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
private fun getPathFromColumn(
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
private fun getCursor(
    context: Context,
    uri: Uri,
    projection: Array<String?>? = null,
    selection: String? = null,
    selectionArgs: Array<String?>? = null
) =
    context.contentResolver
        .query(uri, projection, selection, selectionArgs, null)


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
private fun getSubFolders(uriString: String, folderRoot: String = FOLDER_DOWNLOAD) =
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