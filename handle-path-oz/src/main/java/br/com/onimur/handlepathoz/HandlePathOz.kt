/*
 * Created by Murillo Comino on 23/06/20 16:56
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 23/06/20 16:56
 */

package br.com.onimur.handlepathoz

import android.content.Context
import android.net.Uri
import br.com.onimur.handlepathoz.utils.HandlePathOzUtils
import kotlinx.coroutines.FlowPreview

class HandlePathOz(context: Context, listener: HandlePathOzListener) {
    private val handlePathOzUtils =
        HandlePathOzUtils(context, listener)

    /**
     *
     * Method responsible for handle file path, for previous API of KitKat and later.
     *
     * @param listUri list to handle
     *
     * @param concurrency It explains the number of tasks that can be performed in parallel,
     * by default it is 10. If you choose 1 then the task will be sequential.
     */
    @FlowPreview
    fun getRealPath(listUri: List<Uri>, concurrency: Int) {
        handlePathOzUtils.getRealPath(listUri, concurrency)
    }

    @FlowPreview
    fun getRealPath(listUri: List<Uri>) {
        getRealPath(listUri, 10)
    }

    /**
     * Cancel the task, if it is active
     *
     */
    fun cancelTask() {
        handlePathOzUtils.cancelTask()
    }

    /**
     * Destroy all task if it is active
     *
     */
    fun onDestroy() {
        handlePathOzUtils.onDestroy()
    }

    /**
     * Delete temporary files on the folder if exists:
     *  yourPackageName/files/Temp/...
     *
     */
    fun deleteTemporaryFiles() {
        handlePathOzUtils.deleteTemporaryFiles()
    }

}