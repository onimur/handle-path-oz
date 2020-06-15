/*
 *
 *  * Created by Murillo Comino on 15/06/20 20:06
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 15/06/20 18:06
 *
 */

package br.com.comino.example.handlepathoz.kotlin

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.TextView
import br.com.comino.example.handlepathoz.R

class ProgressDialog(context: Context, private val text: String) : Dialog(context) {
    private lateinit var textView: TextView
    private lateinit var textViewLoad: TextView

    var currentLoad:String = ""
    set(value) {
        textViewLoad.text = value
        field = value
    }


    override fun create() {
        val view = View.inflate(context, R.layout.dialog_progressbar, null)
        setContentView(view)
        textView = view.findViewById(R.id.tv_progress_dialog)
        textViewLoad = view.findViewById(R.id.tv_list_load)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        textView.text = text

    }
}