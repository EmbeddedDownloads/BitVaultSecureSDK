package currencyconvertor;/**
 * Created by Deepak on 5/11/2017.
 */

import com.android.volley.VolleyError;

import commons.SDKErrors;
import commons.SecureSDKException;
import iclasses.CheckCurrency;
import iclasses.CurrencyConvertorCallback;
import webservicescontroller.CurrencyLocator;

/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public class CurrencyConvertor implements CheckCurrency {
    private String TAG = CurrencyConvertor.class.getSimpleName();
    private CurrencyConvertorCallback mCurrencyConvertorCallback = null;
    /**
     * This class object is used to verify currency
     */
    private CurrencyLocator mCurrencyLocator = null;
    /**
     * This String variable is used to get the value of the one USD
     */
    private String mOneUSDValue = "0.0";
    /**
     * This variable is used to keep track of the track from usd to btc
     */
    private String USDtoBTC = "usdtobtc";

    /**
     * This variable is used to keep track of the btc to usd
     */
    private String BTCtoUSD = "btctousd";
    /**
     * This variable is used to keep track of the current conversion tag
     */
    private String currentConversionTag = "currentTag";
    /**
     * This string variable is used to hold the amount for conversion.
     */
    private String mAmount = "";

    /**
     * This class constructor is used to get callback method to return the status
     * of the currency after conversion.
     *
     * @param mCurrencyConvertorCallback
     */

    public CurrencyConvertor(CurrencyConvertorCallback mCurrencyConvertorCallback) {
        this.mCurrencyConvertorCallback = mCurrencyConvertorCallback;
    }


    /**
     * This method is used to convert the currency value USD to BTC
     *
     * @param USD
     */
    public void convertUSDToBTC(String USD) throws SecureSDKException {
        mCurrencyLocator = new CurrencyLocator(this);
        mCurrencyLocator.checkCurrenty();
        verifyCurrencyValue(USD); // Verify value of the currency
        currentConversionTag = USDtoBTC;
        mAmount = USD;
        finalUSDtoBTC();
    }

    /***
     * This method is used to convert usd to btc and update to UI
     */
    private void finalUSDtoBTC() {
        if (mCurrencyConvertorCallback != null) {
            float USD_int = Float.parseFloat(mOneUSDValue);
            float USD_toConvert = Float.parseFloat(mAmount);
            float final_btc = USD_toConvert * USD_int;
            mCurrencyConvertorCallback.USDtoBTC(final_btc + "");
        }
    }

    /***
     * This method is used to convert btc to usd and update on the UI
     */
    private void finalBTCtoUSD() {
        if (mCurrencyConvertorCallback != null) {
            float USD_int = Float.parseFloat(mOneUSDValue);
            float BTC_toConvert = Float.parseFloat(mAmount);
            float final_usd = BTC_toConvert / USD_int;
            mCurrencyConvertorCallback.BTCtoUSD(final_usd + "");
        }
    }

    /**
     * This method is used to convert the currency value BTC to USD
     *
     * @param BTC
     */
    public void convertBTCtoUSD(String BTC) throws SecureSDKException {
        mCurrencyLocator = new CurrencyLocator(this);
        verifyCurrencyValue(BTC); // Verify value of the currency
        mCurrencyLocator.checkCurrenty();
        currentConversionTag = BTCtoUSD;
        mAmount = BTC;

    }

    /***
     * This method is used to
     *
     * @param mValue
     */
    private void verifyCurrencyValue(String mValue) {
        if (mValue == null && mValue.equalsIgnoreCase("") && mValue.equalsIgnoreCase("0"))
            throw new NullPointerException(SDKErrors.CURRENCY_FOR_CONVERSION_NULL);
    }


    @Override
    public void checkCurrency(String mBtc) {
        mOneUSDValue = mBtc;
        if (currentConversionTag.equalsIgnoreCase(USDtoBTC)) {
            // Convert currency from usd to btc
            finalUSDtoBTC();
        } else {
            // convert currency from btc to usd
            finalBTCtoUSD();
        }
    }

    @Override
    public void conversionError(VolleyError mError) {
        if (mCurrencyConvertorCallback != null) {
            mCurrencyConvertorCallback.ConversionFailed(mError);
        }
    }
}
