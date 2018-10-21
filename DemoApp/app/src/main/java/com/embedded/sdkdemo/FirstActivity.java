package com.embedded.sdkdemo;/**
 * Created by ${e} on 5/17/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.bitcoinj.crypto.MnemonicException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import bitmanagers.BitVaultWalletManager;
import bitmanagers.EOTWalletManager;
import commons.SecureSDKException;
import iclasses.EotCallbacks.EotWalletCallback;
import iclasses.RecoverWalletSeedListener;
import iclasses.TransactionBuilder;
import iclasses.TransactionHistoryCallback;
import iclasses.VaultDetailsCallback;
import iclasses.WalletArrayCallback;
import iclasses.WalletBalanceCallback;
import iclasses.WalletCallback;
import iclasses.WalletType;
import iclasses.WalletUnspentCountHandler;
import model.VaultDetails;
import model.WalletBalanceModel;
import model.WalletDetails;
import model.eotmodel.EotWalletDetail;
import qrcode.QRCodeManager;
import qrcode.ScanQRCode;
import utils.SDKUtils;
import valle.btc.BTCUtils;

import static com.embedded.sdkdemo.R.id.mCodeCodeImage;

/**********************************************************************
 * Embedded Downloads
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * Embedded Downloads. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Embedded Downloads.
 ********************************************************************/
public class FirstActivity extends Activity implements
        WalletCallback, WalletArrayCallback, WalletUnspentCountHandler, TransactionBuilder {
    private String TAG = FirstActivity.class.getSimpleName();
    private static final int REQUEST_SCAN_PRIVATE_KEY = 0;
    private ImageView mQRCodeImage = null;
    private TextView textOut = null;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private int WRITE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
        checkRunTimePermission();
    }

    /***
     * This method is used to check the permission of the external storage.
     */
    private void checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    EOTWalletManager.getEOTWalletInstance();
                }
                break;
        }
    }

    private void initView() {
        mQRCodeImage = (ImageView) findViewById(mCodeCodeImage);
        textOut = (TextView) findViewById(R.id.textOut);
    }

    public void getWallet(View mView) {
        if (BitVaultWalletManager.getWalletInstance() != null) {
            try {
                BitVaultWalletManager.getWalletInstance().getWallet(1, WalletType.BIT_COIN_TESTNET, this);
            } catch (SecureSDKException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateQRCode(View v) {
        Bitmap bitmap = new QRCodeManager().showQRCodePopupForAddress("mv3cLbUnNbiqihDHGMVHHHuL7DncjBtZ4F");
        if (mQRCodeImage != null && bitmap != null) {
            mQRCodeImage.setImageBitmap(bitmap);
            mQRCodeImage.setVisibility(View.VISIBLE);
            textOut.setVisibility(View.GONE);
        }
    }

    public void getAllWallets(View mView) {
        if (BitVaultWalletManager.getWalletInstance() != null) {
            try {
                textOut.setText("");
                BitVaultWalletManager.getWalletInstance().getWallets(this);
            } catch (SecureSDKException e) {
                e.printStackTrace();
            }
        }
    }

    public void getUnspentCount(View v) {
        if (BitVaultWalletManager.getWalletInstance() != null) {
            try {
                BitVaultWalletManager.getWalletInstance().getWalletBalance(2, new WalletBalanceCallback() {
                    @Override
                    public void successWalletBalanceCallback(WalletBalanceModel mWalletBalanceModel) {

                    }

                    @Override
                    public void failedWalletBalanceCallback(VolleyError mError) {

                    }
                });
            } catch (SecureSDKException e) {
                e.printStackTrace();
            }
        }
    }

    public void getEotWallet(View v) {
        EOTWalletManager.getEOTWalletInstance().getEotWallet(new EotWalletCallback() {
            @Override
            public void eotWallet(EotWalletDetail mEotWalletDetail) {
                SDKUtils.showLog(TAG, "-----Wallet Address-----" + mEotWalletDetail.getAddress());
                SDKUtils.showLog(TAG, "-----Wallet Public Key-----" + mEotWalletDetail.getPublic_key());
            }
        });
    }

    public void getEotSeed(View v) {
        EOTWalletManager.getEOTWalletInstance().showWalletSeed(new RecoverWalletSeedListener() {
            @Override
            public void walletSeedSuccess(String walletSeed, Bitmap seedQrcode) {
                SDKUtils.showLog(TAG, "-------Wallet Seed-----" + walletSeed);
            }

            @Override
            public void walletSeedFailed(String failed) {

            }
        });
    }

    public void createBitCoinTransaction(View v) {
        if (BitVaultWalletManager.getWalletInstance() != null) {
            // From which wallet bitcoins to send
            // Recipient address
            // Amount to be send
            // Mining fee of the transaction
            BitVaultWalletManager.getWalletInstance().getVault(new VaultDetailsCallback() {
                @Override
                public void vaultDetails(VaultDetails mVaultDetails) {
                    SDKUtils.showLog(TAG, "---Vault Addreess---" + mVaultDetails.getmKeyPair().address);
                }
            });
            BitVaultWalletManager.getWalletInstance().sendBitCoins(5, "mzK1EAyPs6N2u7HrRcZ1TbDouCFNzSVaeD", BTCUtils.parseValue("0"),
                    0, this);
        }
    }

    public void sendEot(View v) {
        if (EOTWalletManager.getEOTWalletInstance() != null) {
            EOTWalletManager.getEOTWalletInstance().transferEot("receivers_address", "amount_to_send", "priority_fee", null);
        }
    }

    public void restoreEotWallet(View v) {
        if (EOTWalletManager.getEOTWalletInstance() != null) {
            try {
                EOTWalletManager.getEOTWalletInstance().verifySeedAndGetWallet("passphrase_seed", new EotWalletCallback() {
                    @Override
                    public void eotWallet(EotWalletDetail mEotWalletDetail) {

                    }
                });
            } catch (MnemonicException e) {
                e.printStackTrace();
            }
        }
    }

    public void getEotWalletHistory(View v) {
        if (EOTWalletManager.getEOTWalletInstance() != null) {
            EOTWalletManager.getEOTWalletInstance().getWalletTransactionsHistory(new TransactionHistoryCallback() {
                @Override
                public void transactionHistorySuccess(JSONObject mHistoryResponse, String wallet_address) {

                }

                @Override
                public void allWalletsTransactionHistory(JSONObject mHistoryResponse, ArrayList<String> mWalletsList) {

                }

                @Override
                public void transactionHistoryFailed(VolleyError mVolleyError) {

                }
            });
        }
    }

    public void emptyAllWalletsToVault(View v) {
        if (BitVaultWalletManager.getWalletInstance() != null) {
            BitVaultWalletManager.getWalletInstance().emptyAllWalletsToVault(this);
        }
    }

    public void getEotBalance(View v) {
        EOTWalletManager.getEOTWalletInstance().getBalance(new WalletBalanceCallback() {
            @Override
            public void successWalletBalanceCallback(WalletBalanceModel mWalletBalanceModel) {
                SDKUtils.showLog(TAG, "---EOT Wallet Balance-----" + mWalletBalanceModel.getmWalletBalance());
                SDKUtils.showLog(TAG, "---EOT Wallet Balance-----" + mWalletBalanceModel.getmWalletAddress());
            }

            @Override
            public void failedWalletBalanceCallback(VolleyError mError) {

            }
        });
    }

    public void sendVaultToWallet(View v) {
        if (BitVaultWalletManager.getWalletInstance() != null) {
            try {
                BitVaultWalletManager.getWalletInstance().sendBitcoinsVaultToWalllet(1, BTCUtils.parseValue("0.5"), BTCUtils.parseValue("0.00001"),
                        this);
            } catch (SecureSDKException e) {
                e.printStackTrace();
            }
        }
    }

    public void startQrScaning(View view) {
        startActivityForResult(new Intent(this, ScanQRCode.class), REQUEST_SCAN_PRIVATE_KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String scannedResult = data.getStringExtra("data");
            SDKUtils.showLog(TAG, "-----scannedResult---" + scannedResult);
            hideImageView();
            textOut.setVisibility(View.VISIBLE);
            textOut.setText("");
            textOut.setText("Scanned Result : " + scannedResult);
        }
    }

    @Override
    public void getWallet(WalletDetails mWalletDetails) {
        if (mWalletDetails != null) {
            SDKUtils.showLog(TAG, "Wallet name : " + mWalletDetails.getWALLET_NAME());
            SDKUtils.showLog(TAG, "Wallet ID : " + mWalletDetails.getWALLET_ID());
            SDKUtils.showLog(TAG, "Wallet Address : " + mWalletDetails.getmKeyPair().address);
            hideImageView();
            textOut.setVisibility(View.VISIBLE);
            textOut.setText("");
            textOut.setText("Wallet Address " + mWalletDetails.getmKeyPair().address);
        }
    }

    @Override
    public void getWallets(ArrayList<WalletDetails> mRequestedWallets) {
        if (mRequestedWallets != null) {
            for (int i = 0; i < mRequestedWallets.size(); i++) {
                WalletDetails mWalletDetails = mRequestedWallets.get(i);
                if (mWalletDetails != null) {
                    SDKUtils.showLog(TAG, "Wallet name : " + mWalletDetails.getWALLET_NAME());
                    SDKUtils.showLog(TAG, "Wallet ID : " + mWalletDetails.getWALLET_ID());
                    SDKUtils.showLog(TAG, "Wallet Address : " + mWalletDetails.getmKeyPair().address);
                    SDKUtils.showLog(TAG, "Wallet Balance : " + mWalletDetails.getWALLET_LAST_UPDATE_BALANCE());

                    hideImageView();
                    textOut.setVisibility(View.VISIBLE);
//                    textOut.setText("");
                    textOut.append("Wallet " + i + "   " + mWalletDetails.getmKeyPair().address + "\n");
                }
            }
        }
    }

    private void hideImageView() {
        mQRCodeImage.setVisibility(View.GONE);
    }

    @Override
    public void walletUnspentCountSuccess(JSONArray mJsonArrayResponse) {
        SDKUtils.showLog(TAG, "Wallet Response Success : " + mJsonArrayResponse);
        hideImageView();
        textOut.setVisibility(View.VISIBLE);
        textOut.setText("");
        textOut.setText(mJsonArrayResponse.toString());
    }

    @Override
    public void walletUnspentCountFailure(VolleyError mErrorResponse) {
        SDKUtils.showLog(TAG, "Wallet Response Failure : " + mErrorResponse);
    }

    @Override
    public void RequestedTransaction(final String mTransaction) {
        SDKUtils.showLog(TAG, "Raw Transaction : " + mTransaction);
        hideImageView();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textOut.setVisibility(View.VISIBLE);
                textOut.setText("");
                textOut.setText(mTransaction.toString());
            }
        });
    }

    @Override
    public void TransactionId(final String mTxId) {
        hideImageView();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textOut.setVisibility(View.VISIBLE);
                textOut.setText("");
                textOut.setText("Transaction Id : " + mTxId.toString());
            }
        });
    }

    @Override
    public void TransactionFailed(final String error) {
        hideImageView();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textOut.setVisibility(View.VISIBLE);
                textOut.setText("");
                textOut.setText("Please try again : " + error);
            }
        });
    }
}
