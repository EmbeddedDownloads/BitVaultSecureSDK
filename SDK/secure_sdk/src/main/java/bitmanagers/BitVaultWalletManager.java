package bitmanagers;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import com.android.volley.VolleyError;
import com.embedded.wallet.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import commons.ForegroundCheckTask;
import commons.SDKConstants;
import commons.SDKErrors;
import commons.SDKHelper;
import commons.SecureSDKException;
import database.DatabaseHandler;
import iclasses.BroadcastBitCoinTransactionHandler;
import iclasses.TransactionBuilder;
import iclasses.TransactionFeesCalculator;
import iclasses.TransactionHistoryCallback;
import iclasses.VaultDetailsCallback;
import iclasses.WalletArrayCallback;
import iclasses.WalletBalanceCallback;
import iclasses.WalletCallback;
import iclasses.WalletType;
import iclasses.WalletUnspentCountHandler;
import model.RequestRetryModel;
import model.VaultDetails;
import model.WalletBalanceModel;
import model.WalletDetails;
import utils.SDKUtils;
import valle.btc.BTCUtils;
import valle.btc.BitcoinException;
import valle.btc.KeyPair;
import valle.btc.Transaction;
import valle.btc.UnspentOutputInfo;
import webservicescontroller.BitCoinTransactionBroadcast;
import webservicescontroller.FetchUpdatedWalletBalance;
import webservicescontroller.GetAllTransactionsHistoryHandler;
import webservicescontroller.MultipleWalletsUnspentCount;
import webservicescontroller.TransactionsHistoryHandler;
import webservicescontroller.WalletUnspentCount;
import webservicescontroller.eotservices.EOTCoinTransactionBroadcast;


/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public final class BitVaultWalletManager extends BitVaultBaseManager implements
        BroadcastBitCoinTransactionHandler, WalletBalanceCallback, WalletUnspentCountHandler {
    /**
     * This class object is used to get the wallet class object reference
     */
    private static BitVaultWalletManager mBitVaultWalletManagerInstance = null;
    private String TAG = BitVaultWalletManager.class.getSimpleName();
    /**
     * Application id of the app, which is requesting for creating the wallet
     */
    private String app_id = "";
    /**
     * The wallet address will be generated after creating the wallet
     */
    private String wallet_address = "";
    /**
     * The public key address of the wallet
     */
    private String wallet_public_key = "";
    private KeyPair mKeyPair = null;
    /**
     * This wallet details class object is used to hold the wallet details after generating
     * or getting from database.
     */
    private WalletDetails mWalletDetails = null;
    /**
     * This async class object is used to generate raw transaction
     */
    private AsyncTask<Void, Void, GenerateTransactionResult> generateTransactionTask = null;
    /**
     * This class object is used to keep track for unspent bit coins
     */
    private ArrayList<UnspentOutputInfo> unspentOutputs = new ArrayList<>();
    /**
     * This string class variable is used to hold the raw transaction for unspent
     * bit coins
     */
    private String mRawTransaction = null;
    /**
     * This transaction builder class is used to build the transaction
     */
    private TransactionBuilder mTransactionBuilder = null;
    /**
     * This class interface object is used to hold the wallet details
     */
    private WalletCallback mWalletCallback = null;
    /**
     * This method is used to set callback to the calling methods
     */
    private WalletArrayCallback mWalletArrayCallback = null;
    /**
     * This database class object is used to handle database operations
     */
    private DatabaseHandler mDatabaseHandler = null;
    private static Context mContext = null;
    private int NO_OF_WALLETS = 5;
    private RequestRetryModel mRetryRequestModel = null;
    private GenerateTransactionResult mTransactionResult = null;
    private ArrayList<JSONArray> mUnspentAllCounts = new ArrayList<>();
    private HashMap<String, ArrayList<UnspentOutputInfo>> mUnspentCountsSeperateData = null;
    private static int emptyWalletCount = 1;
    private boolean isEmptyAllWalletsInitialized = false;
    private long optionalExtraFee = 0;
    private long emptyAllWalletsToVaultExtraFees = 0;


    /**
     * This wallet constructor is used to get initialize the wallet class
     */
    private BitVaultWalletManager() {
        try {
            createBitVaultDatabase();
            mContext = getContext();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDatabaseHandler = new DatabaseHandler(mContext);
        verifyDatabaseAndGenerateWallets();
        subscribeForNotifications();
    }

    /***
     * This method is used to create the database directory inside the external storage.
     */
    private void createBitVaultDatabase() {
        try {
            File mFilename = new File(SDKHelper.SECURE_SDK_PATH + "/" + SDKHelper.SDK_DB_DIR_NAME);
            if (!mFilename.exists()) {
                mFilename.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method is used to get the instance of this class, to access
     * the methods of wallet class
     *
     * @return
     */
    public static BitVaultWalletManager getWalletInstance() {
        if (mBitVaultWalletManagerInstance == null)
            mBitVaultWalletManagerInstance = new BitVaultWalletManager();
        SDKConstants.CURRENT_RETRY_COUNT = 0;
        SDKConstants.WALLET_TYPE = 0;
        emptyWalletCount = 1;
        return mBitVaultWalletManagerInstance;
    }

    /***
     * This method is used to check whether wallet exists already on database
     */
    private void verifyDatabaseAndGenerateWallets() {
        try {
            if (mDatabaseHandler != null) {
                boolean isDatabaseExist = mDatabaseHandler.verifySecureDatabase();
                int mSizeOfWallets = mDatabaseHandler.getAllWallets().size();
                if (isDatabaseExist && mSizeOfWallets == 0) {
                    for (int i = 0; i <= NO_OF_WALLETS; i++) {
                        generateWallet();
                        generateWalletKeys(mContext.getPackageName(), i);
                        checkWalletBalance(i);
                    }
                } else {
                    checkWalletBalance(-1);
                }
            } else {
                SDKUtils.showLog(TAG, "database handler is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * This method is used to subscribe the notifications
     */
    private void subscribeForNotifications() {
        if (!SDKConstants.isSubscribedForNotification) {
            try {
                new BitVaultNotificationManager().subscribeNotifications(getContext());
            } catch (SecureSDKException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * This method is used to update the balance in the wallet
     *
     * @param mWalletId
     */
    private void checkWalletBalance(final int mWalletId) {
        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mWalletId != -1) {
                    updateWalletBalance(mWalletId);
                } else {
                    WalletDetails mVaultDetails = mDatabaseHandler.getWalletWithId(SDKConstants.VAULT_ID);
                    ArrayList<WalletDetails> mWalletsList = mDatabaseHandler.getAllWallets();
                    mWalletsList.add(mVaultDetails);
                    for (int i = 0; i < mWalletsList.size(); i++) {
                        updateWalletBalance(Integer.parseInt(mWalletsList.get(i).getWALLET_ID()));
                    }
                }
            }
        }, SDKConstants.WALLET_BALANCE_UPDATE_INITIAL_DELAY, SDKConstants.WALLET_BALANCE_TIMER);
    }

    /***
     * Update the balance in the wallet.
     *
     * @param mWalletId
     */
    private void updateWalletBalance(int mWalletId) {
        try {
            getWalletBalance(mWalletId, this);
        } catch (SecureSDKException e) {
            e.printStackTrace();
        }
    }

    /***
     * This method is used to get history of the wallet whatever the transactions performed by the wallet
     *
     * @param wallet_id
     * @param mTransactionHistoryCallback
     * @throws SecureSDKException
     */

    public void getTransactionsHistory(int wallet_id, TransactionHistoryCallback mTransactionHistoryCallback) throws SecureSDKException {
        if (wallet_id < 0)
            throw new NullPointerException(SDKErrors.APP_ID_NULL);
        if (getUserValid()) {
            String mAddress = getWalletAddress(wallet_id);
            if (mContext != null && SDKUtils.isNetworkAvailable(mContext)) {
                new TransactionsHistoryHandler(mTransactionHistoryCallback).getHistory(mAddress);
            } else {
                showInternetError();
            }
        } else {
            showAuthenticationScreen();
        }
    }

    /***
     * This method is used to get history of the wallet whatever the transactions performed by the wallet
     * @param mTransactionHistoryCallback
     * @throws SecureSDKException
     */

    public void getVaultTransactionsHistory(TransactionHistoryCallback mTransactionHistoryCallback) throws SecureSDKException {
        if (getUserValid()) {
            String mAddress = getWalletAddress(SDKConstants.VAULT_ID);
            if (mContext != null && SDKUtils.isNetworkAvailable(mContext)) {
                new TransactionsHistoryHandler(mTransactionHistoryCallback).getHistory(mAddress);
            } else {
                showInternetError();
            }
        } else {
            showAuthenticationScreen();
        }
    }

    /***
     * This method is used to get history of the all wallets whatever the transactions performed by the wallet
     *
     * @param mTransactionHistoryCallback
     * @throws SecureSDKException
     */

    public void getTransactionsHistory(TransactionHistoryCallback mTransactionHistoryCallback) throws SecureSDKException {
        if (getUserValid()) {
            ArrayList<String> mWalletsAddressList = new ArrayList<>();
            if (mDatabaseHandler != null) {
                ArrayList<WalletDetails> mWalletDetails = mDatabaseHandler.getAllWallets();
                for (int i = 0; i < mWalletDetails.size(); i++) {
                    mWalletsAddressList.add(mWalletDetails.get(i).getmKeyPair().address);
                }
            }
            if (mContext != null && SDKUtils.isNetworkAvailable(mContext)) {
                new GetAllTransactionsHistoryHandler(mTransactionHistoryCallback).getHistory(mWalletsAddressList);
            } else {
                showInternetError();
            }
        } else {
            showAuthenticationScreen();
        }
    }

    /***
     * This method is used to get the address of the server to get the data from
     *
     * @param wallet_address
     */
    private String getServerUrl(String wallet_address) {
        String mInsightUrl = "";
        if (SDKConstants.WALLET_TYPE == SDKHelper.ZERO)
            mInsightUrl = SDKHelper.TESTNET_URL_TRANSACTION_HISTORY + wallet_address + SDKHelper.TX_HISTORY_NO_OF_PAGES;
        else
            mInsightUrl = SDKHelper.MAIN_TRANSACTION_HISTORY + wallet_address + SDKHelper.TX_HISTORY_NO_OF_PAGES;
        return mInsightUrl;
    }

    /***
     * This method is used to authenticate the user if session expires
     */
    private void showAuthenticationScreen() {
        try {
//            if (BitVaultSessionManager.getSessionInstance() != null) {
//                BitVaultSessionManager.getSessionInstance().authenticateUser(mContext);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method of Wallet class is used to get the balance of the wallet address
     *
     * @param wallet_id
     */
    private void getWalletUnspentCount(int wallet_id,
                                       WalletUnspentCountHandler mWalletUnspentCountHandler) throws SecureSDKException {
        if (wallet_id < 0)
            throw new NullPointerException(SDKErrors.APP_ID_NULL);
        if (getUserValid()) {
            if (mDatabaseHandler != null) {
                // Call the API from the insight server and get the balance info the particular
                String mAddress = getWalletAddress(wallet_id);
                if (mContext != null) {
                    if (SDKUtils.isNetworkAvailable(mContext)) {
                        new WalletUnspentCount(mWalletUnspentCountHandler).checkMyWallet(mAddress);
                    } else {
                        showInternetError();
                    }
                }
            }
        } else {
            showAuthenticationScreen();
        }
    }


    /****
     * This method is used to get the balance of the wallet address
     *
     * @param wallet_id
     * @param mWalletBalanceCallback
     */
    public void getWalletBalance(int wallet_id, WalletBalanceCallback mWalletBalanceCallback) throws SecureSDKException {
        validateWalletId(wallet_id);
        String mWalletAddress = "";
        try {
            if (getUserValid()) {
                mWalletAddress = getWalletAddress(wallet_id);
                callWalletBalnaceUpdate(mWalletBalanceCallback, mWalletAddress, wallet_id);
            } else {
                showAuthenticationScreen();
            }
        } catch (SecureSDKException e) {
            e.printStackTrace();
        }
    }

    /***
     * This method is used to get the update wallet balance
     *
     * @param mWalletBalanceCallback
     * @param mWalletAddress
     * @param wallet_id
     */
    private void callWalletBalnaceUpdate(WalletBalanceCallback mWalletBalanceCallback, String mWalletAddress, int wallet_id) {
        if (mContext != null && SDKUtils.isNetworkAvailable(mContext)) {
            new FetchUpdatedWalletBalance(mWalletBalanceCallback).checkWalletForUpdateBalance(mWalletAddress, wallet_id);
        } else {
            showInternetError();
        }
    }

    /***
     * This method is used to show if internet is not available on the user's device
     */
    private void showInternetError() {
        // Use like this:
        try {
            if (mContext != null) {
                final boolean isForegroud = new ForegroundCheckTask().execute(mContext).get();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (mTransactionBuilder != null) {
                            String mNetworkError = mContext.getResources().getString(R.string.network_error);
                            mTransactionBuilder.TransactionFailed(mNetworkError);
                        }
                        if (mContext != null && isForegroud)
                            SDKUtils.showToast(mContext, mContext.getResources().getString(R.string.network_error));
                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to get the balance for multiple wallets
     *
     * @param wallet_addresses
     * @throws SecureSDKException
     */
    public void getMultipleWalletsUnSpentCount(ArrayList<String> wallet_addresses, WalletUnspentCountHandler mWalletUnspentCountHandler) throws SecureSDKException {
        if (wallet_addresses == null)
            throw new NullPointerException(SDKErrors.ADDRESSES_ARRAY_LIST_EMPTY);
        if (wallet_addresses.isEmpty())
            throw new NullPointerException(SDKErrors.ADDRESSES_ARRAY_LIST_EMPTY);
        for (int i = 0; i < wallet_addresses.size(); i++) {
            String wallet_address = wallet_addresses.get(i); // get the balance of this wallet address
            new WalletUnspentCount(mWalletUnspentCountHandler).checkMyWallet(wallet_address);
        }
    }

    /***
     * This method is used to create all the wallets and store the wallet if inside
     * the local database.
     *
     * @return
     * @throws SecureSDKException
     */
    public ArrayList<WalletDetails> getWallets(WalletArrayCallback mWalletArrayCallback) throws SecureSDKException {
        this.mWalletArrayCallback = mWalletArrayCallback;
        ArrayList<WalletDetails> mRequestedWallets = new ArrayList<>();
        String app_id = getApp_id();
        app_id = "abc";
        validateAppId(app_id);
        mRequestedWallets = mDatabaseHandler.getAllWallets();
        if (getUserValid()) {
            if (mWalletArrayCallback != null) {
                mWalletArrayCallback.getWallets(mRequestedWallets);
            }
        } else {
            showAuthenticationScreen();
        }
        return mRequestedWallets;
    }

    /***
     * This method is used to update the name of the wallet corresponding to the wallet id
     *
     * @param wallet_id
     * @param wallet_name
     * @return
     */
    public int updateWalletName(String wallet_id, String wallet_name) {
        int isNameUpdated = 0;
        if (getUserValid()) {
            if (mDatabaseHandler != null) {
                isNameUpdated = mDatabaseHandler.updateWalletName(wallet_id, wallet_name);
            }
        } else {
            showAuthenticationScreen();
        }
        return isNameUpdated;
    }

    /***
     * This method is used to update the wallet icon, If user want to set some
     * custom icon for wallet
     *
     * @param wallet_id
     * @param mNewIcon
     * @return
     */
    public int updateWalletIcon(String wallet_id, byte[] mNewIcon) {
        int isNameUpdated = 0;
        if (getUserValid()) {
            if (mDatabaseHandler != null) {
                isNameUpdated = mDatabaseHandler.updateWalletIcon(wallet_id, mNewIcon);
            }
        } else {
            showAuthenticationScreen();
        }
        return isNameUpdated;
    }

    /***
     * This method is used to get the wallet details corresponding to a wallet
     * @param mWallet_Id
     * @param wallet_type
     * @param mWalletCallback
     * @throws SecureSDKException
     */
    public void getWallet(final int mWallet_Id, final int wallet_type, WalletCallback mWalletCallback) throws SecureSDKException {
        if (mWallet_Id == 0 || mWallet_Id < 0)
            throw new RuntimeException(SDKErrors.WALLET_ID_NOT_ALLOWED);
        verifyWalletType(wallet_type);
        this.mWalletCallback = mWalletCallback;
        WalletDetails mWalletDetails = null;
        String app_id = getApp_id();
        app_id = "abc";
        validateAppId(app_id);
        boolean isWalletExists = false;
        try {
            isWalletExists = checkIfWalletAlreadyExists(mWallet_Id);
        } catch (SecureSDKException e) {
            e.printStackTrace();
        }
        // Validate the user if particular user is valid or not
        if (getUserValid()) {
            if (!isWalletExists) {
                // Call native method to create wallet from the kernal layer
                generateWallet();
                generateWalletKeys(mContext.getPackageName(), mWallet_Id);
            } else {
                mWalletDetails = mDatabaseHandler.getWalletWithId(mWallet_Id);
                if (mWalletCallback != null)
                    mWalletCallback.getWallet(mWalletDetails);
            }
        } else {
            showAuthenticationScreen();
        }
    }

    /***
     * This method is used to verify the wallet type requested by the client application
     * @param wallet_type
     */
    private void verifyWalletType(int wallet_type) {
        if (wallet_type == WalletType.BIT_COIN_TESTNET || wallet_type == WalletType.BITCOIN_MAIN) {
            SDKUtils.showLog(TAG, "---Wallet Type is valid----" + wallet_type);
            SDKConstants.WALLET_TYPE = wallet_type;
        } else
            throw new RuntimeException(SDKErrors.WALLET_TYPE_NOT_VALID);
    }

    /***
     * This method is used to return the vault to the user
     */
    public void getVault(VaultDetailsCallback mVaultDetailsCallback) {
        VaultDetails mVaultDetails = new VaultDetails();
        if (mDatabaseHandler != null) {
            WalletDetails mWalletDetails = mDatabaseHandler.getWalletWithId(SDKConstants.VAULT_ID);
            if (mWalletDetails != null) {
                mVaultDetails.setVAULT_NAME(mWalletDetails.getWALLET_NAME());
                mVaultDetails.setVAULT_LAST_UPDATE_BALANCE(mWalletDetails.getWALLET_LAST_UPDATE_BALANCE());
                mVaultDetails.setVAULT_UPDATE_TIME(mWalletDetails.getWALLET_UPDATE_TIME());
                mVaultDetails.setVAULT_ICON(mWalletDetails.getWALLET_ICON());
                mVaultDetails.setmKeyPair(mWalletDetails.getmKeyPair());
            }
        }
        if (mVaultDetailsCallback != null)
            mVaultDetailsCallback.vaultDetails(mVaultDetails);
    }


    /***
     * This method is used to check if particular wallet id exists in the database or not
     *
     * @param mWallet_id
     * @return
     */
    private boolean checkIfWalletAlreadyExists(int mWallet_id) throws SecureSDKException {
        boolean isWalletExists = false;
        try {
            if (mWallet_id == -1)
                throw new NullPointerException(SDKErrors.WALLET_ID_NULL);
            if (mDatabaseHandler != null) {
                isWalletExists = mDatabaseHandler.isWalletAlreadyExistsForAppId(mWallet_id + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isWalletExists;
    }

    /**
     * This method is used to get the public key of the wallet corresponding
     * to the application id and wallet id.
     *
     * @param wallet_id
     * @return : _public_key of the wallet
     */
    public byte[] getWalletPublicKey(final int wallet_id) throws SecureSDKException {
        String app_id = getApp_id();
        byte[] wallet_public_key = null;
        validateAppId(app_id);
        // Get the public key of the wallet and return back to the calling user.
        try {
            verifyWalletId(wallet_id);
            if (getUserValid()) {
                if (mDatabaseHandler != null) {
                    WalletDetails mWalletDetails = mDatabaseHandler.getWalletWithId(wallet_id);
                    wallet_public_key = mWalletDetails.getmKeyPair().publicKey;
                }
            } else {
                showAuthenticationScreen();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wallet_public_key;
    }

    /***
     * This method is used to verify the wallet id if anything wrong
     *
     * @param mWallet_id
     * @throws SecureSDKException
     */
    private void verifyWalletId(int mWallet_id) throws SecureSDKException {
        if (mWallet_id == -1)
            throw new NullPointerException(SDKErrors.WALLET_ID_NULL);
    }

    /**
     * The method is used to get the wallet address of particular wallet.
     *
     * @param wallet_id
     * @return
     * @throws SecureSDKException
     */
    public String getWalletAddress(int wallet_id) throws SecureSDKException {
        String wallet_address = "";
        app_id = getApp_id();
        validateAppId(app_id);
        try {
            if (getUserValid()) {
                if (wallet_id != -1 && mDatabaseHandler != null) {
                    WalletDetails mWalletDetails = mDatabaseHandler.getWalletWithId(wallet_id);
                    wallet_address = mWalletDetails.getmKeyPair().address;
                }
            } else {
                showAuthenticationScreen();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wallet_address;
    }

    /***
     * This method is used to empty wallet to the Vault address
     *
     * @param wallet_id
     * @param extra_fee
     * @param mTransactionBuilder
     * @throws SecureSDKException
     */
    public void emptyWalletToVault(int wallet_id, long extra_fee, TransactionBuilder mTransactionBuilder) throws SecureSDKException {
        validateWalletId(wallet_id);
        String mOutputAddress = "";
        long mAmountToSend = 0;
        boolean isFundsSufficient = true;
        if (mDatabaseHandler != null) {
            mOutputAddress = mDatabaseHandler.getWalletWithId(SDKConstants.VAULT_ID).getmKeyPair().address;
            mAmountToSend = BTCUtils.parseValue(mDatabaseHandler.getWalletWithId(wallet_id).getWALLET_LAST_UPDATE_BALANCE());
            if (mAmountToSend <= 0) {
                showLowBalanceError(mTransactionBuilder);
                isFundsSufficient = false;
            }
            if (mAmountToSend > extra_fee) {
                mAmountToSend = mAmountToSend - extra_fee;
            } else {
                showLowBalanceError(mTransactionBuilder);
                isFundsSufficient = false;
            }
        }
        if (isFundsSufficient)
            // After validation of all the balance try to transfer the balance
            sendBitCoins(wallet_id, mOutputAddress, mAmountToSend, extra_fee, mTransactionBuilder);
    }

    /***
     * This method is used to empty all the wallets to the vault address
     * @param mTransactionBuilder
     */
    public void emptyAllWalletsToVault(TransactionBuilder mTransactionBuilder) {
        try {
            this.mTransactionBuilder = mTransactionBuilder;
            if (mDatabaseHandler != null) {
                ArrayList<WalletDetails> mWalletsAddressesList = new ArrayList<>();
                mWalletsAddressesList = mDatabaseHandler.getAllWallets();
                String vault = mDatabaseHandler.getWalletWithId(SDKConstants.VAULT_ID).getmKeyPair().address;
                if (emptyWalletCount < SDKHelper.FIVE) {
                    isEmptyAllWalletsInitialized = true;
                    String walletBalance = mWalletsAddressesList.get(emptyWalletCount - 1).getWALLET_LAST_UPDATE_BALANCE();
                    if (!walletBalance.equalsIgnoreCase(SDKHelper.ZERO_ZERO)) {
                        sendBitCoins(emptyWalletCount, vault, BTCUtils.parseValue(SDKHelper.ZERO + ""), BTCUtils.parseValue("0.0001"), mTransactionBuilder);
                        emptyWalletCount = emptyWalletCount + 1;
                    } else {
                        if (emptyWalletCount < SDKHelper.FIVE) {
                            emptyWalletCount = emptyWalletCount + 1;
                            emptyAllWalletsToVault(mTransactionBuilder);
                        }
                    }
                } else {
                    isEmptyAllWalletsInitialized = false;
                    if (mTransactionBuilder != null && !mTransactionId.equalsIgnoreCase("")) {
                        mTransactionBuilder.TransactionId(mTransactionId);
                    } else {
                        mTransactionBuilder.TransactionFailed(SDKErrors.NO_ENOUGH_FUNDS);
                    }
                }
//                new MultipleWalletsUnspentCount(this).getWalletsUnspent(mWalletsAddressesList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * This method is used to show the low balance in the wallet error
     * @param mTransactionBuilder
     */
    private void showLowBalanceError(TransactionBuilder mTransactionBuilder) {
        if (mTransactionBuilder != null) {
            mTransactionBuilder.TransactionFailed(SDKErrors.NO_ENOUGH_FUNDS);
        }
    }

    /***
     * This method is used to transfer the bit coins from vault to wallet
     * @param wallet_id
     * @param amount_to_transfer
     * @param extra_fee
     * @param mTransactionBuilder
     */
    public void sendBitcoinsVaultToWalllet(int wallet_id, long amount_to_transfer, long extra_fee,
                                           TransactionBuilder mTransactionBuilder) throws SecureSDKException {
        validateWalletId(wallet_id);
        String mOutputAddress = "";
        long mVaultBalance = 0;
        long mTotalBitcoinsToSend = amount_to_transfer + extra_fee;
        boolean isAmountOkToSend = true;
        if (mDatabaseHandler != null) {
            mOutputAddress = mDatabaseHandler.getWalletWithId(wallet_id).getmKeyPair().address;
            mVaultBalance = BTCUtils.parseValue(mDatabaseHandler.getWalletWithId(SDKConstants.VAULT_ID).getWALLET_LAST_UPDATE_BALANCE());
            if (mVaultBalance <= 0) {
                showLowBalanceError(mTransactionBuilder);
                isAmountOkToSend = false;
            } else if (amount_to_transfer > mVaultBalance) {
                showLowBalanceError(mTransactionBuilder);
                isAmountOkToSend = false;
            } else if (mTotalBitcoinsToSend > mVaultBalance) {
                showLowBalanceError(mTransactionBuilder);
                isAmountOkToSend = false;
            }
        }
        if (isAmountOkToSend)
            sendBitCoins(SDKConstants.VAULT_ID, mOutputAddress, amount_to_transfer, extra_fee, mTransactionBuilder);
    }

    /***
     * This method is used to transfer the bit coins from vault to wallet
     * @param wallet_id
     * @param amount_to_transfer
     * @param extra_fee
     * @param mTransactionBuilder
     */
    public void sendBitcoinsWallletToVault(int wallet_id, long amount_to_transfer, long extra_fee, TransactionBuilder mTransactionBuilder) throws SecureSDKException {
        validateWalletId(wallet_id);
        String mOutputAddress = "";
        long mVaultBalance = 0;
        long mTotalBitcoinsToSend = amount_to_transfer + extra_fee;
        boolean isAmountOkToSend = true;
        if (mDatabaseHandler != null) {
            mOutputAddress = mDatabaseHandler.getWalletWithId(SDKConstants.VAULT_ID).getmKeyPair().address;
            mVaultBalance = BTCUtils.parseValue(mDatabaseHandler.getWalletWithId(wallet_id).getWALLET_LAST_UPDATE_BALANCE());
            if (mVaultBalance <= 0) {
                showLowBalanceError(mTransactionBuilder);
                isAmountOkToSend = false;
            } else if (amount_to_transfer > mVaultBalance) {
                showLowBalanceError(mTransactionBuilder);
                isAmountOkToSend = false;
            } else if (mTotalBitcoinsToSend > mVaultBalance) {
                showLowBalanceError(mTransactionBuilder);
                isAmountOkToSend = false;
            }
        }
        if (isAmountOkToSend)
            sendBitCoins(wallet_id, mOutputAddress, amount_to_transfer, extra_fee, mTransactionBuilder);
    }


    /**
     * This method is used to generate transaction for the block chain, this method will
     * communicate with the sdk layer to create transaction.
     *
     * @param mDestinationAddress
     * @param mBitCoinToSend
     * @param mMinorFee
     * @return
     */
    public byte[] generateBitCoinTransaction(String mDestinationAddress, String mBitCoinToSend, String mMinorFee) {
        byte[] mBitCoinTransaction = null;
        return mBitCoinTransaction;
    }

    /***
     * This method is used to send the transaction on the block chain server and
     * send back the transaction id to the user.
     *
     * @param mBitCoinTransaction
     * @param mTransactionBuilder
     * @param mWallet_id
     * @return
     */
    private String sendTransaction(String mBitCoinTransaction,
                                   TransactionBuilder mTransactionBuilder, int mWallet_id) {
        String mTransactionId = "";
        this.mTransactionBuilder = mTransactionBuilder;
        if (mBitCoinTransaction.isEmpty())
            throw new NullPointerException(SDKErrors.BIT_COIN_TRANSACTION_NULL);
        if (mContext != null && SDKUtils.isNetworkAvailable(mContext)) {
            if (SDKConstants.WALLET_TYPE == SDKHelper.ZERO ||
                    SDKConstants.WALLET_TYPE == SDKHelper.ONE) {
                new BitCoinTransactionBroadcast(this).broadCastTransaction(mBitCoinTransaction, mWallet_id);
            } else {
                new EOTCoinTransactionBroadcast(this).broadCastTransaction(mBitCoinTransaction);
            }
        } else {
            showInternetError();
        }
        return mTransactionId;
    }

    /**
     * This method is used to get the status of the transaction which is sent to the
     * block chain server
     *
     * @param mTransactionId
     * @return
     */
    public String getTransactionStatus(String mTransactionId) {
        String mTransactionStatus = "";
        return mTransactionStatus;
    }

    /**
     * This sdk method is used to get all the properties of the wallet address
     *
     * @param wallet_address
     */
    public void getAddressProperties(String wallet_address) {

    }

    /**
     * This sdk method is used to validate the application id of the
     * development app passed by the developer
     *
     * @param app_id
     */
    private void validateAppId(String app_id) {
        if (app_id.equalsIgnoreCase(SDKErrors.EMPTY_DATA))
            throw new NullPointerException(SDKErrors.APP_ID_NULL);
    }

    /**
     * This sdk method is used to validate the application id of the
     * development app passed by the developer
     *
     * @param wallet_id
     */
    private void validateWalletId(int wallet_id) {
        if (wallet_id < 0)
            throw new NullPointerException(SDKErrors.WALLET_ID_NULL);
    }

    /**
     * This sdk method is used to get the package name of the application which is
     * going to call this sdk methods.
     *
     * @return
     */
    private String getApp_id() throws SecureSDKException {
        String mApplicationId = "";
        try {
            Context mAppContext = getContext();
            if (mAppContext == null)
                throw new RuntimeException(SDKErrors.SDK_NOT_INITIALIZED);
            if (mAppContext != null)
                mApplicationId = mAppContext.getPackageName();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mApplicationId;
    }

    /***
     * This method is used to send the request for creating the wallets credentials
     */
    private void generateWallet() {
        mKeyPair = BTCUtils.generateWifKey(true);
    }

    /**
     * This method is used to generate the wallets keys for all the wallets
     *
     * @param app_id
     * @param mWallet_Id
     */
    private void generateWalletKeys(String app_id, int mWallet_Id) throws RuntimeException {
        if (mKeyPair != null) {
            mWalletDetails = new WalletDetails();
            mWalletDetails.setmKeyPair(mKeyPair);
            mWalletDetails.setAPP_ID(app_id);
            mWalletDetails.setWALLET_ID(mWallet_Id + "");
            if (mWallet_Id == SDKHelper.ZERO)
                mWalletDetails.setWALLET_NAME(SDKHelper.VAULT_NAME);
            else
                mWalletDetails.setWALLET_NAME(SDKHelper.WALLET_NAME + " " + mWallet_Id + "");
            mWalletDetails.setWALLET_ICON(null);
            mWalletDetails.setWALLET_LAST_UPDATE_BALANCE(SDKHelper.ZERO_ZERO);
            mWalletDetails.setWALLET_UPDATE_TIME("00:00");
            if (!mDatabaseHandler.isWalletAlreadyExistsForAppId(app_id)) {
                mDatabaseHandler.addWallet(mWalletDetails);
                if (mWalletCallback != null)
                    mWalletCallback.getWallet(mWalletDetails);
            } else {
                throw new RuntimeException(SDKErrors.APP_ID_EXISTS);
            }
        } else {
            SDKUtils.showErrorLog(TAG, "Key Pair is null");
        }
    }

    /****
     * This method is used to calculate the fees for the transaction user is going to spend
     * @param mSenderWalletId
     * @param receiversAddress
     * @param amountToSend
     * @param extraFee
     * @param mTransactionFeesCalculator
     * @throws SecureSDKException
     */
    public void calculateTransactionFees(final int mSenderWalletId,
                                         final String receiversAddress,
                                         final long amountToSend, final long extraFee,
                                         final TransactionFeesCalculator mTransactionFeesCalculator) throws SecureSDKException {

        if (extraFee == SDKHelper.ZERO)
            optionalExtraFee = BTCUtils.MIN_FEE_PER_KB;
        else
            optionalExtraFee = extraFee;
        if (mSenderWalletId < 0 || mSenderWalletId == 0) {
            throw new SecureSDKException(SDKErrors.MEDIA_VAULT_WALLETID);
        } else {
            if (mDatabaseHandler != null) {
                mWalletDetails = mDatabaseHandler.getWalletWithId(mSenderWalletId);
                wallet_address = mWalletDetails.getmKeyPair().address;
                mKeyPair = mWalletDetails.getmKeyPair();
                getWalletUnspentCount(mSenderWalletId, new WalletUnspentCountHandler() {
                    @Override
                    public void walletUnspentCountSuccess(JSONArray mJsonArrayResponse) {
                        parseUnspentWalletData(mJsonArrayResponse);
                        try {
                            BTCUtils.calculateFeesForSendingAmount(unspentOutputs, amountToSend, optionalExtraFee, false, mTransactionFeesCalculator);
                        } catch (BitcoinException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void walletUnspentCountFailure(VolleyError mErrorResponse) {
                        if (mTransactionFeesCalculator != null)
                            mTransactionFeesCalculator.transacionFeesCalculatorFailed(SDKErrors.FEES_CALCULATION_FAILED);
                    }
                });
            }
        }
    }

    /***
     * This method is used to calculate the transaction fees for empty all wallets to vault
     * @param mTransactionFeesCalculator
     */
    public void calculateEmptyWalletToVaultFees(long extraFees, TransactionFeesCalculator mTransactionFeesCalculator) {
        if (extraFees == 0 || extraFees < 0)
            emptyAllWalletsToVaultExtraFees = 0;
        this.mTransactionFeesCalculator = mTransactionFeesCalculator;
        emptyAllWalletsToVaultExtraFees = extraFees;
        ArrayList<String> mWalletsAddressesList = new ArrayList<>();
        mWalletsAddressesList = mDatabaseHandler.getAllWalletsAddress();
        new MultipleWalletsUnspentCount(this).getWalletsUnspent(mWalletsAddressesList);
    }

    /***
     * This method is used to calculate the fees for the transaction during vault to wallet transfer
     * @param receiversAddress
     * @param amountToSend
     * @param mTransactionFeesCalculator
     */
    public void calculateVaultToWalletFees(final String receiversAddress,
                                           final long amountToSend,
                                           final TransactionFeesCalculator mTransactionFeesCalculator) {
        if (mDatabaseHandler != null) {
            mWalletDetails = mDatabaseHandler.getWalletWithId(SDKConstants.VAULT_ID);
            wallet_address = mWalletDetails.getmKeyPair().address;
            mKeyPair = mWalletDetails.getmKeyPair();
            try {
                getWalletUnspentCount(SDKConstants.VAULT_ID, new WalletUnspentCountHandler() {
                    @Override
                    public void walletUnspentCountSuccess(JSONArray mJsonArrayResponse) {
                        parseUnspentWalletData(mJsonArrayResponse);
                        try {
                            BTCUtils.calculateFeesForSendingAmount(unspentOutputs, amountToSend, 0, false, mTransactionFeesCalculator);
                        } catch (BitcoinException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void walletUnspentCountFailure(VolleyError mErrorResponse) {
                        if (mTransactionFeesCalculator != null)
                            mTransactionFeesCalculator.transacionFeesCalculatorFailed(SDKErrors.FEES_CALCULATION_FAILED);
                    }
                });
            } catch (SecureSDKException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * This method is used to create the transaction of the wallet
     * User can create the transaction based on the wallet address
     *
     * @param mWalletIdFromWhichDataToSend
     * @param receiversAddress
     * @param amountToSend
     * @param extraFee
     * @param mTransactionBuilder
     */
    public void sendBitCoins(final int mWalletIdFromWhichDataToSend,
                             final String receiversAddress,
                             final long amountToSend, final long extraFee,
                             final TransactionBuilder mTransactionBuilder) {
        long optionalExtraFee = 0;
        if (extraFee == SDKHelper.ZERO)
            optionalExtraFee = BTCUtils.MIN_FEE_PER_KB;
        SDKConstants.WALLET_TYPE = 0;
        unspentOutputs = new ArrayList<>();
        mRetryRequestModel = new RequestRetryModel();
        mRetryRequestModel.setmWalletIdFromWhichDataToSend(mWalletIdFromWhichDataToSend);
        mRetryRequestModel.setOutputAddress(receiversAddress);
        mRetryRequestModel.setAmountToSend(amountToSend);
        mRetryRequestModel.setExtraFee(optionalExtraFee);
        mRetryRequestModel.setmTransactionBuilder(mTransactionBuilder);
        this.mTransactionBuilder = mTransactionBuilder;
        WalletDetails mWalletDetails = null;
        if (mDatabaseHandler != null) {
            mWalletDetails = mDatabaseHandler.getWalletWithId(mWalletIdFromWhichDataToSend);
            wallet_address = mWalletDetails.getmKeyPair().address;
            mKeyPair = mWalletDetails.getmKeyPair();
        }
        try {
            getWalletUnspentCount(mWalletIdFromWhichDataToSend, new WalletUnspentCountHandler() {
                @Override
                public void walletUnspentCountSuccess(JSONArray mJsonArrayResponse) {
                    parseUnspentWalletData(mJsonArrayResponse);
                    new TransactionSignature(mRetryRequestModel, mUnspentCountsSeperateData).execute(); // send data for sign the tx
                }

                @Override
                public void walletUnspentCountFailure(VolleyError mErrorResponse) {
                    if (mErrorResponse == null && !isEmptyAllWalletsInitialized) {
                        if (mTransactionBuilder != null) {
                            mTransactionBuilder.TransactionFailed(SDKErrors.NO_UNSPENT_COUNT);
                        }
                    } else {
                        emptyAllWalletsToVault(mTransactionBuilder);
                    }
                }
            });
        } catch (SecureSDKException e) {
            e.printStackTrace();
        }
    }

    /***
     * This method is used to parse the wallet unspent count from the APi's
     * @param mJsonArrayResponse
     */
    private void parseUnspentWalletData(JSONArray mJsonArrayResponse) {
        unspentOutputs = new ArrayList<UnspentOutputInfo>();
        String mData = "";
        String jsonStr = mData.replace((char) 160, ' ').trim();//remove nbsp
        byte[] outputScriptWeAreAbleToSpend = new byte[0];
        try {
            outputScriptWeAreAbleToSpend = Transaction.Script.buildOutput(wallet_address).bytes;
        } catch (BitcoinException e) {
            e.printStackTrace();
        }
        try {
            Transaction.Script script = null;
            long value = 0;
            long confirmations = 0;
            String address = "";
            for (int i = 0; i < mJsonArrayResponse.length(); i++) {
                JSONObject unspentOutput = mJsonArrayResponse.getJSONObject(i);
                byte[] txHash = BTCUtils.fromHex(unspentOutput.getString(SDKHelper.KEY_TX_ID));
                script = new Transaction.Script(BTCUtils.fromHex(unspentOutput.getString("scriptPubKey")));
                address = unspentOutput.getString(SDKHelper.KEY_ADDRESS);
                value = unspentOutput.getLong(SDKHelper.KEY_SATOSHIS);
                if (unspentOutput.has(SDKHelper.KEY_CONFIRMATIONS))
                    confirmations = unspentOutput.getLong(SDKHelper.KEY_CONFIRMATIONS);
                int outputIndex = (int) unspentOutput.getLong(SDKHelper.KEY_VOUT);
                unspentOutputs.add(new UnspentOutputInfo(txHash, script, value, outputIndex, confirmations, address));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void walletUnspentCountSuccess(JSONArray mJsonArrayResponse) {
        mUnspentCountsSeperateData = new HashMap<>();
        String mData = "";
        String jsonStr = mData.replace((char) 160, ' ').trim();//remove nbsp
        byte[] outputScriptWeAreAbleToSpend = new byte[0];
        ArrayList<String> mAddresses = null;
        try {
            if (mDatabaseHandler != null) {
                mAddresses = mDatabaseHandler.getAllWalletsAddress();
                mKeyPair = mDatabaseHandler.getAllWallets().get(0).getmKeyPair();
                for (int i = 0; i < mAddresses.size(); i++) {
                    outputScriptWeAreAbleToSpend = Transaction.Script.buildOutput(mAddresses.get(i)).bytes;
                }
            }
        } catch (BitcoinException e) {
            e.printStackTrace();
        }
        try {
            Transaction.Script script = null;
            long value = 0;
            long confirmations = 0;
            String address = "";
            unspentOutputs.clear();
            for (int i = 0; i < mJsonArrayResponse.length(); i++) {
                JSONObject unspentOutput = mJsonArrayResponse.getJSONObject(i);
                byte[] txHash = BTCUtils.fromHex(unspentOutput.getString(SDKHelper.KEY_TX_ID));
                script = new Transaction.Script(BTCUtils.fromHex(unspentOutput.getString("scriptPubKey")));
                value = unspentOutput.getLong(SDKHelper.KEY_SATOSHIS);
                address = unspentOutput.getString(SDKHelper.KEY_ADDRESS);
                confirmations = unspentOutput.getLong(SDKHelper.KEY_CONFIRMATIONS);
                int outputIndex = (int) unspentOutput.getLong(SDKHelper.KEY_VOUT);
                unspentOutputs.add(new UnspentOutputInfo(txHash, script, value, outputIndex, confirmations, address));
                if (mUnspentCountsSeperateData.containsKey(address)) {
                    ArrayList unSpentVaultArray = mUnspentCountsSeperateData.get(address);
                    unSpentVaultArray.add(new UnspentOutputInfo(txHash, script, value, outputIndex, confirmations, address));
                    mUnspentCountsSeperateData.put(address, unSpentVaultArray);
                } else {
                    ArrayList<UnspentOutputInfo> unSpentVaultArray = new ArrayList<>();
                    unSpentVaultArray.add(new UnspentOutputInfo(txHash, script, value, outputIndex, confirmations, address));
                    mUnspentCountsSeperateData.put(address, unSpentVaultArray);
                }
            }

            mRetryRequestModel = new RequestRetryModel();
            String mVaultAddress = mDatabaseHandler.getWalletWithId(SDKConstants.VAULT_ID).getmKeyPair().address;
            mRetryRequestModel.setOutputAddress(mVaultAddress);
            mRetryRequestModel.setAmountToSend(BTCUtils.parseValue(SDKHelper.ZERO + ""));
            mRetryRequestModel.setExtraFee(BTCUtils.parseValue(SDKHelper.ZERO + ""));
            wallet_address = mVaultAddress;
//            new TransactionSignature(mRetryRequestModel, mUnspentCountsSeperateData).execute(); // send data for sign the tx
            BTCUtils.calculateFeesForSendingAmount(unspentOutputs, 0, emptyAllWalletsToVaultExtraFees, false, mTransactionFeesCalculator);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void walletUnspentCountFailure(VolleyError mErrorResponse) {
        if (mTransactionFeesCalculator != null)
            mTransactionFeesCalculator.transacionFeesCalculatorFailed(SDKErrors.FEES_CALCULATION_FAILED);
    }

    /***
     * This class is used to sign the transaction and used to generating the transaction hex to
     * push it to the block chain.
     */
    class TransactionSignature extends AsyncTask<Void, Void, Transaction> {
        RequestRetryModel mRetryRequestModel = null;

        /***
         * This class constructor to get the data to sign the transaction
         * @param mRetryRequestModel
         * @param mUnspentCountsSeperateData
         */
        public TransactionSignature(RequestRetryModel mRetryRequestModel, HashMap<String,
                ArrayList<UnspentOutputInfo>> mUnspentCountsSeperateData) {
            this.mRetryRequestModel = mRetryRequestModel;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Transaction doInBackground(Void... params) {
            Transaction spendTx = null;
            try {
                spendTx = BTCUtils.createTransaction(unspentOutputs,
                        mRetryRequestModel.getOutputAddress(), wallet_address, (long) mRetryRequestModel.getAmountToSend()
                        , (long) mRetryRequestModel.getExtraFee(),
                        mKeyPair.publicKey,
                        mKeyPair.privateKey, mTransactionBuilder, mDatabaseHandler);
            } catch (BitcoinException e) {
                e.printStackTrace();
            }
            return spendTx;
        }

        @Override
        protected void onPostExecute(Transaction transaction) {
            super.onPostExecute(transaction);
            if (transaction != null) {
                tryToGenerateSpendingTransaction(mRetryRequestModel.getOutputAddress(),
                        (long) mRetryRequestModel.getAmountToSend(),
                        transaction, mRetryRequestModel.getmWalletIdFromWhichDataToSend(), mTransactionBuilder);
            } else {
                if (mTransactionBuilder != null)
                    mTransactionBuilder.TransactionFailed(SDKErrors.TRANSACTION_FAILED_ERROR);
            }
        }
    }

    /***
     * This method is used to create raw hash transaction of the given input
     * parameters.
     *  @param outputAddress
     * @param amountToSend
     * @param spendTx       @return
     * @param mTransactionBuilder
     */
    private String tryToGenerateSpendingTransaction(final String outputAddress,
                                                    long amountToSend, final Transaction spendTx, final int mWallet_id,
                                                    final TransactionBuilder mTransactionBuilder) {

        final long requestedAmountToSend = amountToSend;
        WalletDetails mWalletDetails = null;
        if (mDatabaseHandler != null) {
            mWalletDetails = mDatabaseHandler.getWalletWithId(mWallet_id);
            mKeyPair = mWalletDetails.getmKeyPair();
        }
        final boolean inputsComesFromJson = false;
        final int predefinedConfirmationsCount = 0;
        long inValue = 0;
        for (Transaction.Input input : spendTx.inputs) {
            for (UnspentOutputInfo unspentOutput : unspentOutputs) {
                if (Arrays.equals(unspentOutput.txHash, input.outPoint.hash) && unspentOutput.outputIndex == input.outPoint.index) {
                    inValue += unspentOutput.value;
                }
            }
        }
        long outValue = 0;
        for (Transaction.Output output : spendTx.outputs) {
            outValue += output.value;
        }
        long fee = inValue - outValue;
        mTransactionResult = new GenerateTransactionResult(spendTx, fee);
        if (mTransactionResult.tx != null) {
            String amountStr = null;
            Transaction.Script out = null;
            try {
                out = Transaction.Script.buildOutput(outputAddress);
            } catch (BitcoinException ignore) {
            }
            if (mTransactionResult.tx.outputs[0].script.equals(out)) {
                amountStr = BTCUtils.formatValue(mTransactionResult.tx.outputs[0].value, mTransactionBuilder, mContext);
            }
            if (amountStr == null) {
                if (mTransactionBuilder != null && mContext != null)
                    mTransactionBuilder.TransactionFailed(mContext.getResources().getString(R.string.problem_with_funds));
            } else {
                String descStr;
                String feeStr = BTCUtils.formatValue(mTransactionResult.fee, mTransactionBuilder, mContext);
                String changeStr;
                if (mTransactionResult.tx.outputs.length == 1) {
                    changeStr = null;
                    descStr = mContext.getString(R.string.spend_tx_description,
                            amountStr,
                            mKeyPair.address,
                            outputAddress,
                            feeStr
                    );
                } else if (mTransactionResult.tx.outputs.length == 2) {
                    changeStr = BTCUtils.formatValue(mTransactionResult.tx.outputs[1].value, mTransactionBuilder, mContext);
                    descStr = mContext.getString(R.string.spend_tx_with_change_description,
                            amountStr,
                            mKeyPair.address,
                            outputAddress,
                            feeStr,
                            changeStr
                    );
                } else if (mTransactionResult.tx.outputs.length == 3) {
                    changeStr = BTCUtils.formatValue(mTransactionResult.tx.outputs[2].value, mTransactionBuilder, mContext);
                    descStr = mContext.getString(R.string.spend_tx_with_change_description,
                            amountStr,
                            mKeyPair.address,
                            outputAddress,
                            feeStr,
                            changeStr
                    );
                } else {
                    throw new RuntimeException();
                }
                SpannableStringBuilder descBuilder = new SpannableStringBuilder(descStr);
                int spanBegin = descStr.indexOf(mKeyPair.address);
                spanBegin = descStr.indexOf(outputAddress);

                final String nbspBtc = "\u00a0BTC";
                spanBegin = descStr.indexOf(amountStr + nbspBtc);
                if (spanBegin >= 0) {
                    descBuilder.setSpan(new StyleSpan(Typeface.BOLD), spanBegin, spanBegin + amountStr.length() + nbspBtc.length(), SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE);
                }
                spanBegin = descStr.indexOf(feeStr + nbspBtc, spanBegin);
                if (spanBegin >= 0) {
                    descBuilder.setSpan(new StyleSpan(Typeface.BOLD), spanBegin, spanBegin + feeStr.length() + nbspBtc.length(), SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE);
                }
                if (changeStr != null) {
                    spanBegin = descStr.indexOf(changeStr + nbspBtc, spanBegin);
                    if (spanBegin >= 0) {
                        descBuilder.setSpan(new StyleSpan(Typeface.BOLD), spanBegin, spanBegin + changeStr.length() + nbspBtc.length(), SpannableStringBuilder.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                }
                mRawTransaction = BTCUtils.toHex(mTransactionResult.tx.getBytes());
                SDKUtils.showLog(TAG, "-----Raw Transaction-------" + mRawTransaction);
                sendTransaction(mRawTransaction, BitVaultWalletManager.this.mTransactionBuilder, mWallet_id);
                if (mTransactionBuilder != null)
                    mTransactionBuilder.RequestedTransaction(mRawTransaction);
            }
        }
        return mRawTransaction;
    }


    @Override
    public void TransactionBroadcastSuccess(String mTransactionId, int mWallet_id) {
        try {
            this.mTransactionId = mTransactionId;
            if (mTransactionBuilder != null) {
                if (!isEmptyAllWalletsInitialized)
                    mTransactionBuilder.TransactionId(mTransactionId);
                else if (isEmptyAllWalletsInitialized && emptyWalletCount == SDKHelper.FIVE)
                    mTransactionBuilder.TransactionId(mTransactionId);
                else
                    emptyAllWalletsToVault(mTransactionBuilder);
                String mAddress = "";
                if (mDatabaseHandler != null) {
                    updateWalletBalance(mWallet_id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void TransactionBroadcastFailure(VolleyError mTransactionError) {
        if (mTransactionError.networkResponse.statusCode == 400 && SDKConstants.CURRENT_RETRY_COUNT <= SDKConstants.MAX_FAILURE_RETRY_COUNT) {
            SDKConstants.CURRENT_RETRY_COUNT = SDKConstants.CURRENT_RETRY_COUNT + 1;
            if (mRetryRequestModel != null) {
                sendBitCoins(mRetryRequestModel.getmWalletIdFromWhichDataToSend(), mRetryRequestModel.getOutputAddress(),
                        mRetryRequestModel.getAmountToSend(), mRetryRequestModel.getExtraFee(), mRetryRequestModel.getmTransactionBuilder());
            } else {
                SDKUtils.showErrorLog(TAG, "-------mRetryRequestModel is null--");
            }
        } else if (mTransactionBuilder != null) {
            mTransactionBuilder.TransactionFailed(SDKErrors.TRANSACTION_FAILED_ERROR);
        }
    }

    @Override
    public void successWalletBalanceCallback(WalletBalanceModel mWalletBalanceModel) {
        if (mWalletBalanceModel != null) {
            String mWalletBalance = mWalletBalanceModel.getmWalletBalance();
            if (mDatabaseHandler != null) {
                mDatabaseHandler.updateWalletBalance(mWalletBalanceModel.getmWalletId() + "", mWalletBalance);
            }
        }
    }

    @Override
    public void failedWalletBalanceCallback(VolleyError mError) {

    }

    /***
     * This class is used to store the parameters of the transaction.
     */
    private class GenerateTransactionResult {
        static final int ERROR_SOURCE_UNKNOWN = 0;
        static final int ERROR_SOURCE_INPUT_TX_FIELD = 1;
        static final int ERROR_SOURCE_ADDRESS_FIELD = 2;
        static final int HINT_FOR_ADDRESS_FIELD = 3;
        static final int ERROR_SOURCE_AMOUNT_FIELD = 4;

        final Transaction tx;
        final String errorMessage;
        final int errorSource;
        final long fee;

        public GenerateTransactionResult(String errorMessage, int errorSource) {
            tx = null;
            this.errorMessage = errorMessage;
            this.errorSource = errorSource;
            fee = -1;
        }

        public GenerateTransactionResult(Transaction tx, long fee) {
            this.tx = tx;
            errorMessage = null;
            errorSource = ERROR_SOURCE_UNKNOWN;
            this.fee = fee;
        }
    }

    /****
     * This method is used to manage internal wallets
     */
    public void manageWallets() {
        verifyDatabaseAndGenerateWallets();
        subscribeForNotifications();
    }
}
