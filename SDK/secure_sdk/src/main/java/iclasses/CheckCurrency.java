package iclasses;/**
 * Created by Deepak on 5/12/2017.
 */

import com.android.volley.VolleyError;

/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public interface CheckCurrency {
    public void checkCurrency(String mBtc);
    public void conversionError(VolleyError mError);
}
