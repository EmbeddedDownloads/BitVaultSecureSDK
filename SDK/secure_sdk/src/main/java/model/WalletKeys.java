package model;/**
 * Created by Deepak on 4/5/2017.
 */

/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public class WalletKeys {
    /**
     * This variable is used to get the id of the generated wallet
     */
    private String wallet_id = "";
    /**
     * This variable is used to get the app id, app_id of which application
     * is requesting for creating wallet.
     */
    private String app_id = "";
    /**
     * This wallet address will be returned from the kernal layer to the application
     */
    private String wallet_address = "";
    /**
     * wallet public key is used to get the public key of the wallet
     */
    private byte[] wallet_public_key = null;
    /**
     * This object reference is used to store the private address of the wallet
     */
    private String wallet_private_key = "";


    /**
     * This method is used to get the wallet id which is created by the user
     *
     * @return: _wallet_id
     */
    public String get_wallet_id() {
        return wallet_id;
    }

    /**
     * This method is used to set the wallet id which is created by the user
     *
     * @return: _wallet_id
     */

    public void set_wallet_id(String _wallet_id) {
        this.wallet_id = _wallet_id;
    }

    /**
     * This method is used to get the application id from which application
     * request is coming.
     *
     * @return: _wallet_id
     */

    public String get_app_id() {
        return app_id;
    }

    /**
     * This method is used to set the application id from which application
     * request is coming.
     *
     * @return: _wallet_id
     */
    public void set_app_id(String _app_id) {
        this.app_id = _app_id;
    }

    /**
     * This method is used to get the wallet address of the new created wallet
     *
     * @return: _wallet_id
     */
    public String get_wallet_address() {
        return wallet_address;
    }

    /**
     * This method is used to set the wallet address of the new created wallet
     *
     * @return: _wallet_id
     */
    public void set_wallet_address(String _wallet_address) {
        this.wallet_address = _wallet_address;
    }

    /**
     * This method is used to get the wallet public key of the new created wallet
     *
     * @return: _wallet_id
     */
    public byte[] get_wallet_public_key() {
        return wallet_public_key;
    }

    /**
     * This method is used to set the wallet public key of the new created wallet
     *
     * @return: _wallet_id
     */
    public void set_wallet_public_key(byte[] wallet_public_key) {
        this.wallet_public_key = wallet_public_key;
    }

    public String getWallet_private_key() {
        return wallet_private_key;
    }

    public void setWallet_private_key(String wallet_private_key) {
        this.wallet_private_key = wallet_private_key;
    }
}
