package com.delhi6.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_CHOOSER_REQUEST = 1001;

    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;
    private Uri cameraImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwipeRefreshLayout refresh = new SwipeRefreshLayout(this);
        webView = new WebView(this);

        refresh.addView(webView);
        setContentView(refresh);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                    WebView view,
                    ValueCallback<Uri[]> callback,
                    FileChooserParams params) {

                if (filePathCallback != null) {
                    filePathCallback.onReceiveValue(null);
                }

                filePathCallback = callback;
                cameraImageUri = null;

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setType("image/*");

                Intent cameraIntent =
                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        File imageFile = createCameraImageFile();

                        cameraImageUri = FileProvider.getUriForFile(
                                MainActivity.this,
                                getPackageName() + ".fileprovider",
                                imageFile
                        );

                        cameraIntent.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                cameraImageUri
                        );

                        cameraIntent.addFlags(
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );

                        cameraIntent.addFlags(
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        );

                    } catch (IOException e) {
                        cameraImageUri = null;
                    }
                }

                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(
                        Intent.EXTRA_INTENT,
                        galleryIntent
                );

                if (cameraImageUri != null) {
                    chooser.putExtra(
                            Intent.EXTRA_INITIAL_INTENTS,
                            new Intent[]{cameraIntent}
                    );
                }

                startActivityForResult(
                        chooser,
                        FILE_CHOOSER_REQUEST
                );

                return true;
            }
        });

        refresh.setOnRefreshListener(() -> {
            webView.reload();
            refresh.setRefreshing(false);
        });

        webView.loadUrl(
                "https://delhi-6.pages.dev/admin.html"
        );
    }

    private File createCameraImageFile() throws IOException {

        String timestamp = new SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.US
        ).format(new Date());

        File storageDir = getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
        );

        return File.createTempFile(
                "DELHI6_" + timestamp + "_",
                ".jpg",
                storageDir
        );
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode != FILE_CHOOSER_REQUEST
                || filePathCallback == null) {
            return;
        }

        Uri[] results = null;

        if (resultCode == RESULT_OK) {

            if (data != null && data.getData() != null) {

                results = new Uri[]{
                        data.getData()
                };

            } else if (cameraImageUri != null) {

                results = new Uri[]{
                        cameraImageUri
                };
            }
        }

        filePathCallback.onReceiveValue(results);

        filePathCallback = null;
        cameraImageUri = null;
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
