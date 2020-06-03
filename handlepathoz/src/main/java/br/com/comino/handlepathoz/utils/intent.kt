package br.com.comino.handlepathoz.utils

import android.content.ClipData
import android.content.Intent
import android.net.Uri

/**
 * Take the path (Uri) of each [action]
 *
 */
internal inline fun ClipData.forEachUri(action: (Uri) -> Unit) {
    for (element in 0 until this.itemCount)
        action(this.getItemAt(element).uri)
}

/**
 * Retrieve a Uri list
 * Or if the passed intent does not contain any Uri, then an empty list is returned.
 *
 * @return - List of Uri or emptyList
 */
fun Intent?.getListUri(): List<Uri> {
    val listUri = mutableListOf<Uri>()
    return this?.let { intent ->
        intent.clipData?.let { data ->
            data.forEachUri {
                listUri += listUri
            }
            listUri.toList()
        } ?: intent.data?.let {
            listUri += it
            listUri.toList()
        }
    } ?: emptyList()
}

