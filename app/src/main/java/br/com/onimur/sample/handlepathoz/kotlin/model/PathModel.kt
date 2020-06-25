/*
 * Created by Murillo Comino on 24/06/20 21:16
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 24/06/20 21:14
 */

package br.com.onimur.sample.handlepathoz.kotlin.model

import br.com.onimur.handlepathoz.model.PairPath
import java.io.File

 data class PathModel(private val pairPath: PairPath) {
    private val file = File(pairPath.path)
    var expanded = false
    var type = pairPath.type
    var nameFile: String = file.name
    var absolutePath: String = file.absolutePath
}