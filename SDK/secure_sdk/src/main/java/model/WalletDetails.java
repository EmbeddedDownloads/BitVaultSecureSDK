package model;/**
 * Created by Deepak on 5/2/2017.
 */

import java.io.Serializable;

import valle.btc.EotKeyPair;
import valle.btc.KeyPair;

/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public class WalletDetails implements Serializable{
    /**
     * String variable to hold application id
     */
    private String APP_ID="";
    /**
     * Wallet id
     */
    private String WALLET_ID="";
    /**
     * String variable used to hold the wallet name
     */
    private String WALLET_NAME="";
    /**
     * byte array to hold the wallet icon
     */
    private byte[] WALLET_ICON=null;
    /**
     * This string variable is used to hold the last updated balance of the wallet
     */
    private String WALLET_LAST_UPDATE_BALANCE="0.0";
    /**
     * Used to hold at what time the last balance was updated
     */
    private String WALLET_UPDATE_TIME="";
    /**
     * Used for temp purpose meanwhile in the case of secure zone unavailbility
     */
    private KeyPair mKeyPair=null;
    private EotKeyPair mEotKeyPair=null;

    public String getAPP_ID() {
        return APP_ID;
    }

    public void setAPP_ID(String APP_ID) {
        this.APP_ID = APP_ID;
    }

    public String getWALLET_ID() {
        return WALLET_ID;
    }

    public void setWALLET_ID(String WALLET_ID) {
        this.WALLET_ID = WALLET_ID;
    }

    public KeyPair getmKeyPair() {
        return mKeyPair;
    }

    public void setmKeyPair(KeyPair mKeyPair) {
        this.mKeyPair = mKeyPair;
    }


    public String getWALLET_NAME() {
        return WALLET_NAME;
    }

    public void setWALLET_NAME(String WALLET_NAME) {
        this.WALLET_NAME = WALLET_NAME;
    }

    public byte[] getWALLET_ICON() {
        return WALLET_ICON;
    }

    public void setWALLET_ICON(byte[] WALLET_ICON) {
        this.WALLET_ICON = WALLET_ICON;
    }

    public String getWALLET_LAST_UPDATE_BALANCE() {
        return WALLET_LAST_UPDATE_BALANCE;
    }

    public void setWALLET_LAST_UPDATE_BALANCE(String WALLET_LAST_UPDATE_BALANCE) {
        this.WALLET_LAST_UPDATE_BALANCE = WALLET_LAST_UPDATE_BALANCE;
    }

    public String getWALLET_UPDATE_TIME() {
        return WALLET_UPDATE_TIME;
    }

    public void setWALLET_UPDATE_TIME(String WALLET_UPDATE_TIME) {
        this.WALLET_UPDATE_TIME = WALLET_UPDATE_TIME;
    }

    public EotKeyPair getmEotKeyPair() {
        return mEotKeyPair;
    }

    public void setmEotKeyPair(EotKeyPair mEotKeyPair) {
        this.mEotKeyPair = mEotKeyPair;
    }
}
