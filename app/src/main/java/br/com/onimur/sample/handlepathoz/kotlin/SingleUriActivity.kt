/*
 * Created by Murillo Comino on 27/07/20 15:18
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 27/07/20 14:36
 */

package br.com.onimur.sample.handlepathoz.kotlin

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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.com.comino.sample.handlepathoz.R
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import kotlinx.coroutines.FlowPreview

class SingleUriActivity : AppCompatActivity(R.layout.activity_single_uri),
    HandlePathOzListener.SingleUri {

    companion object {
        const val REQUEST_PERMISSION = 123
        const val REQUEST_OPEN_GALLERY = 1111
    }

    private lateinit var buttonOpen: Button
    private lateinit var tvOriginalPath: TextView
    private lateinit var tvOriginalType: TextView
    private lateinit var tvRealPath: TextView
    private lateinit var tvRealType: TextView
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
        initTextViews()
        initProgressBar()
        initHandlePathOz()
    }

    private fun startAction() {
        startButton()
        startProgressBar()
    }

    //////////////////////////////////////     INIT    /////////////////////////////////////////////
    private fun initButton() {
        buttonOpen = findViewById(R.id.btn_open)
    }

    private fun initTextViews() {
        tvOriginalPath = findViewById(R.id.tv_original_path)
        tvOriginalType = findViewById(R.id.tv_original_type)
        tvRealPath = findViewById(R.id.tv_real_path)
        tvRealType = findViewById(R.id.tv_real_type)
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

            data?.data?.also { it ->
                //Update TextView with Original path
                tvOriginalPath.text = it.path
                tvOriginalType.text = "Unknown"

                //set uri to handle
                handlePathOz.getRealPath(it)
                //show Progress Loading
                if (!progressLoading.isShowing) {
                    progressLoading.show()
                }
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
    override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
        //Hide Progress
        if (progressLoading.isShowing or progressCancelling.isShowing) {
            progressLoading.dismiss()
            progressCancelling.dismiss()
        }
        //Update TextView with RealPath
        tvRealPath.text = pathOz.path
        tvRealType.text = pathOz.type

        //Handle Exception (Optional)
        tr?.let {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
