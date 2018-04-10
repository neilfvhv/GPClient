package com.gpclient.neilfvhv.www.Util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;

public class PermissionUtil {

    private static final String TAG = PermissionUtil.class.getSimpleName();

    /**
     * Code for Requesting Permission
     */
    private static final int REQUEST_PERMISSION = 9999;

    /**
     * Request Permissions
     * @param activity activity to send request
     * @param permissions permissions to be requested
     * @param callback callback after request
     */
    public static void requestPermissions(final @NonNull Activity activity,
                                          final @NonNull String[] permissions,
                                          RequestCallback callback) {
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> notGrantedPermissionsList = new ArrayList<>();
            for (String permission : permissions) {
                // check status for current permission
                int status = ActivityCompat.checkSelfPermission(activity, permission);
                // add not granted permission to list
                if (status != PackageManager.PERMISSION_GRANTED) {
                    notGrantedPermissionsList.add(permission);
                }
            }
            if (notGrantedPermissionsList.size() > 0) {
                // request not granted permissions
                ActivityCompat.requestPermissions(
                        activity,
                        notGrantedPermissionsList.toArray(
                                new String[notGrantedPermissionsList.size()]),
                        REQUEST_PERMISSION);
            } else { // no need to request permission for granted permissions
                callback.onResult();
            }
        } else { // no need to request permission for low SDK version
            callback.onResult();
        }
    }

    /**
     * Result for Requesting Permission
     * @param code code of result
     * @param callback callback after request
     */
    public static void requestResult(final @IntRange(from = 0) int code, RequestCallback callback) {
        if (code == REQUEST_PERMISSION) {
            Log.e(TAG, "Success to request permission");
            callback.onResult();
        } else {
            Log.e(TAG, "Fail to request permission");
        }
    }

    /**
     * Callback for Requesting Permission
     */
    public interface RequestCallback {
        void onResult();
    }

}
