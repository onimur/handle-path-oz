/*
 * Created by Murillo Comino on 18/06/20 20:56
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 18/06/20 20:44
 */

package br.com.onimur.handlepathoz

import android.content.Context
import android.net.Uri
import android.os.Build
import br.com.onimur.handlepathoz.utils.Constants.HandlePathOzConts.BELOW_KITKAT_FILE
import br.com.onimur.handlepathoz.utils.Constants.HandlePathOzConts.CLOUD_FILE
import br.com.onimur.handlepathoz.utils.Constants.HandlePathOzConts.LOCAL_PROVIDER
import br.com.onimur.handlepathoz.utils.Constants.HandlePathOzConts.UNKNOWN_FILE_CHOOSER
import br.com.onimur.handlepathoz.utils.Constants.HandlePathOzConts.UNKNOWN_PROVIDER
import br.com.onimur.handlepathoz.utils.FileUtils.deleteTemporaryFiles
import br.com.onimur.handlepathoz.utils.FileUtils.downloadFile
import br.com.onimur.handlepathoz.utils.FileUtils.getFullPathTemp
import br.com.onimur.handlepathoz.utils.PathUtils.getPathAboveKitKat
import br.com.onimur.handlepathoz.utils.PathUtils.getPathBelowKitKat
import br.com.onimur.handlepathoz.utils.extension.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class HandlePathOz(
    private val context: Context,
    private val listener: HandlePathOzListener
) {
    private var isDestroy = false
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
                listener.onLoading(0)
                logD("Launch Job")
                val time = measureTimeMillis {
                    listUri.forEachIndexed { index, uri ->
                        list.add(getPathAsync(uri).await())
                        listener.onLoading(index + 1)
                    }
                }
                logD("Total task time: $time ms")
            } catch (tr: Throwable) {
                error = tr
                logE("$tr - ${tr.message}")
            } finally {
                if (!isDestroy){
                    listener.onRequestHandlePathOz(list, error)
                }
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
        val contentResolver = context.contentResolver
        val pathTempFile = getFullPathTemp(context, uri)
        if (isKitKat) {
            async {
                val returnedPath = getPathAboveKitKat(context, uri)
                when {
                    //Cloud
                    uri.isCloudFile -> {
                        Pair(
                            CLOUD_FILE,
                            downloadFile(contentResolver, pathTempFile, uri, this)
                        ).alsoLogD()
                    }
                    //Third Party App
                    returnedPath.isBlank() -> {
                        Pair(
                            UNKNOWN_FILE_CHOOSER,
                            downloadFile(contentResolver, pathTempFile, uri, this)
                        ).alsoLogD()
                    }
                    //Unknown Provider or unknown mime type
                    uri.isUnknownProvider(returnedPath, contentResolver) -> {
                        Pair(
                            UNKNOWN_PROVIDER,
                            downloadFile(contentResolver, pathTempFile, uri, this)
                        ).alsoLogD()
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
     * Cancel the children task, if it is active
     *
     */
    fun cancelTask() {
        if (job.isActive) {
            job.cancelChildren()
            logD("\nJob isActive: ${job.isActive}\nJob isCancelled: ${job.isCancelled}\nJob isCompleted: ${job.isCompleted}")
        }
    }

    /**
     *
     *
     */
    fun onDestroy () {
        if (job.isActive){
            isDestroy = true
            job.cancel()
        }
    }


    fun deleteTemporaryFiles() {
        deleteTemporaryFiles(context)
    }

}