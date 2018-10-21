package com.embedded.sdkdemo;/**
 * Created by ${e} on 5/24/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import adapters.WalletsLoaderAdapter;
import bitmanagers.BitVaultWalletManager;
import commons.SecureSDKException;
import iclasses.TransactionBuilder;
import model.WalletDetails;
import qrcode.ScanQRCode;
import utils.SDKUtils;
import valle.btc.BTCUtils;

/**********************************************************************
 * Embedded Downloads
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * Embedded Downloads. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Embedded Downloads.
 ********************************************************************/
public class BitCoinTransaction extends Activity
        implements View.OnClickListener, TransactionBuilder, AdapterView.OnItemSelectedListener {
    private EditText recipientAddress, bitcoinsAmount;
    private Button btnScan, btnSendTran;
    private final int REQUEST_SCAN_PRIVATE_KEY = 0;
    private String TAG = BitCoinTransaction.class.getSimpleName();
    private TextView responseTxt;
    private Spinner walletsSpinner = null;
    private WalletsLoaderAdapter mWalletsLoaderAdapter = null;
    private ArrayList<WalletDetails> mWalletsList = null;
    private int WALLET_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bitcoins_transactions);
        initViews();
        initObjects();
        onClickManager();

    }

    private void onClickManager() {
        btnScan.setOnClickListener(this);
        btnSendTran.setOnClickListener(this);
        walletsSpinner.setOnItemSelectedListener(this);
    }

    private void initViews() {
        recipientAddress = (EditText) findViewById(R.id.recipientAddress);
        bitcoinsAmount = (EditText) findViewById(R.id.bitcoinsAmount);
        btnScan = (Button) findViewById(R.id.btnScan);
        btnSendTran = (Button) findViewById(R.id.btnSendTran);
        responseTxt = (TextView) findViewById(R.id.responseTxt);
        walletsSpinner = (Spinner) findViewById(R.id.walletsSpinner);
    }

    /***
     * Initialize Objects of this class
     */
    private void initObjects() {
        try {
            mWalletsList = BitVaultWalletManager.getWalletInstance().getWallets(null);
            mWalletsLoaderAdapter = new WalletsLoaderAdapter(this, mWalletsList);
            walletsSpinner.setAdapter(mWalletsLoaderAdapter);
        } catch (SecureSDKException e) {
            e.printStackTrace();
        }
    }


    public void startQrScaning() {
        startActivityForResult(new Intent(this, ScanQRCode.class), REQUEST_SCAN_PRIVATE_KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String scannedResult = data.getStringExtra("data");
            SDKUtils.showLog(TAG, "-----scannedResult---" + scannedResult);
//            responseTxt.setText(scannedResult);
            recipientAddress.setText(scannedResult);
        }
    }


    public void createBitCoinTransaction() {
        String mAmountToSend="0.0";
        if (BitVaultWalletManager.getWalletInstance() != null) {
            // From which wallet bitcoins to send
            // Recipient address
            // Amount to be send
            // Mining fee of the transaction
            if (!responseTxt.getText().toString().equalsIgnoreCase("")) {
                if (!bitcoinsAmount.getText().toString().equalsIgnoreCase("")){
                    mAmountToSend = bitcoinsAmount.getText().toString();
                }else{
                    SDKUtils.showToast(this,"0.0 Bit coins can not be sent");
                    return;
                }
                BitVaultWalletManager.getWalletInstance().sendBitCoins(WALLET_ID, recipientAddress.getText().toString().trim(), BTCUtils.parseValue(mAmountToSend),
                        BTCUtils.parseValue("0.0001"), this);
            } else {
                SDKUtils.showToast(this, "Receiver's Address can not be empty");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendTran:
                createBitCoinTransaction();
                break;
            case R.id.btnScan:
                startQrScaning();
                break;
        }
    }

    @Override
    public void RequestedTransaction(String mTransaction) {
        responseTxt.setText("");
        responseTxt.setText("Raw Transactions : " + mTransaction);
    }

    @Override
    public void TransactionId(String mTxId) {
        responseTxt.setText("");
        responseTxt.setText("Transaction sent : " + mTxId);
    }

    @Override
    public void TransactionFailed(String error) {
        responseTxt.setText("");
        responseTxt.setText("Please try again : " + error);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SDKUtils.showLog(TAG, "-----Wallet Position --------" + position);
        WALLET_ID = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
