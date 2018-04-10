package com.gpclient.neilfvhv.www.Util;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtil {

    private static OkHttpUtil mInstance;
    private OkHttpClient mOkHttpClient;

    /**
     * Constructor for OkHttpUtil
     */
    private OkHttpUtil() {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        // set connect timeout
        okHttpBuilder.connectTimeout(5, TimeUnit.SECONDS);
        // set read timeout
        okHttpBuilder.readTimeout(5, TimeUnit.MINUTES);
        // set write timeout
        okHttpBuilder.writeTimeout(5, TimeUnit.MINUTES);
        // build OkHttpClient
        mOkHttpClient = okHttpBuilder.build();
    }

    /**
     * Singleton for OkHttpUtil
     *
     * @return the only instance for OkHttpUtil
     */
    private static OkHttpUtil getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtil.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * Specific Call for this Application (No Generic Target)
     * @param request request fot HTTP
     * @param callback callback for the request
     */
    public static void call(Request request, final OkHttpCallback callback) {
        getInstance().mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.code() == 200) {
                    callback.onSuccess(call, response);
                } else {
                    callback.onError(response.code());
                }
            }
        });
    }

    /**
     * Callback for OkHttpUtil
     */
    public interface OkHttpCallback {
        void onFailure(Call call, IOException e);
        void onError(int code);
        void onSuccess(Call call, Response response);
    }
}