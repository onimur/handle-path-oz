/*
 * Created by Murillo Comino on 29/07/20 17:45
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 29/07/20 17:08
 */

package br.com.onimur.sample.handlepathoz.kotlin

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.comino.sample.handlepathoz.R
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import br.com.onimur.handlepathoz.utils.extension.getListUri
import br.com.onimur.sample.handlepathoz.kotlin.adapter.RealPathAdapter
import br.com.onimur.sample.handlepathoz.kotlin.model.PathModel
import kotlinx.coroutines.FlowPreview

class MultipleUriActivity : AppCompatActivity(R.layout.activity_multiple_uri),
    HandlePathOzListener.MultipleUri {

    companion object {
        const val REQUEST_PERMISSION = 123
        const val REQUEST_OPEN_GALLERY = 1111
    }

    private var listUri = emptyList<Uri>()
    private lateinit var buttonOpen: Button
    private lateinit var rvOriginal: RecyclerView
    private lateinit var rvReal: RecyclerView
    private lateinit var originalAdapter: RealPathAdapter
    private lateinit var realAdapter: RealPathAdapter
    private lateinit var progressLoading: ProgressDialog
    private lateinit var progressCancelling: ProgressDialog
    private lateinit var handlePathOz: HandlePathOz

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //
        init()
        startAction()
        //
    }

    private fun init() {
        initButton()
        initRecyclerView()
        initAdapter()
        initProgressBar()
        initHandlePathOz()

    }

    private fun startAction() {
        startButton()
        startRecyclerView()
        startProgressBar()
    }

    //////////////////////////////////////     INIT    /////////////////////////////////////////////
    private fun initButton() {
        buttonOpen = findViewById(R.id.btn_open)
    }

    private fun initRecyclerView() {
        rvOriginal = findViewById(R.id.lv_original)
        rvReal = findViewById(R.id.lv_real)
    }

    private fun initAdapter() {
        originalAdapter = RealPathAdapter(ArrayList())
        realAdapter = RealPathAdapter(ArrayList())
    }

    private fun initProgressBar() {
        progressLoading = ProgressDialog(this, getString(R.string.validating)).apply {
            setCancelable(true)
            create()
        }

        progressCancelling = ProgressDialog(this, getString(R.string.cancelling)).apply {
            setCancelable(false)
            create()
        }
    }

    private fun initHandlePathOz() {
        //initialize library
        handlePathOz = HandlePathOz(this, this)
    }

    //////////////////////////////////////     START    ////////////////////////////////////////////
    private fun startButton() {
        buttonOpen.setOnClickListener { openFile() }
    }

    private fun startRecyclerView() {
        rvOriginal.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = LinearLayoutManager(context)
            // specify an viewAdapter (see also next example)
            adapter = originalAdapter
        }

        rvReal.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = realAdapter
        }
    }

    private fun startProgressBar() {
        progressLoading.setOnCancelListener {
            //Call progress to cancel task
            if (!progressCancelling.isShowing) {
                progressCancelling.show()
            }
            //cancelTask
            handlePathOz.cancelTask()

        }
    }

    //////////////////////////////////////     OTHER METHODS   /////////////////////////////////////
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

    /////////////////////////////     OVERRIDE METHODS    //////////////////////////////////////////
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                openFile()
            } else {
                TODO("show Message to the user")
            }
        }
    }

    @FlowPreview
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_OPEN_GALLERY) and (resultCode == Activity.RESULT_OK)) {
            //This extension retrieves the path of all selected files without treatment.
            listUri = data.getListUri()
            //Update the adapter

            originalAdapter.updateListChanged(listUri.map {
                PathModel(
                    PathOz(
                        "unknown",
                        it.path.toString()
                    )
                )
            })

            //set list of the Uri to handle
            //in concurrency use:
            // 1                -> for tasks sequentially
            //greater than 1    -> for the number of tasks you want to perform in parallel.
            //Nothing           -> for parallel tasks - by default the value is 10
            handlePathOz.getListRealPath(listUri)
            // handlePathOz.getListRealPath(listUri, 1)
            //show Progress Loading
            if (!progressLoading.isShowing) {
                progressLoading.show()
            }
        }
    }

    override fun onBackPressed() {
        with(handlePathOz) {
            //Cancel the task if it is working.
            cancelTask()
            //Deletes temporary files
            deleteTemporaryFiles()
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        with(handlePathOz) {
            onDestroy()
            deleteTemporaryFiles()
        }
        super.onDestroy()
    }


    /////////////////////////////     LISTENER HANDLE PATH OZ    ///////////////////////////////////
    override fun onRequestHandlePathOz(listPathOz: List<PathOz>, tr: Throwable?) {
        //Hide Progress
        if (progressLoading.isShowing or progressCancelling.isShowing) {
            progressLoading.dismiss()
            progressCancelling.dismiss()
        }
        //Update the adapter
        realAdapter.updateListChanged(listPathOz.map { pathOz ->
            PathModel(pathOz)
        })

        //Handle Exception (Optional)
        tr?.let {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    //This method is Optional
    override fun onLoading(currentUri: Int) {
        progressLoading.currentLoad = "${currentUri}/${listUri.size}"
    }
}
