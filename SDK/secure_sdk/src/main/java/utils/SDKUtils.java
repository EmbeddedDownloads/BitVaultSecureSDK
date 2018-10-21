package utils;/**
 * Created by Deepak on 4/6/2017.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import bitmanagers.BitVaultBaseManager;
import commons.SDKConstants;
import commons.SecureSDKException;
import controller.Preferences;
import iclasses.AlertViewCallback;


/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public class SDKUtils {
    private static final String TAG = SDKUtils.class.getSimpleName();
    private static Toast mToast = null;

    /**
     * Show debug Message into logcat
     *
     * @param TAG
     * @param msg
     */
    public static void showLog(String TAG, String msg) {
        if (SDKConstants.IS_DEBUGGING) {
            Log.d(TAG, msg);
        }

    }

    /**
     * Show debug Error Message into logcat
     *
     * @param TAG
     * @param msg
     */
    public static void showErrorLog(String TAG, String msg) {
        if (SDKConstants.IS_DEBUGGING) {
            Log.e(TAG, msg);
        }
    }

    /***
     * This method is used to show the toast short message on the screen.
     * @param cont
     * @param message
     */
    public static void showToast(Context cont, String message) {
        if (mToast == null) {
            mToast = Toast.makeText(cont, message, Toast.LENGTH_SHORT);
        }
        if (!mToast.getView().isShown()) {
            mToast.setText(message);
            mToast.show();
        }
    }

    /***
     * This method is used to show the alert View to the user on the screen
     *
     * @param mTitle
     * @param mMessage
     * @param mPositiveBtnMsg
     * @param mNegativeBtnMsg
     * @param mAlertViewCallback
     */
    public static void alertView(final Context mContext, final String mTitle, final String mMessage, final String mPositiveBtnMsg,
                                 final String mNegativeBtnMsg, final int mCommand, final AlertViewCallback mAlertViewCallback) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

        dialog.setTitle(mTitle)
                .setMessage(mMessage)
                .setNegativeButton(mPositiveBtnMsg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.dismiss();
                        if (mAlertViewCallback != null) {
                            mAlertViewCallback.PositiveButtonPressed(mCommand);
                        }
                    }
                })
                .setPositiveButton(mNegativeBtnMsg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.dismiss();
                        if (mAlertViewCallback != null) {
                            mAlertViewCallback.NegativeButtonPressed(mCommand);
                        }
                    }
                }).show();
    }

    /***
     * This method is sued to get the name of the applications
     *
     * @param context
     * @return
     */
    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }


    /***
     * This method is used to get the unique device id
     * @return
     */
    public static String getDeviceKey() {
        String unique_app_id = "";
        if (BitVaultBaseManager.getInstance() != null) {
            try {
                if (Preferences.instance.getDeviceId() == null || Preferences.instance.getDeviceId().equalsIgnoreCase("")) {
                    Context mContext = BitVaultBaseManager.getInstance().getContext();
                    if (mContext != null) {
                        unique_app_id = Settings.Secure.getString(mContext.getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                        Preferences.instance.saveDeviceId(unique_app_id);
                    }
                } else {
                    unique_app_id = Preferences.instance.getDeviceId();
                }
            } catch (NullPointerException e) {
                unique_app_id = Preferences.instance.getDeviceId();
                return unique_app_id;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return unique_app_id;
    }

    /**
     * Check device have internet connection or not
     *
     * @param mContext
     * @return
     */
    public static boolean isNetworkAvailable(Context mContext) {
        try {
            ConnectivityManager conMgr = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            // ARE WE CONNECTED TO THE NET
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /***
     * This method is used to get the string separated with commas from the array list.
     * @param mList
     * @return
     */
    public static String getStringFromArrayListWithSeparator(ArrayList<String> mList) {
        String mFinalAddresses = "";
        try {
            int mSizeOfWallets = mList.size();
            int mCount = 0;
            for (int i = 0; i < mSizeOfWallets; i++) {
                mCount = mCount + 1;
                if (mCount < mSizeOfWallets)
                    mFinalAddresses = mFinalAddresses + mList.get(i) + ",";
                else
                    mFinalAddresses = mFinalAddresses + mList.get(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mFinalAddresses;
    }

    /***
     * This method is used to check if service is already running or not
     * @param serviceClass
     * @param mContext
     * @return
     */
    public static boolean isMyServiceRunning(Class<?> serviceClass, Context mContext) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /***
     * This method is used to get whether application id exists or not
     * @return
     */
    public static String getApplicationKey() {
        String mAppKey = "";
        try {
            ApplicationInfo ai = null;
            try {
                if (BitVaultBaseManager.getInstance().getContext() != null) {
                    Activity mActivity = (Activity) BitVaultBaseManager.getInstance().getContext();
                    ai = BitVaultBaseManager.getInstance().getContext().getPackageManager().getApplicationInfo(mActivity.getPackageName(), PackageManager.GET_META_DATA);
                    Bundle bundle = ai.metaData;
                    mAppKey = bundle.getString("app_key");
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (SecureSDKException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        }
        return mAppKey;
    }

    /***
     * This method is used to convert the string value to the double
     * @param mValue
     * @return
     */
    public static Double getDoubleFromString(String mValue) {
        Double mDouble = 0.0;
        try {
            if (mValue != null)
                mDouble = Double.parseDouble(mValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDouble;
    }
}
