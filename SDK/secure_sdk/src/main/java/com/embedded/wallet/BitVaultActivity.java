package com.embedded.wallet;/**
 * Created by Deepak on 5/16/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Stack;

import bitmanagers.AuthenticationManager;
import commons.SDKConstants;
import fragments.FingerPrintAuth;
import fragments.IRISAuth;
import fragments.NFCPasswordAuth;
import iclasses.AlertViewCallback;
import iclasses.FingerPrintManagerCallbacks;
import iclasses.UserAuthenticationCallback;
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
 public class BitVaultActivity extends FragmentActivity implements
        FingerPrintManagerCallbacks, AlertViewCallback {
    private static Context mContext = null;
    private String TAG = BitVaultActivity.class.getSimpleName();
    private int CURRENT_AUTH = 0;
    private UserAuthenticationCallback mUserAuthenticationCallback = null;
    private boolean isUserValidated = false;
    private int WRITE_REQUEST_CODE = 1;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    public static Activity mActivity = null;
    private FrameLayout bit_vault_container = null;
    private Stack<Fragment> mSliderFragmentsStack = new Stack<Fragment>();
    private int ZERO = 0, ONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bit_vault_activity);
        mActivity = BitVaultActivity.this;
        if (SDKConstants.IN_INTERNAL_AUTH)
            validateFingerPrint();
    }

    /***
     * Check for device if its mi device
     */
    private void checkIfDeviceMI() {
        String manufacturer = "xiaomi";
        if(manufacturer.equalsIgnoreCase(Build.MANUFACTURER)) {
            //this will open auto start screen where user can enable permission for your app
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent);
        }
    }

    /***
     * This method is used to take validation from the user
     *
     * @param mContext
     */
    public void validateUser(Context mContext, UserAuthenticationCallback mUserAuthenticationCallback) {
        this.mContext = mContext;
        AuthenticationManager.getInstance().initializeAuthenticationManager(mContext);
        this.mUserAuthenticationCallback = mUserAuthenticationCallback;
        validateFingerPrint();
    }

    /***
     * This method is used to validate the finger print of the user
     */
    private void validateFingerPrint() {
        pushFragment(new FingerPrintAuth());
        startFingerPrintSensorScanning();
    }

    /***
     * This method is used to start scanning from the sensor of the finger print
     */
    private void startFingerPrintSensorScanning() {
        AuthenticationManager.getInstance().authenticationFingerPrint(this);
    }


    /**
     * This method is used to perform the action while user click on the next button
     *
     * @param v
     */
    public void showNfcSecurity(View v) {
        validateNfcAuth();
    }

    /***
     * This method is just for dummy right now when all the authentication has been completed
     *
     * @param v
     */
    public void authComplete(View v) {
        checkRunTimePermission();
    }

    /***
     * This method is used to check the permission of the external storage.
     */
    private void checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        } else {
            userIsValidated();
        }
    }

    /**
     * Check the validation of the user if valid user is accessing the sdk method or not
     */
    private void userIsValidated() {
        if (SDKConstants.IN_INTERNAL_AUTH) {
            SDKConstants.IN_INTERNAL_AUTH = false;
            finish();
        }
//        SDKUtils.alertView(this,SDKUtils.getApplicationName(mContext),getResources().getString(R.string.permission_msg),
//                getResources().getString(R.string.yes),getResources().getString(R.string.no),ONE,this);
        if (mUserAuthenticationCallback != null) {
            mUserAuthenticationCallback.onAuthenticationSuccess();
        }
    }


    /**
     * Push Fragment into fragment Stack and show top fragment to GUI
     */
    private void pushFragment(Fragment mFragment) {
        try {
            if (mActivity != null && !mActivity.isFinishing()) {
                // Begin the transaction
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.bit_vault_container, mFragment);
                // Commit the transaction
                transaction.commitAllowingStateLoss();
                // add fragment into flow stack
                mSliderFragmentsStack.add(mFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Maintain the track of slider fragment items
     *
     * @return
     */
    private Fragment getTopFragment() {
        Fragment mtopRemovingFragemnt = null;
        try {
            if (mSliderFragmentsStack.size() != 0) {
                mtopRemovingFragemnt = mSliderFragmentsStack.get(mSliderFragmentsStack
                        .size() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mtopRemovingFragemnt;
    }

    /**
     * Remove All Old Attached fragment
     */
    private void removeAllOldAttachedFragment() {
        try {
            if (mSliderFragmentsStack != null && !mSliderFragmentsStack.isEmpty()) {
                /* Old fragments Available */
                for (int i = 0; i < mSliderFragmentsStack.size(); i++) {
                    popFragment(mSliderFragmentsStack.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Pop Fragment from fragment stack and remove latest one and show Pervious
     * attached fragment
     */
    private void popFragment(Fragment mFragment) {
        try {
            if (mActivity != null && !mActivity.isFinishing()) {
                // Begin the transaction
                FragmentTransaction ft = getSupportFragmentManager()
                        .beginTransaction();
                // remove fragment from view
                if (mFragment != null) {
                    ft.remove(mFragment);
                    ft.detach(mFragment);
                } else {
                    SDKUtils.showLog(TAG,
                            "onPop framgent Fragment not found");
                }
                ft.commitAllowingStateLoss();
                getSupportFragmentManager().popBackStack();
                mSliderFragmentsStack.pop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                try {
                    if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        userIsValidated();
                    } else {
                        SDKUtils.alertView(this, this.getResources().getString(R.string.app_name),
                                this.getResources().getString(R.string.denied_db_permission),
                                this.getResources().getString(R.string.yes), this.getResources().getString(R.string.no), ZERO, this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    /***
     * This method is used to validate the NFC authentication
     */
    private void validateNfcAuth() {
        removeAllOldAttachedFragment();
        pushFragment(new NFCPasswordAuth());
        CURRENT_AUTH = 2;
    }

    /**
     * This method is used to pick context from the calling application
     */
    public void sdkStartWith(Context mContext) {
        this.mContext = mContext;
        AuthenticationManager.getInstance().initializeAuthenticationManager(mContext);
    }

    @Override
    public void onBackPressed() {
        try {
            if (CURRENT_AUTH == 1) {
                if (CURRENT_AUTH <= 1)
                    CURRENT_AUTH = CURRENT_AUTH - 1;
                validateFingerPrint();
            } else if (CURRENT_AUTH == 2) {
                if (CURRENT_AUTH <= 1)
                    CURRENT_AUTH = CURRENT_AUTH - 1;
                validateIris();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        validateIris();
    }

    /**
     * This method is used validate the IRIS of the user
     */
    public void validateIris() {
        CURRENT_AUTH = 1;
        removeAllOldAttachedFragment();
        pushFragment(new IRISAuth());
    }

    @Override
    public void onAuthenticationFailed() {
        startFingerPrintSensorScanning();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
    }

    @Override
    public void PositiveButtonPressed(int mCommand) {
        finish();
    }

    @Override
    public void NegativeButtonPressed(int mCommand) {
        if (mCommand == ZERO)
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        if (mCommand==ONE)
            checkIfDeviceMI();

    }
}
