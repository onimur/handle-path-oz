/*
 * Created by Murillo Comino on 29/07/20 14:47
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 29/07/20 14:46
 */

package br.com.onimur.handlepathoz.utils

import android.annotation.SuppressLint
import android.provider.MediaStore

object Constants {
    internal object PathUri {
        //Local
        private const val PATH_ANDROID = "com.android"
        const val PATH_EXTERNAL_STORAGE = "$PATH_ANDROID.externalstorage.documents"
        const val PATH_DOWNLOAD = "$PATH_ANDROID.providers.downloads.documents"
        const val PATH_MEDIA = "$PATH_ANDROID.providers.media.documents"
        const val PATH_RAW_DOWNLOAD = "$PATH_DOWNLOAD/document/raw"

        //Cloud GoogleDrive
        private const val PATH_GOOGLE = "com.google.android"
        const val PATH_GOOGLE_APPS = "$PATH_GOOGLE.apps"
        const val PATH_GOOGLE_PHOTOS = "$PATH_GOOGLE_APPS.photos.content"

        //Cloud OneDrive
        const val PATH_ONEDRIVE = "com.microsoft.skydrive.content"

        //Cloud DropBox
        const val PATH_DROPBOX = "com.dropbox."

        //Folder
        const val FOLDER_DOWNLOAD = "Download"

        //Columns
        const val COLUMN_DISPLAY_NAME = MediaStore.Files.FileColumns.DISPLAY_NAME

        //Deprecated
        //val COLUMN_DATA = MediaStore.MediaColumns.DATA
        const val COLUMN_DATA = "_data"
    }
    internal object SDCard {
        @SuppressLint("SdCardPath")
        val SDCARD_PATHS = arrayOf(
            "/storage/sdcard0",
            "/storage/sdcard1",
            "/storage/extsdcard",
            "/storage/sdcard0/external_sdcard",
            "/mnt/extsdcard",
            "/mnt/sdcard/external_sd",
            "/mnt/sdcard/ext_sd",
            "/mnt/external_sd",
            "/mnt/media_rw/sdcard1",
            "/removable/microsd",
            "/mnt/emmc",
            "/storage/external_SD",
            "/storage/ext_sd",
            "/storage/removable/sdcard1",
            "/data/sdext",
            "/data/sdext2",
            "/data/sdext3",
            "/data/sdext4",
            "/sdcard1",
            "/sdcard2",
            "/storage/microsd"
        )
    }

    object HandlePathOz {
        const val BELOW_KITKAT_FILE = "belowKitkatFile"
        const val CLOUD_FILE = "cloudFile"
        const val UNKNOWN_PROVIDER = "unknownProvider"
        const val UNKNOWN_FILE_CHOOSER = "unknownFileChooser"
        const val LOCAL_PROVIDER = "localProvider"
    }
}

