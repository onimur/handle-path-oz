/*
 *
 *  * Created by Murillo Comino on 09/06/20 22:10
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 09/06/20 21:52
 *
 */

package br.com.comino.handlepathoz

import android.content.Context
import android.net.Uri
import br.com.comino.handlepathoz.utils.PathUtils
import br.com.comino.handlepathoz.utils.extension.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class HandlePathOz(
    private val context: Context,
    private val listener: HandlePathOzListener
) {
    private val pathUtils = PathUtils(context)
    private lateinit var job: Job
    private val handleScope = CoroutineScope(Main)

    /**
     *
     * Method responsible for handle file path, for previous API of KitKat and later.
     *
     * @param listUri list to handle
     */
    fun getRealPath(listUri: List<Uri>) {
        job = handleScope.launch {
            try {
                logD("Launch Job")
                val time = measureTimeMillis {
                    listener.onRequestHandlePathOz(pathUtils.getPath(listUri))
                }
                logD("Total task time: $time ms")
            } catch (tr: Throwable) {
                listener.onRequestHandlePathOz(emptyList(), tr)
                logE("${tr.cause} - ${tr.message}")
                deleteTemporaryFile()
            } finally {
                delay(300)
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
     * Cancel the task, if it is active
     *
     */
    fun cancelTask() {
        if (job.isActive) {
            job.cancel()
            deleteTemporaryFile()
            logD("\nJob isActive: ${job.isActive}\nJob isCancelled: ${job.isCancelled}\nJob isCompleted: ${job.isCompleted}")
        } else {
            logD("Job remains active: ${job.isActive}")
            listener.onRequestHandlePathOz(emptyList())
            deleteTemporaryFile()
        }
    }

    /**
     * Delete the files in the "Temp" folder at the root of the project.
     *
     */
    private fun deleteTemporaryFile() {
        context.getExternalFilesDir("Temp")?.let { folder ->
            folder.listFiles()?.let { files ->
                files.forEach {
                    if (it.deleteRecursively()) {
                        logD("$it delete file was called")
                    } else {
                        logE("$it there is no file")
                    }
                }
            }
        }
    }
}