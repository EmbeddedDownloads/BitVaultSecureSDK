package com.embedded.sdkdemo;

import android.content.Intent;
import android.os.Bundle;

import com.embedded.wallet.BitVaultActivity;

import iclasses.UserAuthenticationCallback;

public class MainActivity extends BitVaultActivity implements UserAuthenticationCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validateUser(this,this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onAuthenticationSuccess() {
        startActivity(new Intent(this, FirstActivity.class));
    }
}
