/*
 * Created by Murillo Comino on 22/06/20 17:50
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 21/06/20 00:13
 */

package br.com.onimur.handlepathoz.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import br.com.onimur.handlepathoz.utils.Constants.PathUri.FOLDER_DOWNLOAD
import br.com.onimur.handlepathoz.utils.ContentUriUtils.getCursor
import br.com.onimur.handlepathoz.utils.extension.logD
import br.com.onimur.handlepathoz.utils.extension.logE
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import java.io.File
import java.io.FileOutputStream

internal object FileUtils {

    /**
     *  Method that downloads the file to an internal folder at the root of the project.
     *  For cases where the file has an unknown provider, cloud files and for users using
     *  third-party file explorer api.
     *
     * @param uri of the file
     * @return new path string
     */
     fun downloadFile(
        contentResolver: ContentResolver,
        file: File,
        uri: Uri,
        job: Job?
    ): Boolean {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                inputStream?.use { input ->
                    logD("${input.available()}")
                    FileOutputStream(file).use { output ->
                        job?.ensureActive()
                        val buffer = ByteArray(1024)
                        var read: Int = input.read(buffer)
                        logD("Copying ${file.name}")
                        while (read != -1) {
                            job?.ensureActive()
                            output.write(buffer, 0, read)
                            read = input.read(buffer)
                        }
                    }
                }
            } catch (e: Exception) {
                file.deleteRecursively()
                logE("Task canceled with exception ${e.message} and file ${file.name} deleted")
                throw e
            }
            logD("File and Path copied - ${file.name}")
            return true
        }


    fun getFullPathTemp(context: Context, uri: Uri): String {
        val folder: File? = context.getExternalFilesDir("Temp")
        return "${folder.toString()}/${getFileName(context, uri)}"
    }


    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null

        uri.scheme?.let {
            if (it == "content") {
                getCursor(context.contentResolver, uri)?.use { cursor ->
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

    /**
     * Delete the files in the "Temp" folder at the root of the project.
     *
     */
    fun deleteTemporaryFiles(context: Context) {
        context.getExternalFilesDir("Temp")?.let { folder ->
            folder.listFiles()?.let { files ->
                files.forEach {
                    if (it.deleteRecursively()) {
                        logD("${it.absoluteFile} delete file was called")
                    } else {
                        logE("${it.absoluteFile} there is no file")
                    }
                }
            }
        }
    }
}