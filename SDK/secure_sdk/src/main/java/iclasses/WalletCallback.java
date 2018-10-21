package iclasses;

import model.WalletDetails;

/**
 * Created by Deepak on 5/2/2017.
 */

public interface WalletCallback {
    /***
     * This method is used to hold the wallet according to the wallet id and return the
     * wallet object from the database.
     *
     * @param mWalletDetails
     */
    public void getWallet(WalletDetails mWalletDetails);
}
