package iclasses;

import com.android.volley.VolleyError;

/**
 * Created by Deepak on 5/11/2017.
 */

public interface CurrencyConvertorCallback {
    public void USDtoBTC(String btc);
    public void BTCtoUSD(String usd);
    public void ConversionFailed(VolleyError mError);
}
