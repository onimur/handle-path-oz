/*
 * Created by Murillo Comino on 27/07/20 13:46
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 27/07/20 12:14
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
     * Working with multiple uri's
     * Method responsible for handle file path, for previous API of KitKat and later.
     *
     * @param listUri list to handle
     *
     * @param concurrency It explains the number of tasks that can be performed in parallel,
     * by default it is 10. If you choose 1 then the task will be sequential.
     */
    @FlowPreview
    fun getListRealPath(listUri: List<Uri>, concurrency: Int) {
        handlePathOzUtils.getListRealPath(listUri, concurrency)
    }

    @FlowPreview
    fun getListRealPath(listUri: List<Uri>) {
        getListRealPath(listUri, 10)
    }

    /**
     * Working with single uri
     * Method responsible for handle file path, for previous API of KitKat and later.
     *
     * @param uri
     */
    @FlowPreview
    fun getRealPath(uri: Uri) {
        handlePathOzUtils.getRealPath(uri)
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