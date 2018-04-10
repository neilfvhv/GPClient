package com.gpclient.neilfvhv.www.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.gpclient.neilfvhv.www.Bean.User;
import com.gpclient.neilfvhv.www.R;
import com.gpclient.neilfvhv.www.Util.OkHttpUtil;
import com.gpclient.neilfvhv.www.Util.UriUtil;
import com.tasomaniac.android.widget.DelayedProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.blurry)
    ImageView blurryImageView;
    @BindView(R.id.gallery)
    ImageButton galleryImageButton;
    @BindView(R.id.camera)
    ImageButton cameraImageButton;
    @BindView(R.id.submit)
    ImageButton submitImageButton;

    DelayedProgressDialog dialog;

    Uri imageUri;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    /**
     * Initialize for MainActivity
     */
    private void initialize() {
        Log.e(TAG, "MainActivity Initialized");
        // bind view
        ButterKnife.bind(this);
        // initialize ProgressDialog - loading dialog when the server is processing image
        dialog = DelayedProgressDialog.make(
                this, "Processing", "please wait for several minutes");
        // delay to show the dialog - 0.5s
        dialog.setMinDelay(500);
        // minimum time to show the dialog - 1s
        dialog.setMinShowTime(1000);
    }

    @OnClick({R.id.gallery, R.id.camera, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.gallery:
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent, CHOOSE_PICTURE);
                break;
            case R.id.camera:
                // set image path
                imagePath = pictureRootPath + "/IMG_" + System.currentTimeMillis() + ".jpeg";
                // create new file
                File file = new File(imagePath);
                // get URI for new file
                imageUri = Uri.fromFile(file);
                Intent cameraIntent = new Intent();
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                        this, "com.gpclient.neilfvhv.www", file));
                startActivityForResult(cameraIntent, TAKE_PICTURE);
                break;
            case R.id.submit:
                if (imageUri != null) {
                    // get image path from uri
                    String path = UriUtil.getPathFromUri(
                            MainActivity.this, imageUri, UriUtil.IMAGE);
                    // start the progress dialog
                    dialog.show();
                    // upload image
                    uploadImage(path, new User("neilfvhv", "123456"));
                }
                break;
        }
    }

    private void uploadImage(String path, User user) {
        // compress image file
        File file = compressImage(path, 50, 10);
        // build request
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", user.getUsername())
                .addFormDataPart("password", user.getPassword())
                .addFormDataPart("image", file.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();
        // execute requesting
        OkHttpUtil.call(request, new OkHttpUtil.OkHttpCallback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String stackTrace = e.toString();
                // back to UI thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        // cancel the progress dialog
                        dialog.cancel();
                        Toast.makeText(MainActivity.this, stackTrace,
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(final int code) {
                // back to UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // cancel the progress dialog
                        dialog.cancel();
                        String message;
                        switch (code) {
                            case 401:
                                message = "No Authority";
                                break;
                            case 500:
                                message = "Internal Server Error";
                                break;
                            default:
                                message = "Unknown Error";
                        }
                        Toast.makeText(MainActivity.this, message,
                                Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onSuccess(Call call, Response response) {
                InputStream is;
                FileOutputStream fos;
                try {
                    is = response.body().byteStream();
                    fos = new FileOutputStream(downloadTempImage);
                    byte[] bytes = new byte[512];
                    int len;
                    while ((len = is.read(bytes)) != -1) {
                        fos.write(bytes, 0, len);
                    }
                    fos.flush();
                    // back to UI thread
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // cancel the progress dialog
                            dialog.cancel();
                            // clear MainActivity for next using
                            imagePath = null;
                            imageUri = null;
                            blurryImageView.setImageURI(null);
                            // start ResultActivity
                            Intent intent = new Intent(
                                    MainActivity.this, ResultActivity.class);
                            startActivity(intent);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CHOOSE_PICTURE) {
                // get uri
                imageUri = data.getData();
            } else if (requestCode == TAKE_PICTURE) {
                // save image to gallery
                saveImageToGallery(imagePath);
            }
            // set blurry image
            blurryImageView.setImageURI(imageUri);
        }
    }

}
