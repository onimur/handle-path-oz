/*
 * Created by Murillo Comino on 27/07/20 15:18
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 27/07/20 13:20
 */

package br.com.onimur.sample.handlepathoz.kotlin.model

import br.com.onimur.handlepathoz.model.PathOz
import java.io.File

 data class PathModel(private val pathOz: PathOz) {
    private val file = File(pathOz.path)
    var expanded = false
    var type = pathOz.type
    var nameFile: String = file.name
    var absolutePath: String = file.absolutePath
}