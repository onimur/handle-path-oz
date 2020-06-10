/*
 *
 *  * Created by Murillo Comino on 09/06/20 21:53
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 09/06/20 21:41
 *
 */

package br.com.comino.handlepathoz.utils.extension

import android.util.Log

private const val TAG = "HandlePathOz"

internal fun Any.logD(message: String?) {
    Log.d(TAG, "${this::class.java.simpleName} - $message")
}

internal fun Any.logE(message: String?) {
    Log.e(TAG, "${this::class.java.simpleName} - $message")
}

internal fun <T:Any> T.alsoLogD(message: String = ""): T =
    also { logD("$this $message") }
