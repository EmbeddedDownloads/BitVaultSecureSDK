package iclasses;/**
 * Created by ${e} on 5/30/2017.
 */

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;

/**********************************************************************
 * Embedded Downloads
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * Embedded Downloads. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Embedded Downloads.
 ********************************************************************/
public interface TransactionHistoryCallback {
    public void transactionHistorySuccess(JSONObject mHistoryResponse, String wallet_address);
    public void allWalletsTransactionHistory(JSONObject mHistoryResponse, ArrayList<String> mWalletsList);
    public void transactionHistoryFailed(VolleyError mVolleyError);
}
