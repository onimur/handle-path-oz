/*
 *
 *  * Created by Murillo Comino on 06/06/20 22:23
 *  * Github: github.com/MurilloComino
 *  * StackOverFlow: pt.stackoverflow.com/users/128573
 *  * Email: murillo_comino@hotmail.com
 *  *
 *  * Copyright (c) 2020.
 *  * Last modified 06/06/20 21:59
 *
 */

package br.com.comino.example.handlepathoz.kotlin

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.comino.example.handlepathoz.R
import br.com.comino.handlepathoz.utils.extension.getListUri

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {
        const val REQUEST_PERMISSION = 123
        const val REQUEST_OPEN_GALLERY = 1111
    }

    private lateinit var buttonOpen: Button
    private lateinit var rvOriginal: RecyclerView
    private lateinit var rvReal: RecyclerView
    private lateinit var originalAdapter: ListUriAdapter
    private lateinit var realAdapter: ListUriAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //
        init()
        startAction()
    }

    private fun init() {
        buttonOpen = findViewById(R.id.btn_open)
        originalAdapter = ListUriAdapter(ArrayList())
        realAdapter = ListUriAdapter(ArrayList())
    }

    private fun startAction() {
        buttonOpen.setOnClickListener { openFile() }

        rvOriginal = findViewById<RecyclerView>(R.id.lv_original).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = LinearLayoutManager(context) as RecyclerView.LayoutManager
            // specify an viewAdapter (see also next example)
            adapter = originalAdapter
        }

        rvReal = findViewById<RecyclerView>(R.id.lv_real).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = realAdapter
        }
    }

    private fun openFile() {
        if (checkSelfPermission()) {
            val intent =
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    Intent(ACTION_PICK, EXTERNAL_CONTENT_URI)
                } else {
                    Intent(ACTION_PICK, INTERNAL_CONTENT_URI)
                }

            intent.apply {
                type = "*/*"
                action = Intent.ACTION_GET_CONTENT
                action = Intent.ACTION_OPEN_DOCUMENT
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                putExtra("return-data", true)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivityForResult(intent, REQUEST_OPEN_GALLERY)
        }
    }

    private fun checkSelfPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
            != PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                openFile()
            } else {
                //TODO("show Message to the user")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_OPEN_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                //This extension retrieves the path of all selected files without treatment.
                val listUri = data.getListUri()
                //Update the adapter
                originalAdapter.updateListChanged(listUri)

            }
        }
    }
}
