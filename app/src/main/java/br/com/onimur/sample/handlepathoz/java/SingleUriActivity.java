/*
 * Created by Murillo Comino on 27/07/20 15:18
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 27/07/20 15:16
 */

package br.com.onimur.sample.handlepathoz.java;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import br.com.comino.sample.handlepathoz.R;
import br.com.onimur.handlepathoz.HandlePathOz;
import br.com.onimur.handlepathoz.HandlePathOzListener;
import br.com.onimur.handlepathoz.model.PathOz;
import br.com.onimur.sample.handlepathoz.kotlin.ProgressDialog;

import static android.content.Intent.ACTION_PICK;
import static android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI;

public class SingleUriActivity extends AppCompatActivity implements HandlePathOzListener.SingleUri {

    private static final int REQUEST_PERMISSION = 123;
    private static final int REQUEST_OPEN_GALLERY = 1111;

    private Button buttonOpen;
    private TextView tvOriginalPath;
    private TextView tvOriginalType;
    private TextView tvRealPath;
    private TextView tvRealType;
    private ProgressDialog progressLoading;
    private ProgressDialog progressCancelling;
    private HandlePathOz handlePathOz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_uri);

        init();
        startAction();
    }

    private void init() {
        initButton();
        initTextView();
        initProgressBar();
        initHandlePathOz();
    }

    private void startAction() {
        startButton();
        startProgressBar();
    }

    //////////////////////////////////////     INIT    /////////////////////////////////////////////
    private void initButton() {
        buttonOpen = findViewById(R.id.btn_open);
    }

    private void initTextView() {
        tvOriginalPath = findViewById(R.id.tv_original_path);
        tvOriginalType = findViewById(R.id.tv_original_type);
        tvRealPath = findViewById(R.id.tv_real_path);
        tvRealType = findViewById(R.id.tv_real_type);
    }

    private void initProgressBar() {
        progressLoading = new ProgressDialog(this, getString(R.string.validating));
        progressLoading.setCancelable(true);
        progressLoading.create();

        progressCancelling = new ProgressDialog(this, getString(R.string.cancelling));
        progressCancelling.setCancelable(false);
        progressCancelling.create();
    }

    private void initHandlePathOz() {
        //initialize library
        handlePathOz = new HandlePathOz(this, this);
    }

    //////////////////////////////////////     START    ////////////////////////////////////////////
    private void startButton() {
        buttonOpen.setOnClickListener(v -> openFile());
    }

    private void startProgressBar() {
        progressLoading.setOnCancelListener(dialog -> {
            //Call progress to cancel task
            if (!progressCancelling.isShowing()) {
                progressCancelling.show();
            }
            //cancelTask
            handlePathOz.cancelTask();
        });
    }

    //////////////////////////////////////     OTHER METHODS   /////////////////////////////////////
    private void openFile() {
        if (checkSelfPermission()) {
            Intent intent;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                intent = new Intent(ACTION_PICK, EXTERNAL_CONTENT_URI);
            } else {
                intent = new Intent(ACTION_PICK, INTERNAL_CONTENT_URI);
            }

            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra("return-data", true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivityForResult(intent, REQUEST_OPEN_GALLERY);
        }
    }

    private boolean checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            return false;
        }
        return true;
    }

    /////////////////////////////     OVERRIDE METHODS    //////////////////////////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFile();
            } else {
                //TODO("show Message to the user")
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                //Update the TextView with Original Path
                tvOriginalPath.setText(uri.getPath());
                tvOriginalType.setText("Unknown");
                //set Uri to handle
                handlePathOz.getRealPath(uri);
            }
            //show Progress Loading
            if (!progressLoading.isShowing()) {
                progressLoading.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //Cancel the task if it is working.
        handlePathOz.cancelTask();
        //Deletes temporary
        handlePathOz.deleteTemporaryFiles();

        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        handlePathOz.onDestroy();
        handlePathOz.deleteTemporaryFiles();
        super.onDestroy();
    }

    /////////////////////////////     LISTENER HANDLE PATH OZ    ///////////////////////////////////
    @Override
    public void onRequestHandlePathOz(@NotNull PathOz pathOz, @Nullable Throwable tr) {
        //Hide Progress
        if (progressLoading.isShowing() || progressCancelling.isShowing()) {
            progressLoading.dismiss();
            progressCancelling.dismiss();
        }
        //Update the TextView with Real Path
        tvRealPath.setText(pathOz.getPath());
        tvRealType.setText(pathOz.getType());

        //Handle Exception (Optional)
        if (tr != null) {
            Toast.makeText(this, tr.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
