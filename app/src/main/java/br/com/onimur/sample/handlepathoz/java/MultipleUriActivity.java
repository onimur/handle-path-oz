/*
 * Created by Murillo Comino on 29/07/20 17:45
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 29/07/20 17:08
 */

package br.com.onimur.sample.handlepathoz.java;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.comino.sample.handlepathoz.R;
import br.com.onimur.handlepathoz.HandlePathOz;
import br.com.onimur.handlepathoz.HandlePathOzListener;
import br.com.onimur.handlepathoz.model.PathOz;
import br.com.onimur.sample.handlepathoz.kotlin.ProgressDialog;
import br.com.onimur.sample.handlepathoz.kotlin.adapter.RealPathAdapter;
import br.com.onimur.sample.handlepathoz.kotlin.model.PathModel;

import static android.content.Intent.ACTION_PICK;
import static android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI;
import static br.com.onimur.handlepathoz.utils.extension.PathsKt.getListUri;

public class MultipleUriActivity extends AppCompatActivity implements HandlePathOzListener.MultipleUri {

    private static final int REQUEST_PERMISSION = 123;
    private static final int REQUEST_OPEN_GALLERY = 1111;

    private List<Uri> listUri;
    private Button buttonOpen;
    private RecyclerView rvOriginal;
    private RecyclerView rvReal;
    private RealPathAdapter originalAdapter;
    private RealPathAdapter realAdapter;
    private ProgressDialog progressLoading;
    private ProgressDialog progressCancelling;
    private HandlePathOz handlePathOz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_uri);

        init();
        startAction();
    }

    private void init() {
        initButton();
        initRecyclerView();
        initAdapter();
        initProgressBar();
        initHandlePathOz();
    }

    private void startAction() {
        startButton();
        startRecyclerView();
        startProgressBar();
    }

    //////////////////////////////////////     INIT    /////////////////////////////////////////////
    private void initButton() {
        buttonOpen = findViewById(R.id.btn_open);
    }

    private void initRecyclerView() {
        rvOriginal = findViewById(R.id.lv_original);
        rvReal = findViewById(R.id.lv_real);
    }

    private void initAdapter() {
        originalAdapter = new RealPathAdapter(new ArrayList<>());
        realAdapter = new RealPathAdapter(new ArrayList<>());
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

    private void startRecyclerView() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvOriginal.setHasFixedSize(true);
        // use a linear layout manager
        rvOriginal.setLayoutManager(new LinearLayoutManager(this));
        // specify an viewAdapter (see also next example)
        rvOriginal.setAdapter(originalAdapter);
        //
        //
        rvReal.setHasFixedSize(true);
        rvReal.setLayoutManager(new LinearLayoutManager(this));
        rvReal.setAdapter(realAdapter);
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
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
            //This extension retrieves the path of all selected files without treatment.
            listUri = getListUri(data);
            //Update the adapter
            List<PathModel> listPath = new ArrayList<>();
            for (int i = 0; i < listUri.size(); i++) {
                String path = listUri.get(i).getPath();
                PathOz pathOz = new PathOz("unknown", Objects.requireNonNull(path));
                PathModel pathModel = new PathModel(pathOz);
                listPath.add(pathModel);
            }
            originalAdapter.updateListChanged(listPath);

            //set list of the Uri to handle
            //in concurrency use:
            // 1                -> for tasks sequentially
            //greater than 1    -> for the number of tasks you want to perform in parallel.
            //Nothing           -> for parallel tasks - by default the value is 10
            handlePathOz.getListRealPath(listUri);
            // handlePathOz.getListRealPath(listUri, 1)
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
    public void onRequestHandlePathOz(@NotNull List<PathOz> listPath, @Nullable Throwable tr) {
        //Hide Progress
        if (progressLoading.isShowing() || progressCancelling.isShowing()) {
            progressLoading.dismiss();
            progressCancelling.dismiss();
        }

        //Update the adapter
        List<PathModel> listPathModel = new ArrayList<>();
        for (int i = 0; i < listPath.size(); i++) {
            PathOz pathOz = listPath.get(i);
            PathModel pathModel = new PathModel(pathOz);
            listPathModel.add(pathModel);
        }
        realAdapter.updateListChanged(listPathModel);

        //Handle Exception (Optional)
        if (tr != null) {
            Toast.makeText(this, tr.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //This method is Optional
    @Override
    public void onLoading(int currentUri) {
        progressLoading.setCurrentLoad(currentUri + "/" + listUri.size());
    }
}
