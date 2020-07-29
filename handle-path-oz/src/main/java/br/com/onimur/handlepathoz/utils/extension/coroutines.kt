/*
 * Created by Murillo Comino on 29/07/20 14:40
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 29/07/20 14:39
 */

package br.com.onimur.handlepathoz.utils.extension

import kotlinx.coroutines.Job

/**
 * Status Job
 */
internal val Job.wasCancelled: Boolean
    get() = !isActive and isCompleted and isCancelled

internal val Job.wasCompleted: Boolean
    get() = !isActive and isCompleted and !isCancelled

internal val Job.isCancelling: Boolean
    get() = !isActive and !isCompleted and isCancelled

internal val Job.isCompleting: Boolean
    get() = isActive and !isCompleted and !isCancelled

internal val Job.isNew: Boolean
    get() = !isActive and !isCompleted and !isCancelled