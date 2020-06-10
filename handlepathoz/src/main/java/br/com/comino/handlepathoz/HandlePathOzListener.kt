/*
 *
 *  * Created by Murillo Comino on 09/06/20 22:08
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 09/06/20 22:02
 *
 */

package br.com.comino.handlepathoz


interface HandlePathOzListener {

    fun onRequestHandlePathOz(path: List<Pair<String, String>>, tr: Throwable? = null)

}