/*
 *
 *  * Created by Murillo Comino on 04/06/20 14:58
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 04/06/20 14:58
 *
 */

package br.com.comino.handlepathoz.utils

import android.net.Uri
import java.util.*

//Local
private const val PATH_ANDROID = "com.android"
private const val PATH_EXTERNAL_STORAGE = "${PATH_ANDROID}.externalstorage.documents"
private const val PATH_DOWNLOAD = "${PATH_ANDROID}.providers.downloads.documents"
private const val PATH_MEDIA = "${PATH_ANDROID}.media.documents"
private const val PATH_RAW_DOWNLOAD = "${PATH_DOWNLOAD}/document/raw"

//Cloud GoogleDrive
private const val PATH_GOOGLE = "com.google.android"
private const val PATH_GOOGLE_APPS = "${PATH_GOOGLE}.apps"
private const val PATH_GOOGLE_PHOTOS = "${PATH_GOOGLE_APPS}.photos.content"

//Cloud OneDrive
private const val PATH_ONEDRIVE = "com.microsoft.skydrive.content"

//Cloud DropBox
private const val PATH_DROPBOX = "com.dropbox.android"

/**
 * Checks Uri authority
 *
 */
internal fun Uri.isExternalStorageDocument() =
    PATH_EXTERNAL_STORAGE == authority

internal fun Uri.isDownloadsDocument() =
    PATH_DOWNLOAD == authority

internal fun Uri.isMediaDocument() =
    PATH_MEDIA == authority

internal fun Uri.isGooglePhotosUri() =
    PATH_GOOGLE_PHOTOS == authority

internal fun Uri.isRawDownloadsDocument() =
    toString().contains(PATH_RAW_DOWNLOAD)
///////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Check different providers
 *
 */
internal fun Uri?.isDropBox() =
    toString().toLowerCase(Locale.ROOT).contains("content://${PATH_DROPBOX}")

internal fun Uri?.isGoogleDrive() =
    toString().toLowerCase(Locale.ROOT).contains(PATH_GOOGLE_APPS)

internal fun Uri?.isOneDrive() =
    toString().toLowerCase(Locale.ROOT).contains(PATH_ONEDRIVE)