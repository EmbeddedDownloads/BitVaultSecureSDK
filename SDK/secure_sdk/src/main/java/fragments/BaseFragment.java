package fragments;/**
 * Created by ${e} on 5/29/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.embedded.wallet.BitVaultActivity;

/**********************************************************************
 * Embedded Downloads
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * Embedded Downloads. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Embedded Downloads.
 ********************************************************************/
public class BaseFragment extends Fragment {
    // instance of main tab activity to be used globally into the inner classes
    public static BitVaultActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BitVaultActivity) this.getActivity();
    }

    public boolean onBackPressed() {
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
