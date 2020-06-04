/*
 *
 *  * Created by Murillo Comino on 04/06/20 13:31
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 04/06/20 13:30
 *
 */

package br.com.comino.handlepathoz

import android.content.Intent
import br.com.comino.choicekotlinjava.BaseChoiceActivity
import br.com.comino.choicekotlinjava.Choice

class ChoiceActivity : BaseChoiceActivity() {

    override val choices: List<Choice>
        get() = listOf(
            Choice(
                "Java",
                "Run the Example App with HandlePathOz library written in Java.",
                Intent(this, br.com.comino.handlepathoz.java.MainActivity::class.java)
            ),
            Choice(
                "Kotlin",
                "Run the Example App with HandlePathOz library written in Kotlin.",
                Intent(this, br.com.comino.handlepathoz.kotlin.MainActivity::class.java)
            )
        )
}