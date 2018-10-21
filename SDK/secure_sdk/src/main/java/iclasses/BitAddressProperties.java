package iclasses;

import com.android.volley.VolleyError;

import org.json.JSONArray;

/**
 * Created by Deepak on 4/11/2017.
 */

public interface BitAddressProperties {
    public void AddressPropertiesSuccess(JSONArray mJsonArray);

    public void AddressPropertiesFailure(VolleyError mVolleyError);
}
