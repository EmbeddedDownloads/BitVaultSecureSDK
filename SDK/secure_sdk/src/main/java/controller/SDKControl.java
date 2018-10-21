package controller;/**
 * Created by Deepak on 4/6/2017.
 */

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.os.SystemClock;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.bitcoinj.crypto.MnemonicCode;

import java.io.IOException;

import eotutil.Fonts;
import eotutil.LinuxSecureRandom;
import webservicescontroller.LruBitmapCache;


/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public class SDKControl extends Application {

    public final String TAG = SDKControl.class
            .getSimpleName();
    private int SOCKET_TIMEOUT = 30000;//30 seconds - change to what you want

    private RequestQueue mRequestQueue = null;
    private ImageLoader mImageLoader;
    public static SDKControl mInstance;
    private PackageInfo packageInfo;
    private ActivityManager activityManager;
    private ConnectivityManager connManager;
    private int lastStop;


    @Override
    public void onCreate() {
        mInstance = this;
        new LinuxSecureRandom(); // init proper random number generator
        // TODO review this
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().detectAll().permitDiskReads().permitDiskWrites().penaltyLog().build());
        super.onCreate();
        packageInfo = packageInfoFromContext(this);
        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        // Set MnemonicCode.INSTANCE if needed
        if (MnemonicCode.INSTANCE == null) {
            try {
                MnemonicCode.INSTANCE = new MnemonicCode();
            } catch (IOException e) {
                throw new RuntimeException("Could not set MnemonicCode.INSTANCE", e);
            }
        }
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Fonts.initFonts(this.getAssets());
    }

    public boolean isConnected() {
        NetworkInfo activeInfo = connManager.getActiveNetworkInfo();
        return activeInfo != null && activeInfo.isConnected();
    }
    public PackageInfo packageInfo() {
        return packageInfo;
    }

    public void touchLastResume() {
        lastStop = -1;
    }

    public void touchLastStop() {
        lastStop = (int) SystemClock.elapsedRealtime();
    }

    public long getLastStop() {
        return lastStop;
    }



    /***
     * This method is used to get the packge name of the application
     * @param context
     * @return
     */
    private PackageInfo packageInfoFromContext(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException x) {
            throw new RuntimeException(x);
        }
    }


    public static synchronized SDKControl getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        setRetryPolicy(req);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        setRetryPolicy(req);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    private <T> void setRetryPolicy(Request<T> req) {
        RetryPolicy policy = new DefaultRetryPolicy(SOCKET_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
    }
}
