package iclasses.iclasseseot;

import com.android.volley.VolleyError;

import model.eotmodel.EotPriceConverterModel;

/**
 * Created by vvdn on 11/1/2017.
 */

public interface EotPriceConverterListener {
    public void EotPriceConversionSuccess(EotPriceConverterModel eotPriceConverterModel);

    public void EotPriceConverterFailed(VolleyError mError);
}
