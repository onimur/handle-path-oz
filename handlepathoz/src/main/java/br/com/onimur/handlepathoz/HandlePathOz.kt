/*
 * Created by Murillo Comino on 21/06/20 00:13
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 20/06/20 22:49
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
import kotlinx.coroutines.flow.*
import java.io.File
import kotlin.system.measureTimeMillis

class HandlePathOz(private val context: Context, private val listener: HandlePathOzListener) {

    private val mainScope = MainScope()
    private var job: Job? = null
    private val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    /**
     *
     * Method responsible for handle file path, for previous API of KitKat and later.
     *
     * @param listUri list to handle
     */
    @FlowPreview
    fun getRealPath(listUri: List<Uri>) {
        val list = mutableListOf<Pair<Int, String>>()
        var error: Throwable? = null
        job = mainScope.launch {
            try {
                listener.onLoading(0)
                logD("Launch Job")
                val time = measureTimeMillis {
                    listUri.asFlow()
                        .flatMapMerge(6) { uri ->
                            flow { emit(getPathAsync(uri)) }
                                .flowOn(IO)
                        }.collectIndexed { index, pair ->
                            list.add(pair)
                            listener.onLoading(index + 1)
                        }
                }
                logD("Total task time: $time ms")
            } catch (tr: Throwable) {
                error = tr
                logE("$tr - ${tr.message}")
            } finally {
                if (mainScope.isActive) {
                    //so Activity is active
                    listener.onRequestHandlePathOz(list, error)
                }
                logD(
                    "MainScope isActive: ${mainScope.isActive}" +
                            "\nThis Scope isActive: ${this.isActive}" +
                            "\nJob isNew: ${job?.isNew}" +
                            "\nJob isCompleting: ${job?.isCompleting}" +
                            "\nJob isCancelling: ${job?.isCancelling}" +
                            "\nJob wasCancelled: ${job?.wasCancelled}" +
                            "\nJob wasCompleted: ${job?.wasCompleted}"
                )

            }
        }
    }

    /**
     * Handle the uri in background and return them.
     *
     * @param uri
     */
    private fun getPathAsync(uri: Uri): Pair<Int, String> {
        val contentResolver = context.contentResolver
        val pathTempFile = getFullPathTemp(context, uri)
        val file: File?
        return if (isKitKat) {
            val returnedPath = getPathAboveKitKat(context, uri)
            when {
                //Cloud
                uri.isCloudFile -> {
                    file = File(pathTempFile)
                    downloadFile(contentResolver, file, uri, job)
                    Pair(CLOUD_FILE, pathTempFile).alsoLogD()
                }
                //Third Party App
                returnedPath.isBlank() -> {
                    file = File(pathTempFile)
                    downloadFile(contentResolver, file, uri, job)
                    Pair(UNKNOWN_FILE_CHOOSER, pathTempFile).alsoLogD()
                }
                //Unknown Provider or unknown mime type
                uri.isUnknownProvider(returnedPath, contentResolver) -> {
                    file = File(pathTempFile)
                    downloadFile(contentResolver, file, uri, job)
                    Pair(UNKNOWN_PROVIDER, pathTempFile).alsoLogD()
                }
                //LocalFile
                else -> {
                    Pair(LOCAL_PROVIDER, returnedPath).alsoLogD()
                }
            }
        } else {
            Pair(BELOW_KITKAT_FILE, getPathBelowKitKat(context, uri)).alsoLogD()
        }
    }


    /**
     * Cancel the children task, if it is active
     *
     */
    fun cancelTask() {
        job?.let {
            if (it.isActive) {
                it.cancel()
                logD("\nJob isActive: ${it.isActive}\nJob isCancelled: ${it.isCancelled}\nJob isCompleted: ${it.isCompleted}")
            }
        }
    }

    /**
     *
     *
     */
    fun onDestroy() {
        if (mainScope.isActive) {
            mainScope.cancel()
        }
    }


    fun deleteTemporaryFiles() {
        deleteTemporaryFiles(context)
    }

}