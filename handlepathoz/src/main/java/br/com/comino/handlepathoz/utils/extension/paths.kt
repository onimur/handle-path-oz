/*
 *
 *  * Created by Murillo Comino on 06/06/20 18:28
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 06/06/20 17:30
 *
 */

package br.com.comino.handlepathoz.utils.extension

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import br.com.comino.handlepathoz.utils.extension.PathUri.PATH_DOWNLOAD
import br.com.comino.handlepathoz.utils.extension.PathUri.PATH_DROPBOX
import br.com.comino.handlepathoz.utils.extension.PathUri.PATH_EXTERNAL_STORAGE
import br.com.comino.handlepathoz.utils.extension.PathUri.PATH_GOOGLE_APPS
import br.com.comino.handlepathoz.utils.extension.PathUri.PATH_GOOGLE_PHOTOS
import br.com.comino.handlepathoz.utils.extension.PathUri.PATH_MEDIA
import br.com.comino.handlepathoz.utils.extension.PathUri.PATH_ONEDRIVE
import br.com.comino.handlepathoz.utils.extension.PathUri.PATH_RAW_DOWNLOAD
import java.util.*

/**
 * Take the path (Uri) of each [action]
 *
 */
internal inline fun ClipData.forEachUri(action: (Uri) -> Unit) {
    for (element in 0 until itemCount)
        action(getItemAt(element).uri)
}

/**
 * Retrieve a Uri list
 * Or if the passed [Intent] does not contain any [Uri], then an empty [List] is returned.
 *
 * @return - [List] of Uri or emptyList
 */
fun Intent?.getListUri(): List<Uri> {
    return mutableListOf<Uri>().also { list ->
        this?.let { intent ->
            intent.clipData?.let { data ->
                data.forEachUri {
                    list.add(it)
                }
            } ?: intent.data?.let {
                list.add(it)
            }
        }
    }
}

/**
 * Checks Uri authority
 *
 */
internal val Uri.isExternalStorageDocument get() = PATH_EXTERNAL_STORAGE == authority

internal val Uri.isDownloadsDocument get() = PATH_DOWNLOAD == authority

internal val Uri.isMediaDocument get() = PATH_MEDIA == authority

internal val Uri.isGooglePhotosUri get() = PATH_GOOGLE_PHOTOS == authority

internal val Uri.isRawDownloadsDocument get() = toString().contains(PATH_RAW_DOWNLOAD)

internal val Uri.isMediaStore get() = "content".equals(scheme, ignoreCase = true)

internal val Uri.isFile get() = "file".equals(scheme, ignoreCase = true)
///////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Check different providers
 *
 */
internal fun Uri.isDropBox() =
    toString().toLowerCase(Locale.ROOT).contains("content://${PATH_DROPBOX}")

internal fun Uri.isGoogleDrive() =
    toString().toLowerCase(Locale.ROOT).contains(PATH_GOOGLE_APPS)

internal fun Uri.isOneDrive() =
    toString().toLowerCase(Locale.ROOT).contains(PATH_ONEDRIVE)

