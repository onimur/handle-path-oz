/*
 *
 *  * Created by Murillo Comino on 11/06/20 19:56
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 11/06/20 19:39
 *
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

}