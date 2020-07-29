/*
 * Created by Murillo Comino on 29/07/20 13:22
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 29/07/20 13:18
 */

package br.com.onimur.handlepathoz.utils

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.*
import android.os.Environment.getExternalStorageDirectory
import br.com.onimur.handlepathoz.utils.Constants.SDCard.SDCARD_PATHS
import br.com.onimur.handlepathoz.utils.extension.isDigitsOnly
import br.com.onimur.handlepathoz.utils.extension.logD
import java.io.File
import java.io.File.pathSeparator
import java.io.File.separator
import java.lang.System.getenv

object SDCardUtils {
    private val envExternalStorage = getenv("EXTERNAL_STORAGE").orEmpty()
    private val envSecondaryStorage = getenv("SECONDARY_STORAGE").orEmpty()
    private val envEmulatedStorageTarget = getenv("EMULATED_STORAGE_TARGET").orEmpty()

    @Suppress("DEPRECATION")
    private val emulatedStorage: String
        get() {
            var rawStorageId = ""
            if (SDK_INT > JELLY_BEAN) {
                val path = getExternalStorageDirectory().absolutePath
                val folders = path.split(separator)
                val lastSegment = folders.last()
                if (lastSegment.isNotBlank() && lastSegment.isDigitsOnly()) {
                    rawStorageId = lastSegment
                }
            }
            return if (rawStorageId.isBlank()) {
                logD("emulatedStorage: rawStorageId is Blank")
                envEmulatedStorageTarget
            } else {
                logD("emulatedStorage: rawStorageId is $rawStorageId")
                envEmulatedStorageTarget + separator + rawStorageId
            }
        }

    private val secondaryStorage: List<String>
        get() = if (envSecondaryStorage.isNotBlank()) {
            logD("secondaryStorage: It has no secondary storage.")
            envSecondaryStorage.split(pathSeparator)
        } else {
            logD("secondaryStorage: It has no secondary storage.")
            listOf()
        }

    private val availableSDCardsPaths: List<String>
        get() {
            val availableSDCardsPaths = mutableListOf<String>()
            SDCARD_PATHS.forEach { path ->
                val file = File(path)
                if (file.exists()) {
                    logD("availableSDCardsPaths: $path exists")
                    availableSDCardsPaths.add(path)
                }
            }
            return availableSDCardsPaths
        }

    fun getStorageDirectories(context: Context): Array<String> {
        val availableDirectories = HashSet<String>()
        if (envEmulatedStorageTarget.isNotBlank()) {
            logD("getStorageDirectories: $envEmulatedStorageTarget")
            availableDirectories.add(emulatedStorage)
        } else {
            logD("getStorageDirectories: envEmulatedStorageTarget is blank")
            availableDirectories.addAll(getExternalStorage(context))
        }
        availableDirectories.addAll(secondaryStorage)
        return availableDirectories.toTypedArray()
    }

    private fun getExternalStorage(context: Context): Set<String> {
        val availableDirectories = HashSet<String>()
        if (SDK_INT >= M) {
            val files = getExternalFilesDirs(context)
            files?.forEach { file ->
                val applicationSpecificAbsolutePath = file.absolutePath
                var rootPath = applicationSpecificAbsolutePath
                    .substring(9, applicationSpecificAbsolutePath.indexOf("Android/data"))
                rootPath = rootPath.substring(rootPath.indexOf("/storage/") + 1)
                rootPath = rootPath.substring(0, rootPath.indexOf("/"))
                if (rootPath != "emulated") {
                    logD("getExternalStorage: rootPath is: $rootPath")
                    availableDirectories.add(rootPath)
                }
            }
        } else {
            if (envExternalStorage.isBlank()) {
                logD("getExternalStorage: envExternalStorage is blank")
                availableDirectories.addAll(availableSDCardsPaths)
            } else {
                logD("getExternalStorage: envExternalStorage is $envExternalStorage")
                availableDirectories.add(envExternalStorage)
            }
        }
        return availableDirectories
    }

    private fun getExternalFilesDirs(context: Context): Array<File>? {
        return if (SDK_INT >= KITKAT) {
            context.getExternalFilesDirs(null)
        } else {
            context.getExternalFilesDir(null)?.listFiles()
        }
    }
}