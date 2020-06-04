/*
 *
 *  * Created by Murillo Comino on 04/06/20 16:09
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 04/06/20 16:09
 *
 */

package br.com.comino.handlepathoz.utils

internal object PathUri {
    //Local
    const val PATH_ANDROID = "com.android"
    const val PATH_EXTERNAL_STORAGE = "${PATH_ANDROID}.externalstorage.documents"
    const val PATH_DOWNLOAD = "${PATH_ANDROID}.providers.downloads.documents"
    const val PATH_MEDIA = "${PATH_ANDROID}.media.documents"
    const val PATH_RAW_DOWNLOAD = "${PATH_DOWNLOAD}/document/raw"

    //Cloud GoogleDrive
    const val PATH_GOOGLE = "com.google.android"
    const val PATH_GOOGLE_APPS = "${PATH_GOOGLE}.apps"
    const val PATH_GOOGLE_PHOTOS = "${PATH_GOOGLE_APPS}.photos.content"

    //Cloud OneDrive
    const val PATH_ONEDRIVE = "com.microsoft.skydrive.content"

    //Cloud DropBox
    const val PATH_DROPBOX = "com.dropbox.android"
}