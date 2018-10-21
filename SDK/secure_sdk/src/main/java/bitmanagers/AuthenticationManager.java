package bitmanagers;/**
 * Created by ${e} on 5/26/2017.
 */

import android.content.Context;

import iclasses.FingerPrintManagerCallbacks;
import iclasses.basemanagercallbacks.IRISCallback;
import iclasses.basemanagercallbacks.NFCAuthCallback;

/**********************************************************************
 * Embedded Downloads
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * Embedded Downloads. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Embedded Downloads.
 ********************************************************************/
public abstract class AuthenticationManager extends BitVaultBaseManager {
    protected static AuthenticationManager mAuthenticationManagerInstance = null;

    public static AuthenticationManager getInstance() {
        if (mAuthenticationManagerInstance == null)
            mAuthenticationManagerInstance = new AuthenticationManager() {
            };
        return mAuthenticationManagerInstance;
    }

    public void initializeAuthenticationManager(Context mContext) {
        initializeBaseManager(mContext);
    }

    @Override
    public void authenticationFingerPrint(FingerPrintManagerCallbacks mFingerPrintManagerCallbacks) {
        super.authenticationFingerPrint(mFingerPrintManagerCallbacks);
    }

    @Override
    public void authenticationIRIS(IRISCallback mIRISCallback) {
        super.authenticationIRIS(mIRISCallback);
    }

    @Override
    public void authenticationNFCCallback(NFCAuthCallback mNFCCallback) {
        super.authenticationNFCCallback(mNFCCallback);
    }

    @Override
    public boolean getUserValid() {
        return super.getUserValid();
    }
}
