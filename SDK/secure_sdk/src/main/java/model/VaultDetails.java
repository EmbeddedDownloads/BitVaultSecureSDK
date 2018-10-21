package model;/**
 * Created by Deepak on 5/2/2017.
 */

import java.io.Serializable;

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
public class VaultDetails implements Serializable{
    /**
     * String variable used to hold the wallet name
     */
    private String VAULT_NAME="";
    /**
     * byte array to hold the wallet icon
     */
    private byte[] VAULT_ICON=null;
    /**
     * This string variable is used to hold the last updated balance of the wallet
     */
    private String VAULT_LAST_UPDATE_BALANCE="0.0";
    /**
     * Used to hold at what time the last balance was updated
     */
    private String VAULT_UPDATE_TIME="";

    public String getVAULT_NAME() {
        return VAULT_NAME;
    }

    public void setVAULT_NAME(String VAULT_NAME) {
        this.VAULT_NAME = VAULT_NAME;
    }

    public byte[] getVAULT_ICON() {
        return VAULT_ICON;
    }

    public void setVAULT_ICON(byte[] VAULT_ICON) {
        this.VAULT_ICON = VAULT_ICON;
    }

    public String getVAULT_LAST_UPDATE_BALANCE() {
        return VAULT_LAST_UPDATE_BALANCE;
    }

    public void setVAULT_LAST_UPDATE_BALANCE(String VAULT_LAST_UPDATE_BALANCE) {
        this.VAULT_LAST_UPDATE_BALANCE = VAULT_LAST_UPDATE_BALANCE;
    }

    public String getVAULT_UPDATE_TIME() {
        return VAULT_UPDATE_TIME;
    }

    public void setVAULT_UPDATE_TIME(String VAULT_UPDATE_TIME) {
        this.VAULT_UPDATE_TIME = VAULT_UPDATE_TIME;
    }
    private KeyPair mKeyPair = null;

    public KeyPair getmKeyPair() {
        return mKeyPair;
    }

    public void setmKeyPair(KeyPair mKeyPair) {
        this.mKeyPair = mKeyPair;
    }
}
