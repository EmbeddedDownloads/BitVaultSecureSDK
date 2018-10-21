package model;

import iclasses.TransactionBuilder;

/**
 * Created by ${e} on 6/9/2017.
 */

public class RequestRetryModel {
    private int mWalletIdFromWhichDataToSend;
    private String outputAddress="";
    private long amountToSend=0, extraFee=0;
    private TransactionBuilder mTransactionBuilder=null;

    public int getmWalletIdFromWhichDataToSend() {
        return mWalletIdFromWhichDataToSend;
    }

    public void setmWalletIdFromWhichDataToSend(int mWalletIdFromWhichDataToSend) {
        this.mWalletIdFromWhichDataToSend = mWalletIdFromWhichDataToSend;
    }

    public String getOutputAddress() {
        return outputAddress;
    }

    public void setOutputAddress(String outputAddress) {
        this.outputAddress = outputAddress;
    }

    public long getAmountToSend() {
        return amountToSend;
    }

    public void setAmountToSend(long amountToSend) {
        this.amountToSend = amountToSend;
    }

    public long getExtraFee() {
        return extraFee;
    }

    public void setExtraFee(long extraFee) {
        this.extraFee = extraFee;
    }

    public TransactionBuilder getmTransactionBuilder() {
        return mTransactionBuilder;
    }

    public void setmTransactionBuilder(TransactionBuilder mTransactionBuilder) {
        this.mTransactionBuilder = mTransactionBuilder;
    }
}
