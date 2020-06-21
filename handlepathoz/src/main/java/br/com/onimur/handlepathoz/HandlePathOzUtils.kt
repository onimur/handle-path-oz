/*
 * Created by Murillo Comino on 21/06/20 20:55
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 21/06/20 20:47
 */

package br.com.onimur.handlepathoz

import android.content.Context
import android.net.Uri
import android.os.Build
import br.com.onimur.handlepathoz.utils.Constants
import br.com.onimur.handlepathoz.utils.FileUtils
import br.com.onimur.handlepathoz.utils.FileUtils.deleteTemporaryFiles
import br.com.onimur.handlepathoz.utils.PathUtils
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
    private val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT


    @FlowPreview
    fun getRealPath(listUri: List<Uri>, concurrency: Int) {
        val list = mutableListOf<Pair<Int, String>>()
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
    }

    private fun getPath(uri: Uri): Pair<Int, String> {
        val contentResolver = context.contentResolver
        val pathTempFile = FileUtils.getFullPathTemp(context, uri)
        val file: File?
        return if (isKitKat) {
            val returnedPath = PathUtils.getPathAboveKitKat(context, uri)
            when {
                //Cloud
                uri.isCloudFile -> {
                    file = File(pathTempFile)
                    FileUtils.downloadFile(contentResolver, file, uri, job)
                    Pair(Constants.HandlePathOzConts.CLOUD_FILE, pathTempFile).alsoLogD()
                }
                //Third Party App
                returnedPath.isBlank() -> {
                    file = File(pathTempFile)
                    FileUtils.downloadFile(contentResolver, file, uri, job)
                    Pair(Constants.HandlePathOzConts.UNKNOWN_FILE_CHOOSER, pathTempFile).alsoLogD()
                }
                //Unknown Provider or unknown mime type
                uri.isUnknownProvider(returnedPath, contentResolver) -> {
                    file = File(pathTempFile)
                    FileUtils.downloadFile(contentResolver, file, uri, job)
                    Pair(Constants.HandlePathOzConts.UNKNOWN_PROVIDER, pathTempFile).alsoLogD()
                }
                //LocalFile
                else -> {
                    Pair(Constants.HandlePathOzConts.LOCAL_PROVIDER, returnedPath).alsoLogD()
                }
            }
        } else {
            Pair(
                Constants.HandlePathOzConts.BELOW_KITKAT_FILE,
                PathUtils.getPathBelowKitKat(context, uri)
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