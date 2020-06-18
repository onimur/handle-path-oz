/*
 * Created by Murillo Comino on 18/06/20 20:56
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 18/06/20 20:44
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

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
        pathFileTemp: String,
        uri: Uri,
        coroutineScope: CoroutineScope
    ): String {
        if (coroutineScope.isActive) {
            val file = File(pathFileTemp)

            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                logD("${input.available()}")
                FileOutputStream(file).use { output ->
                    copyFile(input, output, file, coroutineScope)
                }
            }

            logD("File and Path copied - $pathFileTemp")
        } else {
            logE("Task canceled before completing the download of the last file.")
            throw CancellationException()
        }

        return pathFileTemp
    }

    private fun copyFile(
        input: InputStream,
        output: OutputStream,
        file: File,
        coroutineScope: CoroutineScope
    ) {
        val buffer = ByteArray(1024)
        var read: Int = input.read(buffer)
        logD("Copying ${file.absoluteFile}")
        while (read != -1) {
            if (coroutineScope.isActive) {
                output.write(buffer, 0, read)
                read = input.read(buffer)
            } else {
                input.close()
                output.close()
                file.deleteRecursively()
                logE("Task canceled and file ${file.absoluteFile} deleted")
                throw CancellationException()
            }

        }
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