/*
 *
 *  * Created by Murillo Comino on 13/06/20 18:25
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 13/06/20 18:25
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
import br.com.comino.handlepathoz.utils.FileUtils.deleteTemporaryFile
import br.com.comino.handlepathoz.utils.FileUtils.downloadFile
import br.com.comino.handlepathoz.utils.PathUtils.getPathAboveKitKat
import br.com.comino.handlepathoz.utils.PathUtils.getPathBelowKitKat
import br.com.comino.handlepathoz.utils.extension.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
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

    /**
     * Handle the uri in background and return them.
     *
     * @param uri
     */
    private fun getPath(uri: Uri) =
        if (isKitKat) {
            val returnedPath = getPathAboveKitKat(context, uri)
            when {
                //Cloud
                uri.isCloudFile -> {
                    Pair(CLOUD_FILE, downloadFile(context, uri)).alsoLogD()
                }
                returnedPath.isBlank() -> {
                    Pair(UNKNOWN_FILE_CHOOSER, "").alsoLogD()
                }
                //TODO() need try catch
                //Todo: Add checks for unknown file extensions
                uri.isUnknownProvider(returnedPath, context) -> {
                    Pair(UNKNOWN_PROVIDER, downloadFile(context, uri)).alsoLogD()
                }
                //LocalFile
                else -> {
                    Pair(LOCAL_PROVIDER, returnedPath).alsoLogD()
                }
            }
        } else {
            Pair(BELOW_KITKAT_FILE, getPathBelowKitKat(context, uri)).alsoLogD()
        }

    /**
     * Cancel the task, if it is active
     *
     */
    fun cancelTask() {
        if (job.isActive) {
            job.cancel()
            logD("\nJob isActive: ${job.isActive}\nJob isCancelled: ${job.isCancelled}\nJob isCompleted: ${job.isCompleted}")
        }
    }

    fun deleteTemporaryFile() {
        deleteTemporaryFile(context)
    }
}