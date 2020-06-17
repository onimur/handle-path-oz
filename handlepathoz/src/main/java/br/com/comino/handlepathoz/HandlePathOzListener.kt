/*
 * Created by Murillo Comino on 17/06/20 13:18
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 15/06/20 20:06
 */

package br.com.comino.handlepathoz


interface HandlePathOzListener {

    /**
     * Listener communicates with the responsible for your call.
     *
     * @param listPath returns the list of path already handled.
     * If the user cancels the task then the list returns to the last path that could be handled
     *
     * @param tr Returns the exception for the user to handle (Optional).
     * The exception return will be null, when the task is completely complete without errors
     * and without being canceled.
     */
    fun onRequestHandlePathOz(listPath: List<Pair<Int, String>>, tr: Throwable? = null)

    /**
     * Optional method to show how many uris have already been verified.
     *
     * @param currentUri
     */
    fun onLoading(currentUri: Int) {}

}