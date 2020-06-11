/*
 *
 *  * Created by Murillo Comino on 11/06/20 20:00
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 10/06/20 18:42
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

    override fun create() {
        val view = View.inflate(context, R.layout.dialog_progressbar, null)
        setContentView(view)
        textView = view.findViewById(R.id.tv_progress_dialog)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        textView.text = text

    }
}