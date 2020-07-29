/*
 * Created by Murillo Comino on 29/07/20 14:47
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 29/07/20 14:36
 */

package br.com.onimur.handlepathoz.utils

import android.content.Context
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.KITKAT
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.errors.HandlePathOzListenerException
import br.com.onimur.handlepathoz.model.PathOz
import br.com.onimur.handlepathoz.utils.Constants.HandlePathOz.BELOW_KITKAT_FILE
import br.com.onimur.handlepathoz.utils.Constants.HandlePathOz.CLOUD_FILE
import br.com.onimur.handlepathoz.utils.Constants.HandlePathOz.LOCAL_PROVIDER
import br.com.onimur.handlepathoz.utils.Constants.HandlePathOz.UNKNOWN_FILE_CHOOSER
import br.com.onimur.handlepathoz.utils.Constants.HandlePathOz.UNKNOWN_PROVIDER
import br.com.onimur.handlepathoz.utils.FileUtils.deleteTemporaryFiles
import br.com.onimur.handlepathoz.utils.FileUtils.downloadFile
import br.com.onimur.handlepathoz.utils.FileUtils.getFullPathTemp
import br.com.onimur.handlepathoz.utils.PathUtils.getPathAboveKitKat
import br.com.onimur.handlepathoz.utils.PathUtils.getPathBelowKitKat
import br.com.onimur.handlepathoz.utils.extension.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import kotlin.system.measureTimeMillis

internal class HandlePathOzUtils(
    private val context: Context,
    private val listener: HandlePathOzListener
) {
    private val mainScope = MainScope()
    private var job: Job? = null
    private val isKitKat = SDK_INT >= KITKAT

    @FlowPreview
    fun getListRealPath(listUri: List<Uri>, concurrency: Int) {
        if (listener is HandlePathOzListener.MultipleUri) {
            val list = mutableListOf<PathOz>()
            var error: Throwable? = null
            job = mainScope.launch {
                try {
                    listener.onLoading(0)
                    logD("Launch Job")
                    val time = measureTimeMillis {
                        listUri.asFlow()
                            .flatMapMerge(concurrency) { uri ->
                                flow { emit(getPath(uri)) }
                                    .flowOn(Dispatchers.IO)
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
        } else {
            throw HandlePathOzListenerException("If you are working with single uri, use the interface: HandlePathOzListener.SingleUri")
        }
    }

    fun getRealPath(uri: Uri) {
        if (listener is HandlePathOzListener.SingleUri) {
            var error: Throwable? = null
            var pathOz = PathOz("", "")
            job = mainScope.launch {
                try {
                    logD("Launch Job")
                    val time = measureTimeMillis {
                        pathOz = flow { emit(getPath(uri)) }
                            .flowOn(Dispatchers.IO)
                            .single()
                    }
                    logD("Total task time: $time ms")
                } catch (tr: Throwable) {
                    error = tr
                    logE("$tr - ${tr.message}")
                } finally {
                    if (mainScope.isActive) {
                        //so Activity is active
                        listener.onRequestHandlePathOz(pathOz, error)
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
        } else {
            throw HandlePathOzListenerException("If you are working with multiple uri's, use the interface: HandlePathOzListener.MultipleUri")
        }
    }

    private fun getPath(uri: Uri): PathOz {
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
                    PathOz(CLOUD_FILE, pathTempFile).alsoLogD()
                }
                //Third Party App
                returnedPath.isBlank() -> {
                    file = File(pathTempFile)
                    downloadFile(contentResolver, file, uri, job)
                    PathOz(UNKNOWN_FILE_CHOOSER, pathTempFile).alsoLogD()
                }
                //Unknown Provider or unknown mime type
                uri.isUnknownProvider(returnedPath, contentResolver) -> {
                    file = File(pathTempFile)
                    downloadFile(contentResolver, file, uri, job)
                    PathOz(UNKNOWN_PROVIDER, pathTempFile).alsoLogD()
                }
                //LocalFile
                else -> {
                    PathOz(LOCAL_PROVIDER, returnedPath).alsoLogD()
                }
            }
        } else {
            PathOz(
                BELOW_KITKAT_FILE,
                getPathBelowKitKat(context, uri)
            ).alsoLogD()
        }
    }

    fun cancelTask() {
        job?.let {
            if (it.isActive) {
                it.cancel()
                logD("\nJob isActive: ${it.isActive}\nJob isCancelled: ${it.isCancelled}\nJob isCompleted: ${it.isCompleted}")
            }
        }
    }

    fun onDestroy() {
        if (mainScope.isActive) {
            mainScope.cancel()
        }
    }

    fun deleteTemporaryFiles() {
        deleteTemporaryFiles(context)
    }
}