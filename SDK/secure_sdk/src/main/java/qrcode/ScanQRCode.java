package qrcode;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;

import com.embedded.wallet.R;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

import commons.SDKHelper;
import iclasses.AlertViewCallback;
import utils.SDKUtils;

/**
 * Created by ${e} on 6/23/2017.
 */

public class ScanQRCode extends Activity implements AlertViewCallback {
    private String TAG = ScanQRCode.class.getSimpleName();
    private DecoratedBarcodeView mQRCodeScannarView = null;
    private String[] permissions = {Manifest.permission.CAMERA};
    private int WRITE_REQUEST_CODE = 1;
    private int ZERO=0;
    private Vibrator vibrator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);
        checkRunTimePermission();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mQRCodeScannarView!=null){
            mQRCodeScannarView.resume();
        }
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
                    initViews();
                } else {
                    SDKUtils.alertView(this, this.getResources().getString(R.string.app_name),
                            this.getResources().getString(R.string.denied_db_permission),
                            this.getResources().getString(R.string.yes), this.getResources().getString(R.string.no), ZERO, this);
                }
                break;
        }
    }


    /*********************************************************
     * @Method Name: initViews
     * @Description: This method is used to get the reference
     * of xml views from this layout
     ***********************************************************/
    private void initViews() {
        mQRCodeScannarView = (DecoratedBarcodeView) findViewById(R.id.mQRCodeScannarView);
        if (mQRCodeScannarView!=null){
            mQRCodeScannarView.decodeContinuous(mResultCallback);
            mQRCodeScannarView.resume();
            mQRCodeScannarView.requestFocus();
        }
    }

    private static final long VIBRATE_DURATION = 50L;
    private BarcodeCallback mResultCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result != null) {
                   /*Enable Wifi After Bar code Scan*/
                vibrator.vibrate(VIBRATE_DURATION);
                String resultData = result.getText();
                SDKUtils.showLog(TAG, "QR Code Scanning Result : " + resultData);
                setResult(RESULT_OK, new Intent().putExtra(SDKHelper.KEY_DATA, resultData));
                finish();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (mQRCodeScannarView!=null)
        mQRCodeScannarView.pause();
    }

    @Override
    public void PositiveButtonPressed(int mCommand) {
        finish();
    }

    @Override
    public void NegativeButtonPressed(int mCommand) {
        if (mCommand == ZERO)
            requestPermissions(permissions, WRITE_REQUEST_CODE);
    }
}
