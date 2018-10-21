package model.eotmodel;

import valle.btc.EotKeyPair;

/**
 * Created by vvdn on 10/12/2017.
 */

public class EotCoinsTransferModel {
    private String receivers_address = "";
    private String sender_wallet_address = "";
    private long amount_to_send = 0;
    private long priority_fee = 0;
    private EotKeyPair mWalletKey = null;

    public String getReceivers_address() {
        return receivers_address;
    }

    public void setReceivers_address(String receivers_address) {
        this.receivers_address = receivers_address;
    }

    public String getSender_wallet_address() {
        return sender_wallet_address;
    }

    public void setSender_wallet_address(String sender_wallet_address) {
        this.sender_wallet_address = sender_wallet_address;
    }

    public EotKeyPair getmWalletKey() {
        return mWalletKey;
    }

    public void setmWalletKey(EotKeyPair mWalletKey) {
        this.mWalletKey = mWalletKey;
    }

    public long getAmount_to_send() {
        return amount_to_send;
    }

    public void setAmount_to_send(long amount_to_send) {
        this.amount_to_send = amount_to_send;
    }

    public long getPriority_fee() {
        return priority_fee;
    }

    public void setPriority_fee(long priority_fee) {
        this.priority_fee = priority_fee;
    }
}
