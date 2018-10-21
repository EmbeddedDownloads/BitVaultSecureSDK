package fragments;/**
 * Created by ${e} on 5/29/2017.
 */

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.embedded.wallet.BitVaultActivity;
import com.embedded.wallet.R;

/**********************************************************************
 * Embedded Downloads
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * Embedded Downloads. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Embedded Downloads.
 ********************************************************************/
public class FingerPrintAuth extends BaseFragment implements View.OnClickListener {
    private View mView = null;
    private BitVaultActivity mBitVaultActivityInstance=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_fingerprint, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mBitVaultActivityInstance = (BitVaultActivity) getActivity();
        return mView;
    }
    @Override
    public void onClick(View v) {
        mBitVaultActivityInstance.validateIris();
    }
}
