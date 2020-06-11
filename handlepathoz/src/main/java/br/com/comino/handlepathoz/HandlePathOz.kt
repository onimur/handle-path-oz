/*
 *
 *  * Created by Murillo Comino on 11/06/20 16:25
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 11/06/20 16:24
 *
 */

package br.com.comino.handlepathoz

import android.content.Context
import android.net.Uri
import br.com.comino.handlepathoz.utils.PathUtils
import br.com.comino.handlepathoz.utils.extension.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class HandlePathOz(
    private val context: Context,
    private val listener: HandlePathOzListener
) {
    private var pathUtils:PathUtils = PathUtils(context)
    private var job:Job = Job()
    private val coroutineScope:CoroutineScope = CoroutineScope(Main + job)


    /**
     *
     * Method responsible for handle file path, for previous API of KitKat and later.
     *
     * @param listUri list to handle
     */
    fun getRealPath(listUri: List<Uri>) {
        val list = mutableListOf<Pair<String,String>>()
        var error:Throwable? = null

        coroutineScope.launch {
            try {
                logD("Launch Job")
                val time = measureTimeMillis {
                    listUri.forEach {
                        list.add(pathUtils.getPath(it))
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

    /**
     * Delete the files in the "Temp" folder at the root of the project.
     *
     */
     fun deleteTemporaryFile() {
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