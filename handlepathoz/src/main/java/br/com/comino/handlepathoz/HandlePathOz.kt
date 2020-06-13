/*
 *
 *  * Created by Murillo Comino on 13/06/20 16:54
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 13/06/20 16:54
 *
 */

package br.com.comino.handlepathoz

import android.content.Context
import android.net.Uri
import android.os.Build
import br.com.comino.handlepathoz.utils.FileUtils.downloadFile
import br.com.comino.handlepathoz.utils.PathUtils.getPathAboveKitKat
import br.com.comino.handlepathoz.utils.PathUtils.getPathBelowKitKat
import br.com.comino.handlepathoz.utils.extension.*
import br.com.comino.handlepathoz.utils.extension.HandlePathOzConts.BELOW_KITKAT_FILE
import br.com.comino.handlepathoz.utils.extension.HandlePathOzConts.CLOUD_FILE
import br.com.comino.handlepathoz.utils.extension.HandlePathOzConts.LOCAL_PROVIDER
import br.com.comino.handlepathoz.utils.extension.HandlePathOzConts.UNKNOWN_FILE_CHOOSER
import br.com.comino.handlepathoz.utils.extension.HandlePathOzConts.UNKNOWN_PROVIDER
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
                    listUri.forEach {
                        list.add(getPath(it))
                    }
                }
                logD("Total task time: $time ms")
            } catch (e: CancellationException) {
                error = e
                logE("$e - ${e.message}")
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

    private val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    /**
     * Handle the uri in background and return them.
     *
     * @param uri
     */
    private suspend fun getPath(uri: Uri) = withContext(IO) {
        if (isKitKat) {
            val returnedPath = getPathAboveKitKat(context, uri)
            when {
                //Cloud
                uri.isCloudFile -> {
                    Pair(CLOUD_FILE, downloadFile(context, uri, this)).alsoLogD()
                }
                returnedPath.isBlank() -> {
                    Pair(UNKNOWN_FILE_CHOOSER, "").alsoLogD()
                }
                //TODO() need try catch
                //Todo: Add checks for unknown file extensions
                uri.isUnknownProvider(returnedPath, context) -> {
                    Pair(UNKNOWN_PROVIDER, downloadFile(context, uri, this)).alsoLogD()
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
     * Cancel the task, if it is active
     *
     */
    fun cancelTask() {
        if (job.isActive) {
            job.cancelChildren()
            logD("\nJob isActive: ${job.isActive}\nJob isCancelled: ${job.isCancelled}\nJob isCompleted: ${job.isCompleted}")
        }
    }
}