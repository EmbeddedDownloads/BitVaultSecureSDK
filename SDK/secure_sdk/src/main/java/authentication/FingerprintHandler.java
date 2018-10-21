package authentication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;

import com.embedded.wallet.R;

import commons.SDKErrors;
import iclasses.FingerPrintManagerCallbacks;


/**
 * This class is used to validating the finger print auth data,
 * if the user is valid or not
 */
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    /**
     * This class object is used to track all the finger print callbacks
     */
    private FingerPrintManagerCallbacks mFingerPrintManagerCallbacks = null;
    /**
     * Application context to get reference of the objects
     */
    private Context context;

    /**
     * Default constructor of this class
     *
     * @param mContext
     * @param mFingerPrintManagerCallbacks
     */
    public FingerprintHandler(Context mContext, FingerPrintManagerCallbacks mFingerPrintManagerCallbacks) {
        context = mContext;
        this.mFingerPrintManagerCallbacks = mFingerPrintManagerCallbacks;
    }

    /**
     * This method is used to start the authentication process by scanning the
     * finger print data.
     *
     * @param manager
     * @param cryptoObject
     */
    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.update(/*SDKErrors.FINGER_PRINT_AUTH_ERROR + */errString.toString());
        if (mFingerPrintManagerCallbacks != null)
            mFingerPrintManagerCallbacks.onAuthenticationError(errMsgId, errString);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        this.update(/*SDKErrors.FINGER_PRINT_AUTH_HELP + */helpString.toString());
        if (mFingerPrintManagerCallbacks != null) {
            mFingerPrintManagerCallbacks.onAuthenticationHelp(helpMsgId, helpString);
        }
    }


    @Override
    public void onAuthenticationFailed() {
        this.update(SDKErrors.FINGER_PRINT_AUTH_FAILED);
        if (mFingerPrintManagerCallbacks != null) {
            mFingerPrintManagerCallbacks.onAuthenticationFailed();
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        if (mFingerPrintManagerCallbacks != null) {
            mFingerPrintManagerCallbacks.onAuthenticationSucceeded(result);
        }
    }

    /***
     * This method is used to update the message on the screen according to the
     * response getting from the android callbacks
     *
     * @param mString
     */
    private void update(String mString) {
        try {
            if (context != null) {
                final TextView textView = (TextView) ((Activity) context).findViewById(R.id.mAuthText);
                if (textView != null) {
                    textView.setTextColor(context.getResources().getColor(R.color.color_error));
                    textView.setText(mString);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textView.setTextColor(context.getResources().getColor(R.color.white));
                            textView.setText(context.getResources().getString(R.string.put_your_finger_on_scanner));
                        }
                    }, 5000);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
