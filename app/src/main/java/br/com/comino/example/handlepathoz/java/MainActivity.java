/*
 * Created by Murillo Comino on 17/06/20 13:18
 * Github: github.com/onimur
 * StackOverFlow: pt.stackoverflow.com/users/128573
 * Email: murillo_comino@hotmail.com
 *
 *  Copyright (c) 2020.
 *  Last modified 15/06/20 20:06
 */

package br.com.comino.example.handlepathoz.java;

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

import br.com.comino.example.handlepathoz.R;
import br.com.comino.example.handlepathoz.kotlin.ListUriAdapter;
import br.com.comino.example.handlepathoz.kotlin.ProgressDialog;
import br.com.comino.handlepathoz.HandlePathOz;
import br.com.comino.handlepathoz.HandlePathOzListener;
import kotlin.Pair;

import static android.content.Intent.ACTION_PICK;
import static android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI;
import static br.com.comino.handlepathoz.utils.extension.PathsKt.getListUri;

public class MainActivity extends AppCompatActivity implements HandlePathOzListener {

    private static final int REQUEST_PERMISSION = 123;
    private static final int REQUEST_OPEN_GALLERY = 1111;

    private List<Uri> listUri;
    private Button buttonOpen;
    private RecyclerView rvOriginal;
    private RecyclerView rvReal;
    private ListUriAdapter originalAdapter;
    private ListUriAdapter realAdapter;
    private ProgressDialog progressLoading;
    private ProgressDialog progressCancelling;
    private HandlePathOz handlePathOz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        originalAdapter = new ListUriAdapter(new ArrayList<>());
        realAdapter = new ListUriAdapter(new ArrayList<>());
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
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
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
            originalAdapter.updateListChanged(listUri);

            //set list of the Uri to handle
            handlePathOz.getRealPath(listUri);
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
        handlePathOz.cancelTask();
        handlePathOz.deleteTemporaryFiles();
        super.onDestroy();
    }

    /////////////////////////////     LISTENER HANDLE PATH OZ    ///////////////////////////////////
    @Override
    public void onRequestHandlePathOz(@NotNull List<Pair<Integer, String>> listPath, @Nullable Throwable tr) {
        //Hide Progress
        if (progressLoading.isShowing() || progressCancelling.isShowing()) {
            progressLoading.dismiss();
            progressCancelling.dismiss();
        }

        //Update the adapter
        List<Uri> listUri = new ArrayList<>();
        for (int i = 0; i < listPath.size(); i++) {
            Uri uri = Uri.parse(listPath.get(i).getSecond());
            listUri.add(uri);
        }
        realAdapter.updateListChanged(listUri);

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
