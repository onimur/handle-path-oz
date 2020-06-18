/*
 * Created by Murillo Comino on 18/06/20 20:56
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 18/06/20 20:44
 */

package br.com.onimur.sample.handlepathoz

import android.content.Intent
import br.com.comino.choicekotlinjava.BaseChoiceActivity
import br.com.comino.choicekotlinjava.Choice
import br.com.onimur.sample.handlepathoz.java.MainActivity

class ChoiceActivity : BaseChoiceActivity() {

    override val choices: List<Choice>
        get() = listOf(
            Choice(
                "Java",
                "Run the Example App with HandlePathOz library written in Java.",
                Intent(this, MainActivity::class.java)
            ),
            Choice(
                "Kotlin",
                "Run the Example App with HandlePathOz library written in Kotlin.",
                Intent(this, br.com.onimur.sample.handlepathoz.kotlin.MainActivity::class.java)
            )
        )
}