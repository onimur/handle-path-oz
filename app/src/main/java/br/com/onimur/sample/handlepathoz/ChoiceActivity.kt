/*
 * Created by Murillo Comino on 27/07/20 15:18
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 27/07/20 15:16
 */

package br.com.onimur.sample.handlepathoz

import android.content.Intent
import br.com.comino.choicekotlinjava.BaseChoiceActivity
import br.com.comino.choicekotlinjava.Choice
import br.com.onimur.sample.handlepathoz.java.MultipleUriActivity
import br.com.onimur.sample.handlepathoz.java.SingleUriActivity

class ChoiceActivity : BaseChoiceActivity() {

    override val choices: List<Choice>
        get() = listOf(
            Choice(
                "Java - MultipleUri",
                "Run the Example App with MultipleUri using HandlePathOz library written in Java.",
                Intent(this, MultipleUriActivity::class.java)
            ),
            Choice(
                "Java - SingleUri",
                "Run the Example App with SingleUri using HandlePathOz library written in Java.",
                Intent(this, SingleUriActivity::class.java)
            ),
            Choice(
                "Kotlin - MultipleUri",
                "Run the Example App with MultipleUri using HandlePathOz library written in Kotlin.",
                Intent(
                    this,
                    br.com.onimur.sample.handlepathoz.kotlin.MultipleUriActivity::class.java
                )
            ),
            Choice(
                "Kotlin - SingleUri",
                "Run the Example App with SingleUri using HandlePathOz library written in Kotlin.",
                Intent(this, br.com.onimur.sample.handlepathoz.kotlin.SingleUriActivity::class.java)
            )
        )
}