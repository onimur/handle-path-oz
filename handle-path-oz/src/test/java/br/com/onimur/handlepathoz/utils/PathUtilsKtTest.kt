/*
 * Created by Murillo Comino on 23/06/20 11:08
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 23/06/20 11:03
 */

package br.com.onimur.handlepathoz.utils

import br.com.onimur.handlepathoz.utils.FileUtils.getSubFolders
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test

class PathUtilsKtTest {
    private lateinit var folder: String
    private lateinit var path: String

    companion object {
        const val PATH =
            "content://com.android.providers.downloads.documents/document/raw%3%2Fstorage%2Femulated%2F0%2FDownload%2F"
    }

    @Test
    fun `getSubFolders with a single folder should return a single subfolder`() {
        folder = "ABC"
        path = "$PATH${folder}%2Fexample.pdf"
        val subFolder = getSubFolders(path)
        assertThat(subFolder, `is`("$folder/"))
    }

    @Test
    fun `getSubFolders with a multiples folder should return a multiples subfolder`() {
        folder = "ABC%2FDEF%2FGHI%2FJKLM%2FNOP%2FQRS%2FTUV%2FXYZ"
        path = "$PATH${folder}%2Fexample.pdf"
        val subFolder = getSubFolders(path)
        assertThat(subFolder, `is`("${folder.replace("%2F", "/")}/"))
    }

    @Test
    fun `getSubFolders without folder should return a empty String`() {
        path = "${PATH}example.pdf"
        val subFolder = getSubFolders(path)
        assertThat(subFolder, `is`(""))
    }

    @Test
    fun `getSubFolders with empty or blank folderRoot should return a empty String`() {
        path = "${PATH}example.pdf"
        folder = "ABC"
        val blankSubFolder = getSubFolders(path, "    ")
        val emptySubFolder = getSubFolders(path, "")
        assertThat(blankSubFolder, `is`(""))
        assertThat(emptySubFolder, `is`(""))

        val pathWitSubFolder = "${PATH}${folder}%2Fexample.pdf"
        val blankSubFolder2 = getSubFolders(pathWitSubFolder, "    ")
        val emptySubFolder2 = getSubFolders(pathWitSubFolder, "")
        assertThat(blankSubFolder2, `is`(""))
        assertThat(emptySubFolder2, `is`(""))

    }

    @Test
    fun `getSubFolders with invalid folderRoot should return a empty String`() {
        path = "${PATH}example.pdf"
        folder = "ABC"

        val blankSubFolder = getSubFolders(path, "invalidFolder")
        val emptySubFolder = getSubFolders(path, "Downloads")
        assertThat(blankSubFolder, `is`(""))
        assertThat(emptySubFolder, `is`(""))

        val pathWitSubFolder = "${PATH}${folder}%2Fexample.pdf"
        val blankSubFolder2 = getSubFolders(pathWitSubFolder, "invalidFolder")
        val emptySubFolder2 = getSubFolders(pathWitSubFolder, "Downloads")
        assertThat(blankSubFolder2, `is`(""))
        assertThat(emptySubFolder2, `is`(""))
    }
}