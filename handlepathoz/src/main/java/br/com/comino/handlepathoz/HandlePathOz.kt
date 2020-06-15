/*
 *
 *  * Created by Murillo Comino on 15/06/20 20:06
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 15/06/20 19:41
 *
 */

package br.com.comino.handlepathoz

import android.content.Context
import android.net.Uri
import android.os.Build
import br.com.comino.handlepathoz.utils.Constants.HandlePathOzConts.BELOW_KITKAT_FILE
import br.com.comino.handlepathoz.utils.Constants.HandlePathOzConts.CLOUD_FILE
import br.com.comino.handlepathoz.utils.Constants.HandlePathOzConts.LOCAL_PROVIDER
import br.com.comino.handlepathoz.utils.Constants.HandlePathOzConts.UNKNOWN_FILE_CHOOSER
import br.com.comino.handlepathoz.utils.Constants.HandlePathOzConts.UNKNOWN_PROVIDER
import br.com.comino.handlepathoz.utils.FileUtils.deleteTemporaryFiles
import br.com.comino.handlepathoz.utils.FileUtils.downloadFile
import br.com.comino.handlepathoz.utils.PathUtils.getPathAboveKitKat
import br.com.comino.handlepathoz.utils.PathUtils.getPathBelowKitKat
import br.com.comino.handlepathoz.utils.extension.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class HandlePathOz(
    private val context: Context,
    private val listener: HandlePathOzListener
) {
    private var job: Job = Job()
    private val coroutineScope: CoroutineScope = CoroutineScope(Main + job)
    private val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    /**
     *
     * Method responsible for handle file path, for previous API of KitKat and later.
     *
     * @param listUri list to handle
     */
    fun getRealPath(listUri: List<Uri>) {
        val list = mutableListOf<Pair<Int, String>>()
        var error: Throwable? = null
        coroutineScope.launch {
            try {
                logD("Launch Job")
                val time = measureTimeMillis {
                    listener.onLoading(0)
                    listUri.forEachIndexed { index, uri ->
                        val path = getPathAsync(uri).await()
                        list.add(path)
                        listener.onLoading(index + 1)
                    }
                }
                logD("Total task time: $time ms")
            } catch (tr: Throwable) {
                error = tr
                logE("$tr - ${tr.message}")
            } finally {
                listener.onRequestHandlePathOz(list, error)
                with(job) {
                    logD(
                        "\nJob isNew: $isNew" +
                                "\nJob isCompleting: $isCompleting" +
                                "\nJob isCancelling: $isCancelling" +
                                "\nJob wasCancelled: $wasCancelled" +
                                "\nJob wasCompleted: $wasCompleted"
                    )
                }
            }
        }
    }

    /**
     * Handle the uri in background and return them.
     *
     * @param uri
     */
    private suspend fun getPathAsync(uri: Uri) = withContext(IO) {
        if (isKitKat) {
            async {

                val returnedPath = getPathAboveKitKat(context, uri)
                when {
                    //Cloud
                    uri.isCloudFile -> {
                        Pair(CLOUD_FILE, downloadFile(context, uri, this)).alsoLogD()
                    }
                    //Third Party App
                    returnedPath.isBlank() -> {
                        Pair(UNKNOWN_FILE_CHOOSER, downloadFile(context, uri, this)).alsoLogD()
                    }
                    //Unknown Provider or unknown mime type
                    uri.isUnknownProvider(returnedPath, context) -> {
                        Pair(UNKNOWN_PROVIDER, downloadFile(context, uri, this)).alsoLogD()
                    }
                    //LocalFile
                    else -> {
                        Pair(LOCAL_PROVIDER, returnedPath).alsoLogD()
                    }
                }
            }
        } else {
            async { Pair(BELOW_KITKAT_FILE, getPathBelowKitKat(context, uri)).alsoLogD() }
        }
    }


    /**
     * Cancel the task, if it is active
     *
     */
    fun cancelTask() {
        if (job.isActive) {
            job.cancelChildren()
            logD("\nJob isActive: ${job.isActive}\nJob isCancelled: ${job.isCancelled}\nJob isCompleted: ${job.isCompleted}")
        }
    }

    fun deleteTemporaryFiles() {
        deleteTemporaryFiles(context)
    }
}