package com.embedded.wallet;

import org.junit.Test;

import bitmanagers.BitVaultWalletManager;
import commons.SecureSDKException;
import iclasses.UserAuthenticationCallback;
import utils.SDKUtils;

import static bitmanagers.BitVaultWalletManager.getWalletInstance;

/**
 * Created by ${e} on 6/22/2017.
 */

public class MyJTesting extends BitVaultActivity implements UserAuthenticationCallback {
    @Test
    public void testAuthentication() {
        validateUser(this, this);
    }

    @Override
    public void onAuthenticationSuccess() {
//        startTestingWallets();
    }

    @Test
    public void startTestingWallets() {
        if (getWalletInstance() != null) {
            try {
                String mAddress = BitVaultWalletManager.getWalletInstance().getWalletAddress(1);
                SDKUtils.showLog("Test..", "Wallet Address : " + mAddress);
            } catch (SecureSDKException e) {
                e.printStackTrace();
            }
        }
    }
}
