package com.gpclient.neilfvhv.www.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.gpclient.neilfvhv.www.R;
import com.gpclient.neilfvhv.www.Util.FileUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResultActivity extends BaseActivity {

    private final static String TAG = ResultActivity.class.getSimpleName();

    @BindView(R.id.latent)
    ImageView latentImageView;
    @BindView(R.id.save)
    ImageButton saveImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initialize();
    }

    private void initialize() {
        Log.e(TAG, "ResultActivity Initialized");
        // bind view
        ButterKnife.bind(this);
        // set latent image
        latentImageView.setImageURI(Uri.fromFile(new File(downloadTempImage)));
    }

    @OnClick(R.id.save)
    public void onViewClicked() {
        String path = pictureRootPath + "/DEBLUR_" + System.currentTimeMillis() + ".jpeg";
        FileUtil.copyFile(downloadTempImage,path);
        saveImageToGallery(path);
        File file = new File(downloadTempImage);
        if(file.delete()) {
            finish();
        }
    }

}
