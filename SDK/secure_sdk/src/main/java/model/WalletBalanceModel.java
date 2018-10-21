package model;/**
 * Created by ${e} on 5/26/2017.
 */

import java.util.ArrayList;

/**********************************************************************
 * Embedded Downloads
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * Embedded Downloads. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Embedded Downloads.
 ********************************************************************/
public class WalletBalanceModel {
    private String mWalletAddress = "";
    private String mWalletBalance = "";
    private String mWalletTotalReceivedBitCoins = "";
    private String mWalletTotalSpentBitCoins = "";
    private String mWalletUnconfirmedBalance = "";
    private ArrayList<String> mTransactionsList=null;
    private int mWalletId=0;

    public String getmWalletAddress() {
        return mWalletAddress;
    }

    public void setmWalletAddress(String mWalletAddress) {
        this.mWalletAddress = mWalletAddress;
    }

    public String getmWalletBalance() {
        return mWalletBalance;
    }

    public void setmWalletBalance(String mWalletBalance) {
        this.mWalletBalance = mWalletBalance;
    }

    public String getmWalletTotalReceivedBitCoins() {
        return mWalletTotalReceivedBitCoins;
    }

    public void setmWalletTotalReceivedBitCoins(String mWalletTotalReceivedBitCoins) {
        this.mWalletTotalReceivedBitCoins = mWalletTotalReceivedBitCoins;
    }

    public String getmWalletTotalSpentBitCoins() {
        return mWalletTotalSpentBitCoins;
    }

    public void setmWalletTotalSpentBitCoins(String mWalletTotalSpentBitCoins) {
        this.mWalletTotalSpentBitCoins = mWalletTotalSpentBitCoins;
    }

    public String getmWalletUnconfirmedBalance() {
        return mWalletUnconfirmedBalance;
    }

    public void setmWalletUnconfirmedBalance(String mWalletUnconfirmedBalance) {
        this.mWalletUnconfirmedBalance = mWalletUnconfirmedBalance;
    }

    public ArrayList<String> getmTransactionsList() {
        return mTransactionsList;
    }

    public void setmTransactionsList(ArrayList<String> mTransactionsList) {
        this.mTransactionsList = mTransactionsList;
    }

    public int getmWalletId() {
        return mWalletId;
    }

    public void setmWalletId(int mWalletId) {
        this.mWalletId = mWalletId;
    }
}
