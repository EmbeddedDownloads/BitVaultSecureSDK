package commons;/**
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
public interface SDKErrors {
    //Media Vault error messages
    String MEDIA_VAULT_SESSIONkEY = "Error: 116  Media Vault Session Key is null . ";
    String MEDIA_VAULT_ENCRYPTION_ERROR = "Error: 117  Problem in file  Encryption . ";
    String MEDIA_VAULT_SERVER_ERROR = "Error: 119  Media Vault Internal Server Error . ";
    String BITCOIN_TRANSACTION_FAILED = "Error: 120 Bitcoin Transaction failed. ";
    String MEDIA_VAULT_CALlBACK = "Error: 122  Media Vault Callback is null. ";
    String MEDIA_VAULT_RECEIVE_INPUT = "Error: 123  Sorry to receive file, input is incorrect. ";
    String MEDIA_VAULT_VOLLEY_ERROR = "Error: 124 Sorry unable to upload Media file";
    String MEDIA_VAULT_CRC_VALIDATION = "Error: 124 Error in Secure file CRC Validation";
    String RECEIVE_MEDIA_VAULT_ERROR = "Error: 125 Error in Secure file Receiving";
    String SECURE_MEDIA_FILE_SIZE = "Error: 126 Secure media file should be less than 25 mb & greater than 0 kb.";
    String MEDIA_VAULT_WALLETID = "Error: 115 Wallet Id should be greater than zero. ";
    String MEDIA_VAULT_EMPTY_MESSAGE = " Error: 108 Uploading file can't be is null. ";
    String MEDIA_VAULT_WRONG_FILEINPUT = "Error: 127 Media File details input is incorrect. ";
    String SECURE_MESSAGE_WALLETID_PRIORITY = "Error: 115 Either WalletId or Message Priority is null . ";
    String NO_UNSPENT_COUNT = "Something went wrong with your wallet, please check yours wallet.";
    String NO_ENOUGH_FUNDS = "You have not enough funds to transfer.";
    String ADDRESSES_ARRAY_LIST_NULL = "Error:101 Wallet addresses array list is null.";
    String ADDRESSES_ARRAY_LIST_EMPTY = "Error:102 Wallet addresses array list is empty.";
    String EMPTY_DATA = "";
    String WALLET_ADDRESS_EMPTY = "Error: 103 Wallet address can not be null";
    String APP_ID_NULL = "Error:104 App id can not be null.";
    String BIT_COIN_TRANSACTION_NULL = "Error: 105 Bit-coin transaction can not be null.";
    String APP_ID_EXISTS = "Error: 106 APP_ID already exists in database. ";
    String MESSAGE_FILE_SIZE = "Error: 107 Sending message size is less than zero Kb . ";
    String MESSAGE = "Error: 108 Secure message is null. ";
    String WALLET_ID_NULL = "Error: 109 Wallet id can not be null";
    String CONTEXT_NULL = "Error 110: Application context can not be null.";
    String SDK_NOT_INITIALIZED = "Error 111: Have you extended the BitVaultBaseManger Class before starting with SDK.";
    String CURRENCY_FOR_CONVERSION_NULL = "Error 112: Currency for conversion is 0 or null";
    String FINGER_PRINT_AUTH_ERROR = "Fingerprint Authentication error, Please try again";
    String FINGER_PRINT_AUTH_HELP = "Fingerprint Authentication help, Please try again";
    String FINGER_PRINT_AUTH_FAILED = "Fingerprint Authentication failed, Please try again";
    String WALLET_ID_NOT_ALLOWED = "Error: 115 Wallet id not allowed.";
    String SECURE_MESSAGE_FILE_SIZE = "Error: 107 Sending message size incorrect.File size should be less than 5 mb. ";
    String SECURE_MESSAGE = "Error: 108 Sending Secure message is null. ";
    String SECURE_MESSAGE_RECEIVED = "Error: 113 Received Secure message is null. ";
    String SECURE_MESSAGE_APPID_PRIORITY = "Error: 114 Either AppId or Message Priority is null . ";
    String USER_INVALID = "Error: 118 Access Denied, You are not valid user to access wallet.";
    String TRANSACTION_FAILED_ERROR = "Error: Transaction Failed.";
    String ADDRESSES_SAME = "Sender's address equals to recipient's address, it is likely an error.";
    String CHANGE_ADDRESSES_INVALID = "Change address is invalid";
    String SECURE_MESSAGE_SESSIONkEY = "Error: 116  Secure Message Session Key is null . ";
    String SECURE_MESSAGE_ENCRYPTION_ERROR = "Error: 117  Secure Message Problem in Encryption . ";
    String SECURE_MESSAGE_SERVER_ERROR = "Error: 119  Secure Message Internal Server Error . ";
    String SECURE_MESSAGE_BITCOIN_TRANSACTION_FAILED = "Error: 120  Secure Message Bitcoin Transaction failed. ";
    String SECURE_MESSAGE_DOWNLOAD_ERROR = "Error: 121  Secure Message Error in message downloading. ";
    String SECURE_MESSAGE_CALBACK = "Error: 122  Secure Message Callback is null. ";
    String SECURE_MESSAGE_RECEIVE_INPUT = "Error: 123  Secure Message Receive input is incorrect. ";
    String SEND_MESSAGE_VOLLEY_ERROR = "Error: 124 Sorry unable to send Message";
    String SEND_MESSAGE_CRC_VALIDATION = "Error: 124 Error in Message CRC Validation";
    String RECEIVE_MESSAGE_ERROR = "Error: 125 Error in Message Receiving";
    String SECURE_MESSAGE_PRIORITY = "Error: 114 Message Priority is null . ";
    String SECURE_MESSAGE_WALLETID = "Error: 115 Wallet Id should be greater than zero. ";
    String NOT_ENOUGH_FUNDS = "You have not enough funds to spend. Might be you have some unconfirmed transactions.";
    String RECEIVER_INVALID = "Receiver's address is invalid.";
    String SECURE_MESSAGE_SIZE = "Error: 126 Sending message size incorrect.File size should be less than 5 mb and charcater lengh between 0 to 65567";
    String FEES_CALCULATION_FAILED="Error: 129 Fees calculation failed, Please try again.";

    String WALLET_TYPE_NOT_VALID = "Error: 128 Wallet type not valid";
    String WALLET_TYPE_WRONG = "Error: 129 Wrong Wallet type";
    }
