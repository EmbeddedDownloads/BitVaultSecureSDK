package authentication;/**
 * Created by Deepak on 4/13/2017.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.embedded.wallet.R;


/**********************************************************************
 * VVDN Technologies
 * All rights reserved.
 * This software is the confidential and proprietary information of
 * VVDN Technologies. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with VVDN.
 ********************************************************************/
public class PasswordAuth extends RelativeLayout {
    private LayoutInflater mInflater;

    public PasswordAuth(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        initializeView();
    }

    public PasswordAuth(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        initializeView();
    }

    public PasswordAuth(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInflater = LayoutInflater.from(context);
        initializeView();
    }

    public PasswordAuth(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mInflater = LayoutInflater.from(context);
        initializeView();
    }

    /**
     * This method is used to initialize the xml view on the screen
     */
    private void initializeView() {
        View v = mInflater.inflate(R.layout.sdk_password_auth, this, true);
    }
}
