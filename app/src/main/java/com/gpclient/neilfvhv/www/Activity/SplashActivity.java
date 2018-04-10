package com.gpclient.neilfvhv.www.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gpclient.neilfvhv.www.R;
import com.gpclient.neilfvhv.www.Util.PermissionUtil;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private PermissionUtil.RequestCallback requestCallback = new PermissionUtil.RequestCallback() {
        @Override
        public void onResult() {
            initialize();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // check & request permission before initializing
        PermissionUtil.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCallback);
    }

    /**
     * Initialize for SplashActivity
     */
    private void initialize() {
        Log.e(TAG, "SplashActivity Initialized");
        // remove top bar
        removeTopBar();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // sleep for waiting
                    Thread.sleep(3 * 1000);
                    // start MainActivity
                    Intent intent = new Intent(
                            SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    // destroy SplashActivity
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        // consume events
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // callback for requesting permission
        PermissionUtil.requestResult(requestCode, requestCallback);
    }
}
