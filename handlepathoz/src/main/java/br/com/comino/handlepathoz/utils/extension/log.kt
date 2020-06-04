/*
 *
 *  * Created by Murillo Comino on 04/06/20 18:31
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 04/06/20 18:31
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