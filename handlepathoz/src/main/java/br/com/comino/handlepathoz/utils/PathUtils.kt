/*
 *
 *  * Created by Murillo Comino on 11/06/20 16:25
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 11/06/20 16:17
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
import android.provider.OpenableColumns
import androidx.loader.content.CursorLoader
import br.com.comino.handlepathoz.utils.extension.*
import br.com.comino.handlepathoz.utils.extension.PathUri.COLUMN_DATA
import br.com.comino.handlepathoz.utils.extension.PathUri.COLUMN_DISPLAY_NAME
import br.com.comino.handlepathoz.utils.extension.PathUri.FOLDER_DOWNLOAD
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.*

internal class PathUtils(private val context: Context) {
    private val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    /**
     * Handle the uri in background and return them.
     *
     * @param uri
     */
    suspend fun getPath(uri: Uri) = withContext(IO) {
        if (isKitKat) {
            val returnedPath = getPathAboveKitKat(uri)
            when {
                //Cloud
                uri.isCloudFile -> {
                    Pair("cloud", downloadFile(uri, this)).alsoLogD()
                }
                returnedPath.isBlank() -> {
                    Pair("", "").alsoLogD()
                }
                //TODO() need try catch
                //Todo: Add checks for unknown file extensions
                uri.isUnknownProvider(returnedPath, context) -> {
                    Pair("unknownProvider", downloadFile(uri, this)).alsoLogD()
                }
                //LocalFile
                else -> {
                    Pair("localProvider", getPathAboveKitKat(uri)).alsoLogD()
                }
            }
        } else {
            Pair("", getPathBelowKitKat(uri)).alsoLogD()
        }
    }


    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     */
    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private fun getPathAboveKitKat(uri: Uri): String {
        //Document Provider
        return when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                when {
                    uri.isExternalStorageDocument -> externalStorageDocument(uri)
                    uri.isRawDownloadsDocument -> rawDownloadsDocument(uri)
                    uri.isDownloadsDocument -> downloadsDocument(uri)
                    uri.isMediaDocument -> mediaDocument(uri)
                    else -> ""
                }
            }
            // MediaStore (and general)
            uri.isMediaStore -> {
                if (uri.isGooglePhotosUri) googlePhotosUri(uri)
                    ?: TODO("Throw Exception to GooglePhotos")
                else {
                    TODO("Throw Exception MediaStore")
                }
            }
            uri.isFile -> uri.path ?: TODO("Throw Exception Files Path")

            else -> TODO("Throw Unknown Exception")

        }
    }

    private fun getPathBelowKitKat(uri: Uri): String {
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
     *  Method that downloads the file to an internal folder at the root of the project.
     *  For cases where the file has an unknown provider, cloud files and for users using
     *  third-party file explorer api.
     *
     * @param uri of the file
     * @return new path string
     */
    private fun downloadFile(
        uri: Uri,
        coroutineScope: CoroutineScope
    ): String {
        lateinit var pathPlusName: String
        lateinit var inputStream: InputStream
        val folder: File? = context.getExternalFilesDir("Temp")
        try {
            inputStream = context.contentResolver.openInputStream(uri)!!
        } catch (e: FileNotFoundException) {
            logE("${e.javaClass} - ${e.message}")
        }
        try {
            pathPlusName = "${folder.toString()}/${getFileName(uri)}"
            val file = File(pathPlusName)
            val outputStream = FileOutputStream(file)
            copyFile(inputStream, outputStream, file, coroutineScope)
        } catch (e: IOException) {
            logE("${e.javaClass} - ${e.message}")
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

    private fun getFileName(uri: Uri): String? {
        var result: String? = null

        uri.scheme?.let {
            if (it == "content") {
                getCursor(uri)?.use { cursor ->
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
    private fun googlePhotosUri(uri: Uri): String? {
        val path = getPathFromColumn(uri, COLUMN_DATA)
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
    private fun rawDownloadsDocument(uri: Uri): String {
        val fileName = getPathFromColumn(uri, COLUMN_DISPLAY_NAME)
        val subFolderName = getSubFolders(uri.toString())
        return if (fileName.isNotBlank()) {
            "${Environment.getExternalStorageDirectory()}/$FOLDER_DOWNLOAD/$subFolderName$fileName"
        } else {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"),
                id.toLong()
            )
            getPathFromColumn(contentUri, COLUMN_DATA)
        }
    }

    /**
     * Method for downloadsDocument
     *
     */
    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private fun downloadsDocument(uri: Uri): String {
        val fileName = getPathFromColumn(uri, COLUMN_DISPLAY_NAME)
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
        return getPathFromColumn(contentUri, COLUMN_DATA)
    }

    /**
     * Method for MediaDocument
     *
     */
    @SuppressLint("NewApi")
    private fun mediaDocument(uri: Uri): String {
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
     * @param uri to Query
     * @param column
     * @param selection Optional Filter used in the query
     * @param selectionArgs Optional arguments used in the query
     * @return Value of the column, which is typically a file path or null.
     */
    private fun getPathFromColumn(
        uri: Uri,
        column: String,
        selection: String? = null,
        selectionArgs: Array<String?>? = null
    ): String {
        val projection = arrayOf<String?>(column)
        try {
            getCursor(uri, projection, selection, selectionArgs)
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
