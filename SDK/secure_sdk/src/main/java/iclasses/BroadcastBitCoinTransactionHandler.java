package iclasses;/**
 * Created by Deepak on 4/12/2017.
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
public interface BroadcastBitCoinTransactionHandler {
    public void TransactionBroadcastSuccess(String mTransactionId, int mWallet_id);

    public void TransactionBroadcastFailure(VolleyError mTransactionError);

}
