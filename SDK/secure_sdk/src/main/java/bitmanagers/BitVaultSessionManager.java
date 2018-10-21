package bitmanagers;//package bitmanagers;/**
// * Created by ${e} on 5/29/2017.
// */
//
//import android.app.ActivityManager;
//import android.content.Context;
//import android.content.Intent;
//import android.os.CountDownTimer;
//
//import com.embedded.wallet.BitVaultActivity;
//import com.embedded.wallet.R;
//
//import java.util.List;
//
//import commons.SDKConstants;
//import commons.SDKHelper;
//import utils.SDKUtils;
//
///**********************************************************************
// * Embedded Downloads
// * All rights reserved.
// * This software is the confidential and proprietary information of
// * Embedded Downloads. ("Confidential Information"). You shall not
// * disclose such Confidential Information and shall use it only in
// * accordance with the terms of the license agreement you entered into
// * with Embedded Downloads.
// ********************************************************************/
//public class BitVaultSessionManager {
//    private Context mContext = null;
//    private static boolean userValidation = false;
//    private String TAG = BitVaultSessionManager.class.getSimpleName();
//    private static BitVaultSessionManager mBitVaultSessionManagerInstance = null;
//
//    public void initializeSessionManager(Context mContext) {
//        this.mContext = mContext;
//        userSessionValidator();
//    }
//
//    public boolean isUserValidation() {
//        return userValidation;
//    }
//
//    private void setUserValidation(boolean userValidation) {
//        this.userValidation = userValidation;
//    }
//
//    /***
//     * This method is used to return the instance of this class
//     * @return
//     */
//    public static BitVaultSessionManager getSessionInstance() {
//        if (mBitVaultSessionManagerInstance == null)
//            mBitVaultSessionManagerInstance = new BitVaultSessionManager();
//        return mBitVaultSessionManagerInstance;
//    }
//
//    /****
//     * This method is used to validate the session of the user which is
//     * maintain for validate the user.
//     */
//    private void userSessionValidator() {
//        try {
//            new CountDownTimer(SDKHelper.SESSION_TIMER, SDKHelper.SESSION_SECONDS) {
//                public void onTick(long millisUntilFinished) {
//                    setUserValidation(true);
//                }
//
//                public void onFinish() {
//                    SDKUtils.showToast(mContext, mContext.getResources().getString(R.string.session_timeout));
//                    setUserValidation(false);
//                    if (mContext != null) {
//                        SDKConstants.IN_INTERNAL_AUTH = true;
//                        authenticateUser(mContext);
//                    }
//                }
//            }.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /***
//     * This method is used to authenticate the user and shown the first UI screens.
//     * @param mContext
//     */
//    public void authenticateUser(Context mContext) {
//        if (isAppOnForeground(mContext)) {
//            mContext.startActivity(new Intent(mContext, BitVaultActivity.class));
//        }
//    }
//
//    /***
//     * This method is used to check if application is in background or foreground mode.
//     *
//     * @param context
//     * @return
//     */
//    private boolean isAppOnForeground(Context context) {
//        try {
//            if (context != null) {
//                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//                List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
//                if (appProcesses == null) {
//                    return false;
//                }
//                final String packageName = context.getPackageName();
//                for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
//                    if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
//                        return true;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//}
