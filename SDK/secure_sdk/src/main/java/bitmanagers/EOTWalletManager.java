package bitmanagers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import com.android.volley.VolleyError;
import com.embedded.wallet.R;

import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import commons.SDKConstants;
import commons.SDKErrors;
import commons.SDKHelper;
import commons.SecureSDKException;
import database.DatabaseHandler;
import eotutil.Qr;
import eotutil.eotessential.Constants;
import eotutil.eotessential.SDKUserValidation;
import iclasses.BroadcastBitCoinTransactionHandler;
import iclasses.EotCallbacks.EotWalletCallback;
import iclasses.RecoverWalletSeedListener;
import iclasses.TransactionBuilder;
import iclasses.TransactionHistoryCallback;
import iclasses.UserAuthenticationCallback;
import iclasses.WalletBalanceCallback;
import iclasses.WalletUnspentCountHandler;
import model.eotmodel.EotCoinsTransferModel;
import model.eotmodel.EotWalletDetail;
import utils.SDKUtils;
import valle.btc.BTCUtils;
import valle.btc.BitcoinException;
import valle.btc.EotBTCUtils;
import valle.btc.EotKeyPair;
import valle.btc.Transaction;
import valle.btc.UnspentOutputInfo;
import webservicescontroller.FetchEotWalletBalance;
import webservicescontroller.eotservices.EOTCoinTransactionBroadcast;
import webservicescontroller.eotservices.EotTransactionsHistoryHandler;
import webservicescontroller.eotservices.EotWalletUnspentCount;

/**
 * Created by vvdn on 9/22/2017.
 */

public final class EOTWalletManager extends BitVaultBaseManager implements BroadcastBitCoinTransactionHandler {
    private static EOTWalletManager mEotWalletManager = null;
    private static String walletSeed = "";
    public static int ENTROPY_SIZE_DEBUG = -1;
    private Context mContext = null;
    private Activity mActivity = null;
    private int maxQrSize;
    private RecoverWalletSeedListener mRecoverWalletSeedListener = null;
    private String TAG = EOTWalletManager.class.getSimpleName();
    private ArrayList<UnspentOutputInfo> unspentOutputs = null;
    private String wallet_address = "";
    private EotKeyPair mWalletKey = null;
    private TransactionBuilder mTransactionBuilder = null;
    private GenerateTransactionResult mTransactionResult = null;
    private String mRawTransaction = "";
    private DatabaseHandler mEotDatabaseHandler = null;

    private EOTWalletManager() {
        getWalletApplication();
        initializeDatabase();
        getEotWallet(null);
    }

    /***
     * This method is used to create the new database for the eot wallets
     */
    private void initializeDatabase() {
        if (mContext != null) {
            mEotDatabaseHandler = new DatabaseHandler(mContext);
        } else
            SDKUtils.showErrorLog(TAG, "----Contest is null--------");
    }

    /***
     * This method is used to return the
     * @return
     */
    public static EOTWalletManager getEOTWalletInstance() {
        SDKConstants.WALLET_TYPE=2;
        if (mEotWalletManager == null)
            mEotWalletManager = new EOTWalletManager();
        return mEotWalletManager;
    }

    /***
     * This method is used to access the application controller internally
     * @return
     */

    private void getWalletApplication() {
        try {
            createEotDatabase();
            mContext = getContext();
            if (mContext != null) {
                mActivity = (Activity) mContext;
                maxQrSize = calculateMaxQrCodeSize(mActivity.getResources());
                initializeAuthenticationListener();
            }
        } catch (SecureSDKException e) {
            e.printStackTrace();
        }
    }

    /***
     * This method is used to create the database directory inside the external storage.
     */
    private void createEotDatabase() {
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
     * Qr-code size calculation
     */
    private int calculateMaxQrCodeSize(Resources resources) {
        int qrPadding = resources.getDimensionPixelSize(R.dimen.qr_code_padding);
        int qrCodeViewSize = resources.getDimensionPixelSize(R.dimen.qr_code_size);
        return qrCodeViewSize - 2 * qrPadding;
    }


    /****
     * This method is used to initialize the authentication whether user is validated or not
     */
    private void initializeAuthenticationListener() {
        SDKConstants.mUserAuthenticationCallback = new UserAuthenticationCallback() {
            @Override
            public void onAuthenticationSuccess() {
                Bitmap qrCodeBitmap = null;
                String seedString = "";
                if (mEotDatabaseHandler != null) {
                    EotWalletDetail mWalletDetail = mEotDatabaseHandler.getWalletSeed();
                    seedString = mWalletDetail.getSeed();
                    if (seedString != null && !seedString.isEmpty()) {
                        qrCodeBitmap = Qr.bitmap(seedString, maxQrSize);
                    }
                    if (mRecoverWalletSeedListener != null) {
                        mRecoverWalletSeedListener.walletSeedSuccess(seedString, qrCodeBitmap);
                    }
                }
            }

            @Override
            public void onAuthenticationFailed() {
                if (mRecoverWalletSeedListener != null) {
                    mRecoverWalletSeedListener.walletSeedFailed(mActivity.getResources().getString(R.string.user_auth_failed));
                }
            }
        };
    }

    /***
     * This manager method is used to get the wallet seed for recovery of the wallet
     * @return
     */
    public String getWalletSeed() {
        walletSeed = generateMnemonicString(Constants.SEED_ENTROPY_DEFAULT);
        return walletSeed;
    }

    /***
     * This method is used to get the EOT wallet
     * @param mWalletCallback
     */
    public void getEotWallet(EotWalletCallback mWalletCallback) {
        try {
            if (mEotDatabaseHandler != null) {
                EotKeyPair mWalletKeys = mEotDatabaseHandler.getEotWallet();
                if (mWalletKeys == null) {
                    SDKUtils.showLog(TAG, "====Wallet Already Exists======");
                    // Wallet not exists in the db
                    String mSeed = getWalletSeed();
                    try {
                        verifySeedAndGetWallet(mSeed, mWalletCallback);
                    } catch (MnemonicException e) {
                        e.printStackTrace();
                    }
                } else {
                    SDKUtils.showLog(TAG, "====Wallet Already Not Exists======");
                    EotWalletDetail mWalletDetails = new EotWalletDetail();
                    mWalletDetails.setAddress(mWalletKeys.address);
                    mWalletDetails.setPublic_key(mWalletKeys.publicKey);
                    if (mWalletCallback != null)
                        mWalletCallback.eotWallet(mWalletDetails);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * This method is used to generate the wallet passphrase for the wallet as seed
     * @param entropyBitsSize
     * @return
     */
    private List<String> generateMnemonic(int entropyBitsSize) {
        byte[] entropy;
        if (ENTROPY_SIZE_DEBUG > 0) {
            entropy = new byte[ENTROPY_SIZE_DEBUG];
        } else {
            entropy = new byte[entropyBitsSize / 8];
        }
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(entropy);
        List<String> mnemonic;
        try {
            mnemonic = MnemonicCode.INSTANCE.toMnemonic(entropy);
        } catch (MnemonicException.MnemonicLengthException e) {
            throw new RuntimeException(e); // should not happen, we have 16bytes of entropy
        }
        return mnemonic;
    }

    /***
     * This method is used to generate the plain text string from the wallet seed data
     * @param entropyBitsSize
     * @return
     */
    private String generateMnemonicString(int entropyBitsSize) {
        List<String> mnemonicWords = generateMnemonic(entropyBitsSize);
        return mnemonicToString(mnemonicWords);
    }

    /***
     * Internal method of creating wallet seed procedure
     * @param mnemonicWords
     * @return
     */
    private String mnemonicToString(List<String> mnemonicWords) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mnemonicWords.size(); i++) {
            sb.append(mnemonicWords.get(i));
            sb.append(' ');
        }
        return sb.toString();
    }

    /***
     * This method is used to get the updated balance of the wallet
     * @param mWalletBalanceCallback
     */
    public void getBalance(WalletBalanceCallback mWalletBalanceCallback) {
        try {
            if (mEotDatabaseHandler != null) {
                EotKeyPair mEotKeyPair = mEotDatabaseHandler.getEotWallet();
                String mEotAddress = mEotKeyPair.address;
                new FetchEotWalletBalance(mWalletBalanceCallback).checkWalletForUpdateBalance(mEotAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /****
     * This method is used to match the seed with the older auth for wallet and wallet creation process
     * @param walletSeed
     * @param mEotWalletCallback
     * @throws MnemonicException
     */
    public void verifySeedAndGetWallet(String walletSeed, EotWalletCallback mEotWalletCallback) throws MnemonicException {
        boolean isSeedOk = verifyMnemonic(walletSeed);
        if (isSeedOk) {
            EotKeyPair mKey = EotBTCUtils.generateWifKey(true, walletSeed);
            EotWalletDetail mWalletDetails = new EotWalletDetail();
            mWalletDetails.setAddress(mKey.address);
            mWalletDetails.setPublic_key(mKey.publicKey);
            mWalletDetails.setSeed(walletSeed);
            mEotDatabaseHandler.addEotWallet(walletSeed, mKey);
            if (mEotWalletCallback != null)
                mEotWalletCallback.eotWallet(mWalletDetails);
        } else {
            throw new MnemonicException(walletSeed);
        }
    }

    /****
     * This method is used to get the wallet seed from wallet account
     * @param mRecoverWalletSeedListener
     */

    public void showWalletSeed(RecoverWalletSeedListener mRecoverWalletSeedListener) {
        this.mRecoverWalletSeedListener = mRecoverWalletSeedListener;
        startValidatingUser();
        initializeAuthenticationListener();
    }

    /***
     * This method is used to validate user
     */
    private void startValidatingUser() {
        if (mActivity != null) {
            mActivity.startActivity(new Intent(mActivity, SDKUserValidation.class));
        }
    }

    /****
     * This method is used to validate the user if correct user is using the app or not
     * @param mUserAuthenticationCallback
     */
    public void validateAppUser(UserAuthenticationCallback mUserAuthenticationCallback) {
        SDKConstants.mUserAuthenticationCallback = mUserAuthenticationCallback;
        startValidatingUser();
    }

    /***
     * This method is used to verify the seed whether its valid or not
     * @param generatedSeed
     * @return
     * @throws MnemonicException
     */
    private boolean verifyMnemonic(String generatedSeed) throws MnemonicException {
        String seedText = generatedSeed;
        ArrayList<String> seedWords = new ArrayList<>();
        for (String word : seedText.trim().split(" ")) {
            if (word.isEmpty()) continue;
            seedWords.add(word);
        }
        boolean isSeedValid = false;
        try {
            MnemonicCode.INSTANCE.check(seedWords);
            isSeedValid = true;
        } catch (MnemonicException.MnemonicChecksumException e) {
            isSeedValid = false;
            throw e;
        } catch (MnemonicException.MnemonicWordException e) {
            isSeedValid = false;
            throw e;
        } catch (MnemonicException e) {
            isSeedValid = false;
            throw e;
        }
        return isSeedValid;
    }


    /***
     * This method is used to transfer the EOT coins to the other eot receivers
     * @param receivers_address
     * @param amount_to_send
     * @param optional_priority_fee
     */
    public void transferEot(String receivers_address, String amount_to_send,
                            String optional_priority_fee, final TransactionBuilder mTransactionBuilder) {
        try {
            SDKConstants.WALLET_TYPE=2;
            this.mTransactionBuilder = mTransactionBuilder;
            final EotCoinsTransferModel mEotCoinsTransferModel = new EotCoinsTransferModel();
            mEotCoinsTransferModel.setReceivers_address(receivers_address);
            double mValueToSend = Double.parseDouble(amount_to_send);
            BigDecimal value1 = BigDecimal.valueOf(mValueToSend);
            long mFinalEotToSend = (long) (value1.floatValue() * 100000000);
            double mPriorityFee = Double.parseDouble(optional_priority_fee);
            BigDecimal mBigDecimal = BigDecimal.valueOf(mPriorityFee);
            long mFinalPriorityFee = (long) (mBigDecimal.floatValue() * 100000000);
            mEotCoinsTransferModel.setAmount_to_send(mFinalEotToSend);
            mEotCoinsTransferModel.setPriority_fee(mFinalPriorityFee);
            if (mEotDatabaseHandler != null) {
                EotKeyPair mKeyPair = mEotDatabaseHandler.getEotWallet();
                wallet_address = mKeyPair.address;
                mEotCoinsTransferModel.setSender_wallet_address(wallet_address);
                mWalletKey = mKeyPair;
                mEotCoinsTransferModel.setmWalletKey(mWalletKey);
                getWalletUnspentCount(new WalletUnspentCountHandler() {
                    @Override
                    public void walletUnspentCountSuccess(JSONArray mJsonArrayResponse) {
                        parseEotUnspentData(mJsonArrayResponse);
                        signTransaction(mEotCoinsTransferModel);
                    }

                    @Override
                    public void walletUnspentCountFailure(VolleyError mErrorResponse) {
                        if (mErrorResponse == null) {
                            if (mTransactionBuilder != null) {
                                mTransactionBuilder.TransactionFailed(SDKErrors.NO_UNSPENT_COUNT);
                            }
                        }
                    }
                });
            }
        } catch (SecureSDKException e) {
            e.printStackTrace();
        }

    }

    /***
     * This method is used to parse the eot unspent data
     * @param mJsonArrayResponse
     */
    private void parseEotUnspentData(JSONArray mJsonArrayResponse) {

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
                if (SDKConstants.WALLET_TYPE == SDKHelper.ZERO ||
                        SDKConstants.WALLET_TYPE == SDKHelper.ONE) {
                    value = unspentOutput.getLong(SDKHelper.KEY_SATOSHIS);
                } else {
                    BigDecimal value1 = BigDecimal.valueOf(unspentOutput.getDouble(SDKHelper.TAG_AMOUNT));
                    value = (long) (value1.floatValue() * 100000000);
                }
                if (unspentOutput.has(SDKHelper.KEY_CONFIRMATIONS))
                    confirmations = unspentOutput.getLong(SDKHelper.KEY_CONFIRMATIONS);
                int outputIndex = (int) unspentOutput.getLong(SDKHelper.KEY_VOUT);
                unspentOutputs.add(new UnspentOutputInfo(txHash, script, value, outputIndex, confirmations, address));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * This method is used to sign the transaction
     */
    private void signTransaction(final EotCoinsTransferModel mEotCoinsTransferModel) {
        Transaction spendTx = null;
        try {
            spendTx = EotBTCUtils.createTransaction(unspentOutputs,
                    mEotCoinsTransferModel.getReceivers_address(), mEotCoinsTransferModel.getSender_wallet_address()
                    , (long) mEotCoinsTransferModel.getAmount_to_send()
                    , (long) mEotCoinsTransferModel.getPriority_fee(),
                    mWalletKey.publicKey,
                    mWalletKey.privateKey, mTransactionBuilder);
            tryToGenerateSpendingTransaction(spendTx, mEotCoinsTransferModel);
        } catch (BitcoinException e) {
            e.printStackTrace();
        }
    }

    /***
     * This method is used to create raw hash transaction of the given input
     * parameters.
     * @param spendTx       @return
     */
    private String tryToGenerateSpendingTransaction(final Transaction spendTx,
                                                    final EotCoinsTransferModel mEotCoinsTransferModel) {

        final long requestedAmountToSend = mEotCoinsTransferModel.getAmount_to_send();
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
                out = Transaction.Script.buildOutput(mEotCoinsTransferModel.getReceivers_address());
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
                            mEotCoinsTransferModel.getmWalletKey().address,
                            mEotCoinsTransferModel.getReceivers_address(),
                            feeStr
                    );
                } else if (mTransactionResult.tx.outputs.length == 2) {
                    changeStr = BTCUtils.formatValue(mTransactionResult.tx.outputs[1].value, mTransactionBuilder, mContext);
                    descStr = mContext.getString(R.string.spend_tx_with_change_description,
                            amountStr,
                            mEotCoinsTransferModel.getmWalletKey().address,
                            mEotCoinsTransferModel.getReceivers_address(),
                            feeStr,
                            changeStr
                    );
                } else if (mTransactionResult.tx.outputs.length == 3) {
                    changeStr = BTCUtils.formatValue(mTransactionResult.tx.outputs[2].value, mTransactionBuilder, mContext);
                    descStr = mContext.getString(R.string.spend_tx_with_change_description,
                            amountStr,
                            mEotCoinsTransferModel.getmWalletKey().address,
                            mEotCoinsTransferModel.getReceivers_address(),
                            feeStr,
                            changeStr
                    );
                } else {
                    throw new RuntimeException();
                }
                SpannableStringBuilder descBuilder = new SpannableStringBuilder(descStr);
                int spanBegin = descStr.indexOf(mEotCoinsTransferModel.getmWalletKey().address);
                spanBegin = descStr.indexOf(mEotCoinsTransferModel.getReceivers_address());

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
                sendTransaction(mRawTransaction);
                if (mTransactionBuilder != null)
                    mTransactionBuilder.RequestedTransaction(mRawTransaction);
            }
        }
        return mRawTransaction;
    }

    /***
     * This method is used to send the transaction on the block chain server and
     * send back the transaction id to the user.
     *
     * @param mBitCoinTransaction
     * @return
     */
    private void sendTransaction(String mBitCoinTransaction) {
        if (mBitCoinTransaction.isEmpty())
            throw new NullPointerException(SDKErrors.BIT_COIN_TRANSACTION_NULL);
        if (mContext != null && SDKUtils.isNetworkAvailable(mContext)) {
            new EOTCoinTransactionBroadcast(this).broadCastTransaction(mBitCoinTransaction);
        }
    }

    @Override
    public void TransactionBroadcastSuccess(String mTransactionId, int mWallet_id) {
        if (mTransactionBuilder != null) {
            mTransactionBuilder.TransactionId(mTransactionId);
            getBalance(null);
        }
    }

    @Override
    public void TransactionBroadcastFailure(VolleyError mTransactionError) {
        if (mTransactionBuilder != null) {
            mTransactionBuilder.TransactionFailed(SDKErrors.TRANSACTION_FAILED_ERROR);
        }
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

    /**
     * This method of Wallet class is used to get the balance of the wallet address
     */
    private void getWalletUnspentCount(WalletUnspentCountHandler mWalletUnspentCountHandler) throws SecureSDKException {
        // Call the API from the insight server and get the balance info the particular
        if (mContext != null) {
            if (SDKUtils.isNetworkAvailable(mContext)) {
                new EotWalletUnspentCount(mWalletUnspentCountHandler).checkMyWallet(wallet_address);
            }
        }
    }

    /****
     * This method is used to fetch the transaction history from the block chain
     * @param mTransactionHistoryCallback
     */
    public void getWalletTransactionsHistory(TransactionHistoryCallback mTransactionHistoryCallback) {
        try {
            if (mEotDatabaseHandler != null) {
                EotKeyPair mEotKeyPair = mEotDatabaseHandler.getEotWallet();
                String mEotAddress = mEotKeyPair.address;
                new EotTransactionsHistoryHandler(mTransactionHistoryCallback).getHistory(mEotAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
