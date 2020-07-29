/*
 * Created by Murillo Comino on 29/07/20 14:25
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 29/07/20 14:21
 */

package br.com.onimur.handlepathoz.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import br.com.onimur.handlepathoz.errors.EmptyGooglePhotosException
import br.com.onimur.handlepathoz.errors.HandlePathOzUnknownException
import br.com.onimur.handlepathoz.errors.UnknownFilePathException
import br.com.onimur.handlepathoz.utils.Constants.PathUri.COLUMN_DATA
import br.com.onimur.handlepathoz.utils.Constants.PathUri.COLUMN_DISPLAY_NAME
import br.com.onimur.handlepathoz.utils.Constants.PathUri.FOLDER_DOWNLOAD
import br.com.onimur.handlepathoz.utils.ContentUriUtils.getPathFromColumn
import br.com.onimur.handlepathoz.utils.FileUtils.getSubFolders
import br.com.onimur.handlepathoz.utils.SDCardUtils.getStorageDirectories
import br.com.onimur.handlepathoz.utils.extension.*
import java.io.File

object PathUtils {
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     */
    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    internal fun getPathAboveKitKat(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        //Document Provider
        return when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                when {
                    uri.isExternalStorageDocument -> externalStorageDocument(context, uri)
                    uri.isRawDownloadsDocument -> rawDownloadsDocument(contentResolver, uri)
                    uri.isDownloadsDocument -> downloadsDocument(contentResolver, uri)
                    uri.isMediaDocument -> mediaDocument(contentResolver, uri)
                    else -> {
                        logD("Another Document Provider: ${uri.authority}")
                        return ""
                    }
                }
            }
            // MediaStore (and general)
            uri.isMediaStore -> {
                val path = getPathFromColumn(contentResolver, uri, COLUMN_DATA)
                return if (uri.isGooglePhotosUri) googlePhotosUri(uri)
                    ?: throw EmptyGooglePhotosException(path)
                else {
                    anotherFileProvider(path)
                }
            }
            uri.isFile -> uri.path ?: throw UnknownFilePathException(uri.toString())

            else -> throw HandlePathOzUnknownException(uri.toString())

        }
    }

    internal fun getPathBelowKitKat(context: Context, uri: Uri): String {
        val projection = arrayOf<String?>(COLUMN_DATA)
        var path = ""
        val loader = CursorLoader(context, uri, projection, null, null, null)
        loader.loadInBackground()?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndexOrThrow(COLUMN_DATA)
                path = it.getString(index)
            }
        }
        return path
    }

    /**
     * Method for external document
     *
     */
    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private fun externalStorageDocument(context: Context, uri: Uri): String {
        logD("File is External Storage")
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":").toTypedArray()
        val type = split[0]
        if ("primary".equals(type, ignoreCase = true)) {
            return if (split.size > 1) {
                "${Environment.getExternalStorageDirectory()}/${split[1]}"
            } else {
                "${Environment.getExternalStorageDirectory()}/"
            }
        } else {
            val path = "storage/${docId.replace(":", "/")}"
            if (File(path).exists()) {
                return "/$path"
            }
            val availableExternalStorage = getStorageDirectories(context)
            var root = ""
            availableExternalStorage.forEach { storage ->
                root = if (split[1].startsWith("/")) {
                    "$storage${split[1]}"
                } else {
                    "$storage/${split[1]}"
                }
            }
            return if (root.contains(type)) {
                path
            } else {
                if (root.startsWith("/storage/") || root.startsWith("storage/")) {
                    root
                } else if (root.startsWith("/")) {
                    "/storage$root"
                } else {
                    "/storage/$root"
                }
            }
        }
    }

    /**
     * Method for rawDownloadDocument
     *
     */
    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")
    private fun rawDownloadsDocument(contentResolver: ContentResolver, uri: Uri): String {
        logD("File is Raw Downloads Document")
        val fileName = getPathFromColumn(contentResolver, uri, COLUMN_DISPLAY_NAME)
        val subFolderName = getSubFolders(uri.toString())
        return if (fileName.isNotBlank()) {
            "${Environment.getExternalStorageDirectory()}/$FOLDER_DOWNLOAD/$subFolderName$fileName"
        } else {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"),
                id.toLong()
            )
            getPathFromColumn(contentResolver, contentUri, COLUMN_DATA)
        }
    }

    /**
     * Method for downloadsDocument
     *
     */
    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private fun downloadsDocument(contentResolver: ContentResolver, uri: Uri): String {
        logD("File is Downloads Documents")
        val fileName = getPathFromColumn(contentResolver, uri, COLUMN_DISPLAY_NAME)
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
        return getPathFromColumn(contentResolver, contentUri, COLUMN_DATA)
    }

    /**
     * Method for MediaDocument
     *
     */
    @SuppressLint("NewApi")
    private fun mediaDocument(contentResolver: ContentResolver, uri: Uri): String {
        logD("File is Media Document")
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
            contentResolver,
            contentUri,
            COLUMN_DATA,
            selection,
            selectionArgs
        )
    }

    /**
     * Method for googlePhotos
     *
     */
    private fun googlePhotosUri(uri: Uri): String? {
        // Return the remote address
        logD("File is Google Photos")
        return uri.lastPathSegment
    }

    /**
     *
     * Different fileprovider
     */
    private fun anotherFileProvider(path: String): String {
        if (path.isBlank()) {
            logD("Unknown File Provider")
        } else {
            logD("Another File Provider")
        }
        return path
    }
}
