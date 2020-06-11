/*
 *
 *  * Created by Murillo Comino on 11/06/20 19:56
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 11/06/20 16:46
 *
 */

package br.com.comino.handlepathoz.utils.extension

import android.provider.MediaStore

internal object PathUri {
    //Local
    const val PATH_ANDROID = "com.android"
    const val PATH_EXTERNAL_STORAGE = "$PATH_ANDROID.externalstorage.documents"
    const val PATH_DOWNLOAD = "$PATH_ANDROID.providers.downloads.documents"
    const val PATH_MEDIA = "$PATH_ANDROID.providers.media.documents"
    const val PATH_RAW_DOWNLOAD = "$PATH_DOWNLOAD/document/raw"

    //Cloud GoogleDrive
    const val PATH_GOOGLE = "com.google.android"
    const val PATH_GOOGLE_APPS = "$PATH_GOOGLE.apps"
    const val PATH_GOOGLE_PHOTOS = "$PATH_GOOGLE_APPS.photos.content"

    //Cloud OneDrive
    const val PATH_ONEDRIVE = "com.microsoft.skydrive.content"

    //Cloud DropBox
    const val PATH_DROPBOX = "com.dropbox.android"

    //Folder
    const val FOLDER_DOWNLOAD = "Download"

    //Columns
    const val COLUMN_DISPLAY_NAME = MediaStore.Files.FileColumns.DISPLAY_NAME
    //Deprecated
    //val COLUMN_DATA = MediaStore.MediaColumns.DATA
    const val COLUMN_DATA = "_data"
}

object HandlePathOzConts {
    const val BELOW_KITKAT_FILE = -1
    const val CLOUD_FILE= 1
    const val UNKNOWN_PROVIDER= 2
    const val LOCAL_PROVIDER= 3
    const val UNKNOWN_FILE_CHOOSER = 4
}