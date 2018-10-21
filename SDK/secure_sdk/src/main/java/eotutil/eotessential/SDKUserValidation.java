package eotutil.eotessential;

import android.os.Bundle;

import com.embedded.wallet.BitVaultActivity;

import commons.SDKConstants;
import iclasses.UserAuthenticationCallback;

/**
 * Created by vvdn on 9/22/2017.
 */

public class SDKUserValidation extends BitVaultActivity implements UserAuthenticationCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validateUser(this, this);
    }

    @Override
    public void onAuthenticationSuccess() {
        if (SDKConstants.mUserAuthenticationCallback != null) {
            finish();
            SDKConstants.mUserAuthenticationCallback.onAuthenticationSuccess();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
