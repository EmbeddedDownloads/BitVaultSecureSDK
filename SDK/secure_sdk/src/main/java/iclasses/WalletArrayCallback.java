package iclasses;

import java.util.ArrayList;

import model.WalletDetails;

/**
 * This callback class is used to keep the track of the calling classes to return the wallets
 * from the database.
 */
public interface WalletArrayCallback {
    /***
     * This method is used to get the wallets list exists in the database
     *
     * @param mRequestedWallets
     */
    public void getWallets(ArrayList<WalletDetails> mRequestedWallets);
}
