package bitmanagers;/**
 * Created by Deepak on 5/16/2017.
 */

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;

import authentication.FingerPrintScanner;
import commons.SDKConstants;
import commons.SDKErrors;
import commons.SecureSDKException;
import controller.Preferences;
import iclasses.FingerPrintManagerCallbacks;
import iclasses.TransactionFeesCalculator;
import iclasses.basemanagercallbacks.IRISCallback;
import iclasses.basemanagercallbacks.NFCAuthCallback;
import utils.SDKUtils;

/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public abstract class BitVaultBaseManager implements FingerPrintManagerCallbacks {
    private static Context mContext = null;
    private String TAG = BitVaultBaseManager.class.getSimpleName();
    private static boolean isUserValidated = false;
    private static BitVaultBaseManager mBitVaultBaseManagerInstance = null;
    private FingerPrintManagerCallbacks mFingerPrintManagerCallbacks = null;
    public String mTransactionId="";
    public TransactionFeesCalculator mTransactionFeesCalculator=null;
//    private static BitVaultSessionManager mSessionManager=null;

    public static BitVaultBaseManager getInstance() {
        if (mBitVaultBaseManagerInstance == null)
            mBitVaultBaseManagerInstance = new BitVaultBaseManager() {
            };
        return mBitVaultBaseManagerInstance;
    }


    BitVaultBaseManager() {
    }

    public BitVaultBaseManager(Context mContext) {
        this.mContext = mContext;
        saveAppKeyForApplications();
    }

    /***
     * This method is used to save the app key of the client application
     */
    private void saveAppKeyForApplications() {
        if (Preferences.instance != null && mContext != null) {
            String mAppKey = SDKUtils.getApplicationKey();
            Preferences.instance.setAppKey(mAppKey);
        }
    }

    public void initializeBaseManager(Context mContext) {
        this.mContext = mContext;
    }

    public void authenticationFingerPrint(FingerPrintManagerCallbacks mFingerPrintManagerCallbacks) {
        this.mFingerPrintManagerCallbacks = mFingerPrintManagerCallbacks;
        if (mContext != null)
            new FingerPrintScanner(mContext, this).startUserAuthProcess(mContext);
        else
            throw new NullPointerException(SDKErrors.CONTEXT_NULL);
    }

    public void authenticationIRIS(IRISCallback mIRISCallback) {

    }

    public void authenticationNFCCallback(NFCAuthCallback mNFCCallback) {

    }


    /***
     * This method is used to return the context of the calling application
     *
     * @return
     * @throws SecureSDKException
     */
    public Context getContext() throws SecureSDKException {
        return mContext;
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
//        mSessionManager  = new BitVaultSessionManager();
//        mSessionManager.initializeSessionManager(mContext);
        SDKUtils.showLog(TAG, "------onAuthenticationSucceeded---------");
        isUserValidated = true;
        subscribeForNotifications();
        if (mFingerPrintManagerCallbacks != null) {
            mFingerPrintManagerCallbacks.onAuthenticationSucceeded(result);
        }
    }


    @Override
    public void onAuthenticationFailed() {
        if (mFingerPrintManagerCallbacks != null) {
            mFingerPrintManagerCallbacks.onAuthenticationFailed();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        if (mFingerPrintManagerCallbacks != null) {
            mFingerPrintManagerCallbacks.onAuthenticationHelp(helpMsgId, helpString);
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (mFingerPrintManagerCallbacks != null) {
            mFingerPrintManagerCallbacks.onAuthenticationError(errMsgId, errString);
        }
    }

    /***
     * This method is used to check whether user is valid or not
     *
     * @return
     */
    public boolean getUserValid() {
//        if (mSessionManager!=null)
//            this.isUserValid = mSessionManager.isUserValidation();
        return isUserValidated;
    }

    /***
     * This method is used to subscribe the notifications
     */
    private void subscribeForNotifications() {
        try {
            if (Preferences.instance != null && Preferences.instance.ISDATABASEEXISTS() &&
                    !SDKConstants.isSubscribedForNotification) {
                SDKUtils.showErrorLog(TAG
                        , "----------Now registering for wallets-----------");
                try {
                    new BitVaultNotificationManager().subscribeNotifications(getContext());
                } catch (SecureSDKException e) {
                    SDKConstants.isSubscribedForNotification = false;
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
