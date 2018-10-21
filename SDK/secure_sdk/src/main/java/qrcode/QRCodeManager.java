package qrcode;/**
 * Created by ${e} on 5/23/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import bitmanagers.BitVaultBaseManager;
import commons.SecureSDKException;

/**********************************************************************
 * Embedded Downloads
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * Embedded Downloads. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Embedded Downloads.
 ********************************************************************/
public class QRCodeManager {
    /***
     * This method is used to generate the QR Code for given address
     *
     * @param mContent
     * @return
     */
//    private static final String SCHEME_BITCOIN = "bitcoin:";
    public Bitmap showQRCodePopupForAddress(final String mContent) {
        Bitmap mQRBitMap = null;
        QRCodeWriter writer = new QRCodeWriter();
        Context mContext = null;
        try {
            try {
                mContext = BitVaultBaseManager.getInstance().getContext();
            } catch (SecureSDKException e) {
                e.printStackTrace();
            }
            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            BitMatrix bitMatrix = writer.encode(mContent, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            mQRBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    mQRBitMap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return mQRBitMap;
    }
}
