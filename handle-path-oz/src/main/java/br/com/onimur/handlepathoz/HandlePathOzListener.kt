/*
 * Created by Murillo Comino on 27/07/20 13:46
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 27/07/20 13:20
 */

package br.com.onimur.handlepathoz

import br.com.onimur.handlepathoz.model.PathOz

interface HandlePathOzListener {

    interface SingleUri : HandlePathOzListener {

        /**
         * Listener communicates with the responsible for your call.
         *
         * @param pathOz returns the path already handled.
         *
         * @param tr Returns the exception for the user to handle (Optional).
         * The exception return will be null, when the task is completely complete without errors
         * and without being canceled.
         */
        fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable? = null)
    }

    interface MultipleUri : HandlePathOzListener {

        /**
         * Listener communicates with the responsible for your call.
         *
         * @param listPathOz returns the list of path already handled.
         * If the user cancels the task then the list returns to the last path that could be handled
         *
         * @param tr Returns the exception for the user to handle (Optional).
         * The exception return will be null, when the task is completely complete without errors
         * and without being canceled.
         */
        fun onRequestHandlePathOz(listPathOz: List<PathOz>, tr: Throwable? = null)

        /**
         * Optional method to show how many uris have already been verified.
         *
         * @param currentUri
         */
        fun onLoading(currentUri: Int) {}
    }
}

